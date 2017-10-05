package com.bsmwireless.screens.lockscreen;


import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.common.utils.observers.DutyManagerObservable;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionException;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public final class LockScreenPresenter {

    LockScreenView mView;
    final DutyTypeManager mDutyManager;
    final BlackBoxInteractor mBlackBoxInteractor;
    private final CompositeDisposable mCompositeDisposable;
    private final PreferencesManager mPreferencesManager;
    final BlackBoxStateChecker mChecker;
    private final ELDEventsInteractor mEventsInteractor;
    private final AppSettings mAppSettings;
    final AccountManager mAccountManager;
    private final AtomicReference<Completable> mReconnectionReference;
    private final AtomicReference<Completable> mStoppedReference;
    private Disposable mIdlingTDisposable;
    private Disposable mIgnitionOffDisposable;
    private volatile BlackBoxResponseModel.ResponseType mCurrentResponseType;

    @Inject
    public LockScreenPresenter(
            DutyTypeManager dutyManager,
            BlackBoxInteractor connectionManager,
            PreferencesManager preferencesManager,
            BlackBoxStateChecker checker,
            ELDEventsInteractor eventsInteractor,
            AppSettings appSettings,
            AccountManager accountManager) {
        mDutyManager = dutyManager;
        mBlackBoxInteractor = connectionManager;
        mPreferencesManager = preferencesManager;
        mChecker = checker;
        mEventsInteractor = eventsInteractor;
        mAppSettings = appSettings;
        mAccountManager = accountManager;
        mCompositeDisposable = new CompositeDisposable();
        mReconnectionReference = new AtomicReference<>();
        mIdlingTDisposable = Disposables.disposed();
        mIgnitionOffDisposable = Disposables.disposed();
        mStoppedReference = new AtomicReference<>();
        mCurrentResponseType = BlackBoxResponseModel.ResponseType.NONE;
    }

    public void onStart(@NonNull LockScreenView view) {

        mView = view;

        startDutyTypeMonitoring();
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

        mIgnitionOffDisposable.dispose();

        if (dutyType == DutyType.ON_DUTY) {
            // just close screen, status already changed in this case
            mView.closeLockScreen();
            return;
        }

        Disposable disposable = Single
                .fromCallable(() -> mEventsInteractor.getEvent(dutyType))
                .flatMapCompletable(this::createPostNewEventCompletable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (mView != null) {
                        mView.closeLockScreen();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    void setDutyType(DutyType currentDutyType) {
        Timber.d("Current duty type is - " + currentDutyType);
        mView.setTimeForDutyType(currentDutyType, getTimeForDutyType(currentDutyType));
        mView.setTimeForDutyType(DutyType.SLEEPER_BERTH, getTimeForDutyType(
                currentDutyType == DutyType.SLEEPER_BERTH ? DutyType.DRIVING : DutyType.SLEEPER_BERTH));
        mView.setTimeForDutyType(DutyType.ON_DUTY, getTimeForDutyType(
                currentDutyType == DutyType.ON_DUTY || currentDutyType == DutyType.YARD_MOVES
                        ? DutyType.DRIVING : DutyType.ON_DUTY));
        mView.setTimeForDutyType(DutyType.OFF_DUTY, getTimeForDutyType(
                currentDutyType == DutyType.OFF_DUTY || currentDutyType == DutyType.PERSONAL_USE
                        ? DutyType.DRIVING : DutyType.OFF_DUTY));
    }

    private long getTimeForDutyType(DutyType dutyType) {
        return mDutyManager.getDutyTypeTime(dutyType);
    }

    private void resetTime(DutyType dutyType) {
        mDutyManager.setDutyType(dutyType, true);
    }

    private void startDutyTypeMonitoring() {
        Disposable disposable = createChangingDutyTypeObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setDutyType,
                        throwable -> Timber.e(throwable, "Error getting a duty type"));
        mCompositeDisposable.add(disposable);
    }

    @VisibleForTesting
    Observable<DutyType> createChangingDutyTypeObservable(){
        return DutyManagerObservable.create(mDutyManager);
    }

    @VisibleForTesting
    void startTimer() {
        final Disposable disposable = Observable
                .interval(1, TimeUnit.SECONDS)
                .map(interval -> mDutyManager.getDutyTypeTime(mDutyManager.getDutyType()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::setCurrentTime, throwable -> Timber.d(throwable, "Timer error"));
        mCompositeDisposable.add(disposable);
    }

    /**
     * Start the monitoring statuses task.
     */
    void startMonitoring() {
        // try to restore tasks which triggered by timeout
        if (mCurrentResponseType == BlackBoxResponseModel.ResponseType.STOPPED) {
            handleStopped();
        } else if (mStoppedReference.get() != null) {
            handleDisconnection();
        }

        Disposable disposable = mBlackBoxInteractor
                .getData(mPreferencesManager.getBoxId())
                .skip(1) // first element already handled, skip it
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
                    mCurrentResponseType = BlackBoxResponseModel.ResponseType.NONE;
                    if (throwable instanceof BlackBoxConnectionException) {
                        handleDisconnection();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    void handleIgnitionOff() {
        // cancel the idling timer first
        mIdlingTDisposable.dispose();
        mIgnitionOffDisposable =
                Single.fromCallable(() -> mEventsInteractor.getEvent(DutyType.ON_DUTY))
                        .flatMapCompletable(this::createPostNewEventCompletable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .andThen(Completable.fromAction(() -> {
                            if (mView != null) {
                                mView.showIgnitionOffDetectedDialog();
                            }
                        }))
                        .andThen(Completable.timer(mAppSettings.ignitionOffDialogTimeout(),
                                TimeUnit.MILLISECONDS))
                        .subscribe(() -> {
                            if (mView != null) {
                                mView.closeLockScreen();
                            }
                        });
        mCompositeDisposable.add(mIgnitionOffDisposable);
    }

    void handleStopped() {

        if (!mIdlingTDisposable.isDisposed()) {
            //already running
            return;
        }

        Completable idleReference = mStoppedReference.get();
        if (idleReference == null) {
            idleReference = Completable
                    .timer(mAppSettings.lockScreenIdlingTimeout(), TimeUnit.MILLISECONDS)
                    .cache();

            if (!mStoppedReference.compareAndSet(null, idleReference)) {
                idleReference = mStoppedReference.get();
            }
        }

        mIdlingTDisposable = idleReference
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mReconnectionReference.set(null))
                .subscribe(mView::closeLockScreen);
        mCompositeDisposable.add(mIdlingTDisposable);
    }

    void handleMoving() {
        if (mView != null) {
            mView.removeAnyPopup();
        }
        mIdlingTDisposable.dispose();
    }

    void handleDisconnection() {
        mIdlingTDisposable.dispose();
        Disposable disposable = Single
                .fromCallable(() -> mEventsInteractor.getEvent(DutyType.ON_DUTY))
                .subscribeOn(Schedulers.io())
                .subscribe(this::startReconnection, Timber::e);
        mCompositeDisposable.add(disposable);
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
            reconnectCompletable = mBlackBoxInteractor.getData(boxId)
                    .firstOrError()
                    .toCompletable()
                    .timeout(mAppSettings.lockScreenDisconnectionTimeout(), TimeUnit.MILLISECONDS)
                    .onErrorResumeNext(throwable -> {
                        Timber.d("start reconnection again");
                        if (throwable instanceof TimeoutException) {
                            // If timeout is occurs show disconnection popup and continue monitoring
                            return createReconnectionCompletable(eldEvent);
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
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mReconnectionReference.set(null))
                .subscribe(() -> {
                    if (mView != null) {
                        mView.removeAnyPopup();
                    }
                    startMonitoring();
                }, throwable -> mView.closeLockScreen());
        mCompositeDisposable.add(disposable);
    }

    private Completable createReconnectionCompletable(ELDEvent eldEvent) {
        return Completable
                .fromAction(mView::showDisconnectionPopup)
                .andThen(createPostNewEventCompletable(eldEvent))
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .andThen(mBlackBoxInteractor.getData(mPreferencesManager.getBoxId()))
                .flatMapCompletable(blackBoxModel -> Completable.defer(() -> {

                    // At this moment ON_DUTY event is in database
                    // Switch to driving if box in driving mode

                    if (mChecker.isMoving(blackBoxModel)) {
                        return Single
                                .fromCallable(() -> mEventsInteractor.getEvent(DutyType.DRIVING))
                                .flatMapCompletable(this::createPostNewEventCompletable);
                    }
                    return Completable.complete();
                }));
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
                });
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
