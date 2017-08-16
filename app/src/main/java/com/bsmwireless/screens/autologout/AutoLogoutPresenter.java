package com.bsmwireless.screens.autologout;


import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AutoLogoutPresenter {

    private LoginUserInteractor mLoginUserInteractor;
    private CompositeDisposable mDisposables;
    private AutoLogoutView mView;

    @Inject
    public AutoLogoutPresenter(AutoLogoutView view, LoginUserInteractor interactor) {
        mView = view;
        mLoginUserInteractor = interactor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void rescheduleAutoLogout() {
        SchedulerUtils.cancel();
        if (mLoginUserInteractor.isLoginActive()) {
            SchedulerUtils.schedule();
        }
    }

    public void initAutoLogoutIfNoUserInteraction() {
        mView.initAutoLogoutIfNoUserInteraction();
    }

    public void initAutoLogout() {
        Log.d("JobScheduler", "initAutoLogout");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initJobSchedulerAutoLogout();
        } else {
            initAlarmManager();
        }
    }

    @TargetApi(21)
    private void initJobSchedulerAutoLogout() {
        Disposable disposable = mLoginUserInteractor.logoutUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                mView.goToLoginScreen();
                            }
                            SchedulerUtils.cancel();
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            SchedulerUtils.cancel();
                        }

                );
        mDisposables.add(disposable);
    }

    private void initAlarmManager() {
        Disposable disposable = mLoginUserInteractor.logoutUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                mView.goToLoginScreen();
                            }
                            SchedulerUtils.cancel();
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            SchedulerUtils.cancel();
                        }
                );
        mDisposables.add(disposable);
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
