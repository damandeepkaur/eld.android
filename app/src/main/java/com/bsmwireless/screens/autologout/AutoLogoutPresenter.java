package com.bsmwireless.screens.autologout;


import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.schedulers.alarmmanager.AlarmReceiver;
import com.bsmwireless.schedulers.jobscheduler.AutoLogoutJobService;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AutoLogoutPresenter {

    private LoginUserInteractor mLoginUserInteractor;

    private AutoLogoutJobService mAutoLogoutJobService;
    private CompositeDisposable mDisposables;
    private AutoLogoutView mView;

    @Inject
    public AutoLogoutPresenter(AutoLogoutView view, LoginUserInteractor interactor) {
        mView = view;
        mLoginUserInteractor = interactor;
        mDisposables = new CompositeDisposable();
        mAutoLogoutJobService = new AutoLogoutJobService();

        Timber.d("CREATED");
    }

    public void initAutoLogoutDialog(@Nullable JobParameters jobParameters, @Nullable Intent intent) {
        mView.showAutoLogoutDialog(jobParameters, intent);
    }

    public void rescheduleAutoLogout() {
        SchedulerUtils.cancel();
        SchedulerUtils.schedule();
    }

    public void initAutoLogoutIfNoUserInteraction(JobParameters jobParameters, Intent intent) {
        mView.initAutoLogoutIfNoUserInteraction(jobParameters, intent);
    }

    public void initAutoLogout(JobParameters jobParameters, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initJobSchedulerAutoLogout(jobParameters);
        } else {
            initAlarmManager(intent);
        }
    }

    @TargetApi(21)
    private void initJobSchedulerAutoLogout(JobParameters jobParameters) {
        Disposable disposable = mLoginUserInteractor.logoutUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                mView.goToLoginScreen();

                                mAutoLogoutJobService.jobFinished(jobParameters, false);
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            SchedulerUtils.cancel();
                        }

                );
        mDisposables.add(disposable);
    }

    private void initAlarmManager(Intent intent) {
        Disposable disposable = mLoginUserInteractor.logoutUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                mView.goToLoginScreen();

                                AlarmReceiver.completeWakefulIntent(intent);
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            AlarmReceiver.completeWakefulIntent(intent);
                        }
                );
        mDisposables.add(disposable);
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
