package com.bsmwireless.screens.lockscreen;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import dagger.Lazy;
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
    final AtomicLong drivingTime;
    final Lazy<BlackBoxConnectionManager> connectionManager;
    private final CompositeDisposable mCompositeDisposable;
    private final BlackBox blackBox;
    private final long blackBoxTimeoutMillis;

    @Inject
    public LockScreenPresenter(LockScreenView view,
                               DutyTypeManager dutyManager,
                               Lazy<BlackBoxConnectionManager> connectionManager,
                               BlackBox blackBox,
                               long blackBoxTimeoutMillis) {
        mView = view;
        mDutyManager = dutyManager;
        this.connectionManager = connectionManager;
        this.blackBox = blackBox;
        this.blackBoxTimeoutMillis = blackBoxTimeoutMillis;
        mCompositeDisposable = new CompositeDisposable();
        drivingTime = new AtomicLong(0);
    }

    public void onStart() {

        mView.setTimeForDutyType(DutyType.DRIVING, mDutyManager.getDutyTypeTime(DutyType.DRIVING));
        mView.setTimeForDutyType(DutyType.SLEEPER_BERTH, mDutyManager.getDutyTypeTime(DutyType.SLEEPER_BERTH));
        mView.setTimeForDutyType(DutyType.ON_DUTY, mDutyManager.getDutyTypeTime(DutyType.ON_DUTY));
        mView.setTimeForDutyType(DutyType.OFF_DUTY, mDutyManager.getDutyTypeTime(DutyType.OFF_DUTY));

        startTimer();
        startMonitoring();
    }

    public void onStop() {
        mCompositeDisposable.clear();
        resetTime();
    }

    public void switchCoDriver() {
        mView.openCoDriverDialog();
    }

    private void resetTime() {
        mDutyManager.setDutyType(DutyType.DRIVING, true);
    }

    private void startTimer() {
        final Disposable disposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map(interval -> {
                    final long dutyTypeTime = mDutyManager.getDutyTypeTime(DutyType.DRIVING);
                    drivingTime.set(interval);
                    return dutyTypeTime;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::setCurrentTime, throwable -> Timber.d(throwable, "Timer error"));
        mCompositeDisposable.add(disposable);
    }

    void startMonitoring() {
        final Disposable disposable = blackBox.getDataObservable()
                .filter(blackBoxModel -> blackBoxModel.getResponseType() == BlackBoxResponseModel.ResponseType.IGNITION_OFF
                        || blackBoxModel.getResponseType() == BlackBoxResponseModel.ResponseType.STOPPED) // TODO: Check this filter
                .subscribeOn(Schedulers.io())
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(blackBoxModel -> {

                    switch (blackBoxModel.getResponseType()) {
                        case IGNITION_OFF:
                            // change duty status to on duty- popup is there on screen that ignition off is detected do you want to go off duty- on-duty
                            mView.showIgnitionOfDetectedDialog();
                            startMonitoringAnyStatus();
                            break;

                        case STOPPED:
                            // idling for 5 minutes triggers unlock of screen with on duty status.(no popup) .
                            startIdlingMonitoring();
                            break;

                        default:
                            break;
                    }

                }, throwable -> {
                    Timber.e(throwable, "Error getting a black box data");
                    startReconnection();
                });
        mCompositeDisposable.add(disposable);
    }

    void startReconnection() {

    }

    /**
     * Detect any status from black box except {@link com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel.ResponseType#STOPPED}
     * If that status will be emitted start main monitoring again.
     * Closes the Lock Screen if timeout occurs
     */
    void startIdlingMonitoring() {
        blackBox.getDataObservable()
                .filter(blackBoxModel -> blackBoxModel.getResponseType() != BlackBoxResponseModel.ResponseType.STOPPED)
                .timeout(blackBoxTimeoutMillis, TimeUnit.MILLISECONDS)
                .firstElement()
                .ignoreElement()
                .subscribeOn(Schedulers.io())
                .subscribe(this::startMonitoring,
                        throwable -> {
                            if (throwable instanceof TimeoutException) {
                                mView.closeLockScreen();
                            } else {
                                // what should we do in this case?
                                Timber.d(throwable, "Idling status error");
                            }
                        });
    }

    void startMonitoringAnyStatus() {

    }
}
