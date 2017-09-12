package com.bsmwireless.screens.lockscreen;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class LockScreenPresenter {

    final LockScreenView mView;
    final DutyTypeManager mDutyManager;
    final BlackBoxConnectionManager connectionManager;
    private final CompositeDisposable mCompositeDisposable;
    private final PreferencesManager preferencesManager;
    final BlackBoxStateChecker checker;
    private final long blackBoxTimeoutMillis;
    private final long mIdlingTimeoutMillis;
    private final AccountManager accountManager;
    private volatile CurrentWork currentWork;
    private final AtomicReference<Maybe<BlackBoxModel>> generalMonitoringReference;
    private final AtomicReference<Completable> reconnectionReference;
    private final AtomicReference<Completable> idleMonitoringCompletableReference;
    private final AtomicReference<Maybe<BlackBoxModel>> monitoringDrivingReference;

    @Inject
    public LockScreenPresenter(LockScreenView view,
                               DutyTypeManager dutyManager,
                               BlackBoxConnectionManager connectionManager,
                               PreferencesManager preferencesManager,
                               BlackBoxStateChecker checker,
                               @Named("disconnectTimeout") long blackBoxTimeoutMillis,
                               @Named("idleTimeout") long idlingTimeout, AccountManager accountManager) {
        mView = view;
        mDutyManager = dutyManager;
        this.connectionManager = connectionManager;
        this.preferencesManager = preferencesManager;
        this.checker = checker;
        this.blackBoxTimeoutMillis = blackBoxTimeoutMillis;
        this.mIdlingTimeoutMillis = idlingTimeout;
        this.accountManager = accountManager;
        mCompositeDisposable = new CompositeDisposable();
        currentWork = CurrentWork.NOTHING;
        generalMonitoringReference = new AtomicReference<>();
        reconnectionReference = new AtomicReference<>();
        idleMonitoringCompletableReference = new AtomicReference<>();
        monitoringDrivingReference = new AtomicReference<>();
    }

    public void onStart() {

        mView.setTimeForDutyType(DutyType.DRIVING, mDutyManager.getDutyTypeTime(DutyType.DRIVING));
        mView.setTimeForDutyType(DutyType.SLEEPER_BERTH, mDutyManager.getDutyTypeTime(DutyType.SLEEPER_BERTH));
        mView.setTimeForDutyType(DutyType.ON_DUTY, mDutyManager.getDutyTypeTime(DutyType.ON_DUTY));
        mView.setTimeForDutyType(DutyType.OFF_DUTY, mDutyManager.getDutyTypeTime(DutyType.OFF_DUTY));

        startTimer();
        startMonitoring();
        accountManager.addListener(accountListener);
    }

    public void onStop() {
        mCompositeDisposable.clear();
        resetTime();
        accountManager.removeListener(accountListener);
    }

    public void switchCoDriver() {
        mView.openCoDriverDialog();
    }

    private void resetTime() {
        mDutyManager.setDutyType(DutyType.DRIVING, true);
    }

    private void startTimer() {
        final Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
                .map(interval -> mDutyManager.getDutyTypeTime(DutyType.DRIVING))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::setCurrentTime, throwable -> Timber.d(throwable, "Timer error"));
        mCompositeDisposable.add(disposable);
    }

    /**
     * Start the monitoring statuses task.
     */
    void startMonitoring() {

        switch (currentWork) {
            case NOTHING:
            case GENERAL_MONITORING:
                startGeneralMonitoring();
                break;

            case DRIVING_MONITORING:
                startMonitoringDriving();
                break;

            case IDLE_MONITORING:
                startIdlingMonitoring();
                break;

            case RECONNECTING:
                startReconnection();
                break;

            default:
                throw new IllegalStateException("Unknown current work's type - " + currentWork);
        }
    }

    void startGeneralMonitoring() {
        Maybe<BlackBoxModel> maybe = generalMonitoringReference.get();
        if (maybe == null) {
            maybe = connectionManager.getDataObservable()
                    .filter(blackBoxModel ->
                            checker.isIgnitionOff(blackBoxModel) || checker.isStopped(blackBoxModel)) // TODO: Check this filter
                    .firstElement()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
            if (!generalMonitoringReference.compareAndSet(null, maybe)) {
                maybe = generalMonitoringReference.get();
            }
        }

        final Disposable disposable = maybe
                .doOnSubscribe(unused -> {  // must be lower subscribeOn() in the current chain
                    mView.removeAnyPopup();
                    currentWork = CurrentWork.GENERAL_MONITORING;
                })
                .doFinally(() -> generalMonitoringReference.set(null))
                .subscribe(blackBoxModel -> {

                    switch (blackBoxModel.getResponseType()) {
                        case IGNITION_OFF:
                            // change duty status to on duty- popup is there on screen that ignition
                            // off is detected do you want to go off duty- on-duty
                            mView.showIgnitionOffDetectedDialog();
                            startMonitoringDriving();
                            break;

                        case STOPPED:
                            // idling for X minutes triggers unlock off screen with on duty status.(no popup)
                            startIdlingMonitoring();
                            break;

                        default:
                            break;
                    }

                }, throwable -> {
                    Timber.e(throwable, "Error getting a black box's data");
                    startReconnection();
                }, mView::closeLockScreen); // BB is disconnected
        mCompositeDisposable.add(disposable);
    }

    /**
     * Try to reconnect. If done success start new monitoring task again.
     * If connection is not established show popup and start
     */
    void startReconnection() {

        Completable reconnectCompletable = reconnectionReference.get();
        if (reconnectCompletable == null) {
            int boxId = preferencesManager.getBoxId();
            reconnectCompletable = connectionManager.connectBlackBox(boxId)
                    .toCompletable()
                    .timeout(blackBoxTimeoutMillis, TimeUnit.MILLISECONDS)
                    .onErrorResumeNext(throwable -> {
                        if (throwable instanceof TimeoutException) {
                            // If timeout is occurs show disconnection popup and continue monitoring
                            return Completable.fromAction(mView::showDisconnectionPopup)
                                    .observeOn(Schedulers.io())
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .andThen(connectionManager.connectBlackBox(boxId))
                                    .toCompletable();
                        }
                        return Completable.error(throwable);
                    })
                    .cache();
            if (!reconnectionReference.compareAndSet(null, reconnectCompletable)) {
                reconnectCompletable = reconnectionReference.get();
            }
        }


        final Disposable disposable = reconnectCompletable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(unused -> currentWork = CurrentWork.RECONNECTING)
                .doFinally(() -> reconnectionReference.set(null))
                .subscribe(this::startMonitoring,
                        throwable -> mView.closeLockScreen());
        mCompositeDisposable.add(disposable);
    }

    /**
     * Detect any status from black box except
     * {@link com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel.ResponseType#STOPPED}
     * If that status will be emitted start main monitoring again.
     * Closes the Lock Screen if timeout occurs
     */
    void startIdlingMonitoring() {

        Completable idleCompletable = idleMonitoringCompletableReference.get();
        if (idleCompletable == null) {

            idleCompletable = connectionManager.getDataObservable()
                    .filter(blackBoxModel -> !checker.isStopped(blackBoxModel))
                    .timeout(mIdlingTimeoutMillis, TimeUnit.MILLISECONDS)
                    .firstElement()
                    .ignoreElement()
                    .cache();

            if (!idleMonitoringCompletableReference.compareAndSet(null, idleCompletable)) {
                idleCompletable = idleMonitoringCompletableReference.get();
            }
        }

        final Disposable disposable = idleCompletable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(unused -> currentWork = CurrentWork.IDLE_MONITORING)
                .doFinally(() -> idleMonitoringCompletableReference.set(null))
                .subscribe(this::startGeneralMonitoring, // getting a new status, start monitoring again
                        throwable -> {
                            if (throwable instanceof TimeoutException) {
                                // Didn't receive any new statuses from BB. Close the screen.
                                mView.closeLockScreen();
                            } else {
                                // Unknown error
                                // what should we do in this case?
                                Timber.d(throwable, "Idling status error");
                            }
                        });
        mCompositeDisposable.add(disposable);
    }

    void startMonitoringDriving() {

        Maybe<BlackBoxModel> drivingCompletable = monitoringDrivingReference.get();
        if (drivingCompletable == null) {

            drivingCompletable = connectionManager.getDataObservable()
                    .filter(checker::isMoving)
                    .firstElement()
                    .cache();

            if (!monitoringDrivingReference.compareAndSet(null, drivingCompletable)) {
                drivingCompletable = monitoringDrivingReference.get();
            }
        }

        final Disposable disposable = drivingCompletable
                .doOnSubscribe(unused -> currentWork = CurrentWork.DRIVING_MONITORING)
                .doFinally(() -> monitoringDrivingReference.set(null))
                .subscribe(
                        blackBoxModel -> startGeneralMonitoring(),
                        throwable -> Timber.d(throwable, "Error monitoring MOVING status"),
                        mView::closeLockScreen);// Data observable was completed for any reason. Close lock screen
        mCompositeDisposable.add(disposable);
    }

    private enum CurrentWork {
        GENERAL_MONITORING, RECONNECTING, DRIVING_MONITORING, IDLE_MONITORING, NOTHING
    }

    private final AccountManager.AccountListener accountListener = new AccountManager.AccountListener() {
        @Override
        public void onUserChanged() {
            if (!accountManager.isCurrentUserDriver()) {
                mView.closeLockScreen();
            }
        }

        @Override
        public void onDriverChanged() {

        }
    };
}
