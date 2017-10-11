package com.bsmwireless.screens.lockscreen;


import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.observers.DutyManagerObservable;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.TimeUnit;

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
    private CompositeDisposable mCompositeDisposable;
    private final ELDEventsInteractor mEventsInteractor;
    private final AppSettings mAppSettings;
    final AccountManager mAccountManager;
    private Disposable mIgnitionOffDisposable;
    private final AutoDutyTypeManager mAutoDutyTypeManager;

    @Inject
    public LockScreenPresenter(
            DutyTypeManager dutyManager,
            ELDEventsInteractor eventsInteractor,
            AppSettings appSettings,
            AccountManager accountManager,
            AutoDutyTypeManager autoDutyTypeManager) {
        mDutyManager = dutyManager;
        mEventsInteractor = eventsInteractor;
        mAppSettings = appSettings;
        mAccountManager = accountManager;
        mAutoDutyTypeManager = autoDutyTypeManager;
        mIgnitionOffDisposable = Disposables.disposed();
        mCompositeDisposable = new CompositeDisposable();
    }

    public void bind(@NonNull LockScreenView view) {
        mCompositeDisposable = new CompositeDisposable();
        mView = view;

        startDutyTypeMonitoring();
        startTimer();
        startMonitoring();
        mAccountManager.addListener(accountListener);
    }

    public void unbind() {
        stopMonitoring();
        mCompositeDisposable.dispose();
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
            closeLockScreen();
            return;
        }

        Disposable disposable = Single
                .fromCallable(() -> mEventsInteractor.getEvent(dutyType))
                .flatMapCompletable(this::createPostNewEventCompletable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::closeLockScreen);
        mCompositeDisposable.add(disposable);
    }

    void setDutyType(DutyType currentDutyType) {
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

    private void startDutyTypeMonitoring() {
        Disposable disposable = createChangingDutyTypeObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setDutyType,
                        throwable -> Timber.e(throwable, "Error getting a duty type"));
        mCompositeDisposable.add(disposable);
    }

    @VisibleForTesting
    Observable<DutyType> createChangingDutyTypeObservable() {
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
        mAutoDutyTypeManager.setOnIgnitionOffListener(ignitionOffListener);
        mAutoDutyTypeManager.setOnMovingListener(mOnMovingListener);
        mAutoDutyTypeManager.setOnStoppedListener(mOnStoppedListener);
        mAutoDutyTypeManager.setOnDisconnectListener(mOnDisconnectListener);
    }

    private void stopMonitoring() {
        mAutoDutyTypeManager.setOnIgnitionOffListener(null);
        mAutoDutyTypeManager.setOnMovingListener(null);
        mAutoDutyTypeManager.setOnStoppedListener(null);
        mAutoDutyTypeManager.setOnDisconnectListener(null);
    }

    void handleIgnitionOff(DutyType dutyType) {
        if (mView == null) {
            return;
        }

        if (dutyType == DutyType.PERSONAL_USE || dutyType == DutyType.YARD_MOVES) {
            closeLockScreen();
            return;
        }

        // cancel the idling timer first
        mView.showIgnitionOffDetectedDialog();
        mIgnitionOffDisposable = Completable
                .timer(mAppSettings.ignitionOffDialogTimeout(), TimeUnit.MILLISECONDS)
                .subscribe(this::closeLockScreen);
        mCompositeDisposable.add(mIgnitionOffDisposable);
    }

    void handleStopped() {
        closeLockScreen();
    }

    void handleMoving() {
        if (mView != null) {
            mView.removeAnyPopup();
        }
    }

    void handleDisconnection() {

        DutyType dutyType = mDutyManager.getDutyType();
        if (dutyType != DutyType.DRIVING) {
            closeLockScreen();
            return;
        }

        Disposable disposable = Completable
                .fromAction(() -> {
                    if (mView != null) {
                        mView.showDisconnectionPopup();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
        mCompositeDisposable.add(disposable);
    }


    void closeLockScreen() {
        if (mView != null) {
            mView.closeLockScreen();
        }
    }

    private Completable createPostNewEventCompletable(ELDEvent eldEventInfo) {
        return mEventsInteractor.postNewELDEvent(eldEventInfo)
                .toCompletable()
                .doOnComplete(() -> {
                    Integer eventType = eldEventInfo.getEventType();
                    Integer eventCode = eldEventInfo.getEventCode();
                    DutyType dutyType = DutyType.getDutyTypeByCode(eventType, eventCode);
                    mDutyManager.setDutyType(dutyType, true);
                })
                .onErrorComplete(throwable -> {
                    Timber.e(throwable, "Post new duty event error");
                    return true;
                });
    }

    private final AccountManager.AccountListener accountListener = new AccountManager.AccountListener() {
        @Override
        public void onUserChanged() {
            if (!mAccountManager.isCurrentUserDriver()) {
                closeLockScreen();
            }
        }

        @Override
        public void onDriverChanged() {

        }
    };

    private final AutoDutyTypeManager.OnIgnitionOffListener ignitionOffListener =
            this::handleIgnitionOff;

    private final AutoDutyTypeManager.OnStoppedListener mOnStoppedListener = this::handleStopped;

    private final AutoDutyTypeManager.OnMovingListener mOnMovingListener = this::handleMoving;

    private final AutoDutyTypeManager.OnDisconnectListener mOnDisconnectListener =
            this::handleDisconnection;
}
