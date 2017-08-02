package com.bsmwireless.schedulers;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.schedulers.alarmmanager.AlarmReceiver;
import com.bsmwireless.schedulers.jobscheduler.AutoLogoutJobService;
import com.bsmwireless.screens.login.LoginActivity;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AutoLogoutDialogActivity extends Activity {

    public static final String ARG_JOBS_PARAMETERS = "params";

    @Inject
    LoginUserInteractor mLoginUserInteractor;

    private AutoLogoutJobService mAutoLogoutJobService;
    private CompositeDisposable mDisposables;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getComponent().inject(this);

        mAutoLogoutJobService = new AutoLogoutJobService();
        mDisposables = new CompositeDisposable();

        initAutoLogoutDialog();
    }

    private void initAutoLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.auto_logout_message_alert_dialog);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.auto_logout_positive_button_lbl_alert_dialog, (dialog, which) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                initJobSchedulerAutoLogout();
            } else {
                initAlarmManager();
            }
        });
        builder.setNegativeButton(R.string.auto_logout_cancel_button_lbl_alert_dialog, (dialog, which) -> {
            SchedulerUtils.cancel();
            SchedulerUtils.schedule();
        });
        builder.show();
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
                                Intent intent = new Intent(App.getComponent().context(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getComponent().context().startActivity(intent);

                                mAutoLogoutJobService.jobFinished(getIntent().getExtras().getParcelable(ARG_JOBS_PARAMETERS), false);
                            }
                        },
                        error -> Timber.e("LoginUser error: %s", error)

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
                                Intent loginActivityIntent = new Intent(App.getComponent().context(), LoginActivity.class);
                                loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getComponent().context().startActivity(loginActivityIntent);

                                AlarmReceiver.completeWakefulIntent(getIntent().getParcelableExtra(Intent.EXTRA_INTENT));
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            AlarmReceiver.completeWakefulIntent(getIntent().getParcelableExtra(Intent.EXTRA_INTENT));
                        }
                );
        mDisposables.add(disposable);
    }

    @Override
    protected void onDestroy() {
        mDisposables.dispose();
        super.onDestroy();
    }
}
