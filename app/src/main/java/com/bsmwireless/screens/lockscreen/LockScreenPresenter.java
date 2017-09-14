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
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class LockScreenPresenter {

    final LockScreenView mView;
    final DutyTypeManager mDutyManager;
    final BlackBoxConnectionManager mBlackBoxConnectionManager;
    private final CompositeDisposable mCompositeDisposable;
    private final PreferencesManager mPreferencesManager;
    final BlackBoxStateChecker mChecker;
    private final long mBlackBoxTimeoutMillis;
    private final long mIdlingTimeoutMillis;
    private final AccountManager mAccountManager;
    private volatile CurrentWork mCurrentWork;
    private final AtomicReference<Maybe<BlackBoxModel>> mGeneralMonitoringReference;
    private final AtomicReference<Completable> mReconnectionReference;
    private final AtomicReference<Completable> mIdleMonitoringCompletableReference;
    private final AtomicReference<Maybe<BlackBoxModel>> mMonitoringDrivingReference;
    // Disposable for current monitoring task
    private Disposable mCurrentMonitoringDisposable;

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
        this.mBlackBoxConnectionManager = connectionManager;
        this.mPreferencesManager = preferencesManager;
        this.mChecker = checker;
        this.mBlackBoxTimeoutMillis = blackBoxTimeoutMillis;
        this.mIdlingTimeoutMillis = idlingTimeout;
        this.mAccountManager = accountManager;
        mCompositeDisposable = new CompositeDisposable();
        mCurrentWork = CurrentWork.NOTHING;
        mGeneralMonitoringReference = new AtomicReference<>();
        mReconnectionReference = new AtomicReference<>();
        mIdleMonitoringCompletableReference = new AtomicReference<>();
        mMonitoringDrivingReference = new AtomicReference<>();
        mCurrentMonitoringDisposable = Disposables.disposed();
    }

    public void onStart() {

        mView.setTimeForDutyType(DutyType.DRIVING, mDutyManager.getDutyTypeTime(DutyType.DRIVING));
        mView.setTimeForDutyType(DutyType.SLEEPER_BERTH, mDutyManager.getDutyTypeTime(DutyType.SLEEPER_BERTH));
        mView.setTimeForDutyType(DutyType.ON_DUTY, mDutyManager.getDutyTypeTime(DutyType.ON_DUTY));
        mView.setTimeForDutyType(DutyType.OFF_DUTY, mDutyManager.getDutyTypeTime(DutyType.OFF_DUTY));

        startTimer();
        startMonitoring();
        mAccountManager.addListener(accountListener);
    }

    public void onStop() {
        mCompositeDisposable.clear();
        resetTime();
        mAccountManager.removeListener(accountListener);
    }

    public void switchCoDriver() {
        mView.openCoDriverDialog();
    }

    public void onDutyTypeSelected(DutyType dutyType) {
        changeDutyStatus(dutyType);
        mView.closeLockScreen();
    }

    private void changeDutyStatus(DutyType dutyType) {

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

        Timber.d("startMonitoring, mCurrentWork " + mCurrentWork);

        switch (mCurrentWork) {
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
                throw new IllegalStateException("Unknown current work's type - " + mCurrentWork);
        }
    }

    void startGeneralMonitoring() {
        Maybe<BlackBoxModel> maybe = mGeneralMonitoringReference.get();
        if (maybe == null) {
            maybe = mBlackBoxConnectionManager.getDataObservable()
                    .filter(blackBoxModel ->
                            mChecker.isIgnitionOff(blackBoxModel) || mChecker.isStopped(blackBoxModel)) // TODO: Check this filter
                    .firstElement()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache();
            if (!mGeneralMonitoringReference.compareAndSet(null, maybe)) {
                maybe = mGeneralMonitoringReference.get();
            }
        }

        mCurrentMonitoringDisposable.dispose();
        mCurrentMonitoringDisposable = maybe
                .doOnSubscribe(unused -> {  // must be lower subscribeOn() in the current chain
                    mView.removeAnyPopup();
                    mCurrentWork = CurrentWork.GENERAL_MONITORING;
                })
                .doFinally(() -> mGeneralMonitoringReference.set(null))
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
    }

    /**
     * Try to reconnect. If done success start new monitoring task again.
     * If connection is not established show popup and continue monitoring
     */
    void startReconnection() {

        Timber.d("Start reconnection");

        Completable reconnectCompletable = mReconnectionReference.get();
        if (reconnectCompletable == null) {
            int boxId = mPreferencesManager.getBoxId();
            reconnectCompletable = mBlackBoxConnectionManager.connectBlackBox(boxId)
                    .flatMapObservable(BlackBoxConnectionManager::getDataObservable)
                    .firstOrError()
                    .toCompletable()
                    .timeout(mBlackBoxTimeoutMillis, TimeUnit.MILLISECONDS)
                    .onErrorResumeNext(throwable -> {
                        Timber.d("start reconnection again");
                        if (throwable instanceof TimeoutException) {
                            // If timeout is occurs show disconnection popup and continue monitoring
                            return Completable.fromAction(mView::showDisconnectionPopup)
                                    .observeOn(Schedulers.io())
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .andThen(mBlackBoxConnectionManager.connectBlackBox(boxId))
                                    .flatMapObservable(BlackBoxConnectionManager::getDataObservable)
                                    .firstOrError()
                                    .toCompletable();
                        }
                        return Completable.error(throwable);
                    })
                    .cache();
            if (!mReconnectionReference.compareAndSet(null, reconnectCompletable)) {
                reconnectCompletable = mReconnectionReference.get();
            }
        }

        mCurrentMonitoringDisposable.dispose();
        mCurrentMonitoringDisposable = reconnectCompletable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(unused -> mCurrentWork = CurrentWork.RECONNECTING)
                .doFinally(() -> mReconnectionReference.set(null))
                .subscribe(this::startGeneralMonitoring,
                        throwable -> mView.closeLockScreen());
    }

    /**
     * Detect any status from black box except
     * {@link com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel.ResponseType#STOPPED}
     * If that status will be emitted start main monitoring again.
     * Closes the Lock Screen if timeout occurs
     */
    void startIdlingMonitoring() {

        Completable idleCompletable = mIdleMonitoringCompletableReference.get();
        if (idleCompletable == null) {

            idleCompletable = mBlackBoxConnectionManager.getDataObservable()
                    .filter(blackBoxModel -> !mChecker.isStopped(blackBoxModel)
                            && !mChecker.isUpdate(blackBoxModel))
                    .timeout(mIdlingTimeoutMillis, TimeUnit.MILLISECONDS)
                    .firstElement()
                    .ignoreElement()
                    .cache();

            if (!mIdleMonitoringCompletableReference.compareAndSet(null, idleCompletable)) {
                idleCompletable = mIdleMonitoringCompletableReference.get();
            }
        }

        mCurrentMonitoringDisposable.dispose();
        mCurrentMonitoringDisposable = idleCompletable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(unused -> mCurrentWork = CurrentWork.IDLE_MONITORING)
                .doFinally(() -> mIdleMonitoringCompletableReference.set(null))
                .observeOn(AndroidSchedulers.mainThread())
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
    }

    void startMonitoringDriving() {

        Maybe<BlackBoxModel> drivingCompletable = mMonitoringDrivingReference.get();
        if (drivingCompletable == null) {

            drivingCompletable = mBlackBoxConnectionManager.getDataObservable()
                    .filter(mChecker::isMoving)
                    .firstElement()
                    .cache();

            if (!mMonitoringDrivingReference.compareAndSet(null, drivingCompletable)) {
                drivingCompletable = mMonitoringDrivingReference.get();
            }
        }

        mCurrentMonitoringDisposable.dispose();
        mCurrentMonitoringDisposable = drivingCompletable
                .doOnSubscribe(unused -> mCurrentWork = CurrentWork.DRIVING_MONITORING)
                .doFinally(() -> mMonitoringDrivingReference.set(null))
                .subscribe(
                        blackBoxModel -> startGeneralMonitoring(),
                        throwable -> Timber.d(throwable, "Error monitoring MOVING status"),
                        mView::closeLockScreen);// Data observable was completed for any reason. Close lock screen
    }

    private enum CurrentWork {
        GENERAL_MONITORING, RECONNECTING, DRIVING_MONITORING, IDLE_MONITORING, NOTHING
    }

    private final AccountManager.AccountListener accountListener = new AccountManager.AccountListener() {
        @Override
        public void onUserChanged() {
            if (!mAccountManager.isCurrentUserDriver()) {
                mView.closeLockScreen();
            }
        }

        @Override
        public void onDriverChanged() {

        }
    };
}
