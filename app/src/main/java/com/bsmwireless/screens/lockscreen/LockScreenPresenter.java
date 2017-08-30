package com.bsmwireless.screens.lockscreen;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

@ActivityScope
public class LockScreenPresenter {

    final LockScreenView mView;
    final DutyTypeManager mDutyManager;
    final AtomicLong drivingTime;
    private final CompositeDisposable mCompositeDisposable;

    @Inject
    public LockScreenPresenter(LockScreenView view, DutyTypeManager dutyManager) {
        mView = view;
        mDutyManager = dutyManager;
        mCompositeDisposable = new CompositeDisposable();
        drivingTime = new AtomicLong(0);
    }

    public void onStart() {

        mView.setTimeForDutyType(DutyType.DRIVING, mDutyManager.getDutyTypeTime(DutyType.DRIVING));
        mView.setTimeForDutyType(DutyType.SLEEPER_BERTH, mDutyManager.getDutyTypeTime(DutyType.SLEEPER_BERTH));
        mView.setTimeForDutyType(DutyType.ON_DUTY, mDutyManager.getDutyTypeTime(DutyType.ON_DUTY));
        mView.setTimeForDutyType(DutyType.OFF_DUTY, mDutyManager.getDutyTypeTime(DutyType.OFF_DUTY));

        startTimer();
    }

    public void onStop() {
        mCompositeDisposable.clear();
        resetTime();
    }

    public void switchCoDriver() {
        mView.openCoDriverDialog();
    }

    private void resetTime(){
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
}
