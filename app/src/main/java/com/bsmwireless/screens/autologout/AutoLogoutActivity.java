package com.bsmwireless.screens.autologout;


import android.app.job.JobParameters;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.autologout.dagger.AutoLogoutModule;
import com.bsmwireless.screens.autologout.dagger.DaggerAutoLogoutComponent;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.login.LoginActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.bsmuniversal.com.R;

public class AutoLogoutActivity extends BaseActivity implements AutoLogoutView {

    private static final String TAG = AutoLogoutActivity.class.getSimpleName();

    public static final String ARG_JOBS_PARAMETERS = "params";

    private static final int NO_USER_INTERACTION = 1;

    @Inject
    AutoLogoutPresenter mAutoLogoutPresenter;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerAutoLogoutComponent.builder().appComponent(App.getComponent()).autoLogoutModule(new AutoLogoutModule(this)).build().inject(this);

        mAutoLogoutPresenter.initAutoLogoutDialog(getIntent().getExtras().getParcelable(ARG_JOBS_PARAMETERS),
                getIntent().getParcelableExtra(Intent.EXTRA_INTENT));
    }

    @Override
    protected void onDestroy() {
        mAlertDialog.dismiss();
        mAutoLogoutPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void goToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showAutoLogoutDialog(JobParameters jobParameters, Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.auto_logout_message_alert_dialog);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.auto_logout_positive_button_lbl_alert_dialog, (dialog, which) ->
                mAutoLogoutPresenter.initAutoLogout(jobParameters, intent));
        builder.setNegativeButton(R.string.auto_logout_cancel_button_lbl_alert_dialog, (dialog, which) ->
                mAutoLogoutPresenter.rescheduleAutoLogout());
        mAlertDialog = builder.create();
        mAlertDialog.show();

        mAutoLogoutPresenter.initAutoLogoutIfNoUserInteraction(jobParameters, intent);
    }

    @Override
    public void initAutoLogoutIfNoUserInteraction(JobParameters jobParameters, Intent intent) {
        final Handler mHandler = new Handler();
        final Runnable runnable = () -> {
            if (mAlertDialog.isShowing()) {
                mAutoLogoutPresenter.initAutoLogout(jobParameters, intent);
            }
        };

        mAlertDialog.setOnDismissListener(dialog -> mHandler.removeCallbacks(runnable));

        mHandler.postDelayed(runnable, TimeUnit.MINUTES.toMillis(NO_USER_INTERACTION));
    }
}
