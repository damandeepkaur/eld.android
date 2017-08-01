package com.bsmwireless.schedulers.jobscheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.bsmwireless.common.App;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.screens.login.LoginActivity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@TargetApi(21)
public class AutoLogoutJobService extends JobService {

    @Inject
    LoginUserInteractor mLoginUserInteractor;

    private CompositeDisposable mDisposables;

    public AutoLogoutJobService() {
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Disposable disposable = mLoginUserInteractor.logoutUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                Intent intent = new Intent(App.getComponent().context(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getComponent().context().startActivity(intent);

                                jobFinished(params, false);
                            }
                        },
                        error -> Timber.e("LoginUser error: %s", error)
                );
        mDisposables.add(disposable);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mDisposables.dispose();
        return super.onUnbind(intent);
    }
}
