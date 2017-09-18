package com.bsmwireless.screens.lockscreen;


import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionException;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class LockScreenPresenter {

    LockScreenView mView;
    final DutyTypeManager mDutyManager;
    final BlackBoxConnectionManager mBlackBoxConnectionManager;
    private final CompositeDisposable mCompositeDisposable;
    private final PreferencesManager mPreferencesManager;
    final BlackBoxStateChecker mChecker;
    private final ELDEventsInteractor mEventsInteractor;
    private final long mBlackBoxTimeoutMillis;
    private final long mIdlingTimeoutMillis;
    private final AccountManager mAccountManager;
    private final AtomicReference<Completable> mReconnectionReference;
    private Disposable mIdlingTDisposable;
    private volatile BlackBoxResponseModel.ResponseType mCurrentResponseType;

    @Inject
    public LockScreenPresenter(
            DutyTypeManager dutyManager,
            BlackBoxConnectionManager connectionManager,
            PreferencesManager preferencesManager,
            BlackBoxStateChecker checker,
            ELDEventsInteractor eventsInteractor,
            @Named("disconnectTimeout") long blackBoxTimeoutMillis,
            @Named("idleTimeout") long idlingTimeout, AccountManager accountManager) {
        mDutyManager = dutyManager;
        this.mBlackBoxConnectionManager = connectionManager;
        this.mPreferencesManager = preferencesManager;
        this.mChecker = checker;
        mEventsInteractor = eventsInteractor;
        this.mBlackBoxTimeoutMillis = blackBoxTimeoutMillis;
        this.mIdlingTimeoutMillis = idlingTimeout;
        this.mAccountManager = accountManager;
        mCompositeDisposable = new CompositeDisposable();
        mReconnectionReference = new AtomicReference<>();
        mIdlingTDisposable = Disposables.disposed();
        mCurrentResponseType = BlackBoxResponseModel.ResponseType.NONE;
    }

    public void onStart(@NonNull LockScreenView view) {

        mView = view;

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
        mAccountManager.removeListener(accountListener);
        mView = null;
    }

    public void switchCoDriver() {
        if (mView != null) {
            mView.openCoDriverDialog();
        }
    }

    public void onDutyTypeSelected(DutyType dutyType) {

        if (dutyType == DutyType.ON_DUTY &&
                mCurrentResponseType == BlackBoxResponseModel.ResponseType.IGNITION_OFF) {
            // just close screen, status already changed in this case
            mView.closeLockScreen();
            return;
        }

        Disposable disposable = createPostNewEventCompletable(mEventsInteractor.getEvent(dutyType))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (mView != null) {
                        mView.closeLockScreen();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    private void resetTime(DutyType dutyType) {
        mDutyManager.setDutyType(dutyType, true);
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
        if (mView != null) {
            mView.removeAnyPopup();
        }
        mBlackBoxConnectionManager.getDataObservable()
                .distinctUntilChanged(BlackBoxModel::getResponseType)
                .doOnNext(blackBoxModel -> mCurrentResponseType = blackBoxModel.getResponseType())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(blackBoxModel -> {
                    switch (blackBoxModel.getResponseType()) {

                        case IGNITION_OFF:
                            handleIgnitionOff();
                            break;

                        case STOPPED:
                            handleStopped();
                            break;

                        case MOVING:
                            handleMoving();
                            break;

                        default:
                            break;
                    }
                }, throwable -> {
                    if (throwable instanceof BlackBoxConnectionException) {
                        handleDisconnection();
                    }
                });
    }

    void handleIgnitionOff() {
        mIdlingTDisposable.dispose();
        Disposable disposable = createPostNewEventCompletable(mEventsInteractor.getEvent(DutyType.ON_DUTY))
                .subscribe(() -> {
                    if (mView != null) {
                        mView.showIgnitionOffDetectedDialog();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    void handleStopped() {

        if (!mIdlingTDisposable.isDisposed()) {
            //already running
            return;
        }

        mIdlingTDisposable = Completable.timer(mIdlingTimeoutMillis, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::closeLockScreen);
    }

    void handleMoving() {

        Disposable disposable = createPostNewEventCompletable(mEventsInteractor.getEvent(DutyType.DRIVING))
                .subscribe(() -> {
                    if (mView != null) {
                        mView.removeAnyPopup();
                    }
                });
        mCompositeDisposable.add(disposable);
        mIdlingTDisposable.dispose();
    }

    void handleDisconnection() {
        mIdlingTDisposable.dispose();
        ELDEvent event = mEventsInteractor.getEvent(DutyType.ON_DUTY);
        startReconnection(event);
    }

    /**
     * Try to reconnect. If done success start new monitoring task again.
     * If connection is not established show popup and continue monitoring
     */
    void startReconnection(ELDEvent eldEvent) {

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

        Disposable disposable = reconnectCompletable
                .subscribeOn(Schedulers.io())
                .andThen(createPostNewEventCompletable(eldEvent))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mReconnectionReference.set(null))
                .subscribe(this::startMonitoring, throwable -> mView.closeLockScreen());
        mCompositeDisposable.add(disposable);
    }

    private Completable createPostNewEventCompletable(ELDEvent eldEventInfo) {
        return mEventsInteractor.postNewELDEvent(eldEventInfo)
                .toCompletable()
                .doOnComplete(() -> {
                    Integer eventType = eldEventInfo.getEventType();
                    Integer eventCode = eldEventInfo.getEventCode();
                    DutyType dutyType = DutyType.getTypeByCode(eventType, eventCode);
                    resetTime(dutyType);
                })
                .onErrorComplete(throwable -> {
                    Timber.e(throwable, "Post new duty event error");
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private final AccountManager.AccountListener accountListener = new AccountManager.AccountListener() {
        @Override
        public void onUserChanged() {
            if (!mAccountManager.isCurrentUserDriver() & mView != null) {
                mView.closeLockScreen();
            }
        }

        @Override
        public void onDriverChanged() {

        }
    };
}
