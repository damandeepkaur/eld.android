package com.bsmwireless.screens.autologout;


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

import javax.inject.Inject;

import app.bsmuniversal.com.R;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_MIN;

public class AutoDutyDialogActivity extends BaseActivity implements AutoDutyDialogView {

    private static final String TAG = AutoDutyDialogActivity.class.getSimpleName();

    public static final String EXTRA_AUTO_LOGOUT = "auto_logout";
    public static final String EXTRA_AUTO_ON_DUTY = "auto_on_duty";
    public static final String EXTRA_AUTO_DRIVING = "auto_driving";
    public static final String EXTRA_AUTO_DRIVING_WITHOUT_CONFIRM = "auto_driving_without_confirm";

    @Inject
    AutoDutyDialogPresenter mPresenter;

    private AlertDialog mAlertDialog;

    private Handler mHandler = new Handler();

    private Runnable mAutoLogoutTask = () -> mPresenter.onAutoLogoutClick();

    private Runnable mAutoOnDutyTask = () -> mPresenter.onOnDutyClick();

    private boolean mIsAutoDrivingDialogShown = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerAutoLogoutComponent.builder().appComponent(App.getComponent()).autoLogoutModule(new AutoLogoutModule(this)).build().inject(this);

        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(EXTRA_AUTO_LOGOUT)) {
                showAutoLogoutDialog();

            } else if (intent.hasExtra(EXTRA_AUTO_DRIVING)) {
                showAutoDrivingDialog();

            } else if (intent.hasExtra(EXTRA_AUTO_ON_DUTY)) {
                showAutoOnDutyDialog();

            } else if (intent.hasExtra(EXTRA_AUTO_DRIVING_WITHOUT_CONFIRM) && mIsAutoDrivingDialogShown) {
                mPresenter.onDrivingClick();

            } else {
                onActionDone();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        mPresenter.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void goToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onActionDone() {
        finish();
    }

    private void showAutoLogoutDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.logout_dialog_title)
                .setMessage(R.string.logout_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.logout_accept, (dialog, which) -> mPresenter.onRescheduleAutoLogoutClick())
                .setNegativeButton(R.string.logout_cancel, (dialog, which) -> mPresenter.onAutoLogoutClick())
                .show();

        mHandler.removeCallbacks(mAutoLogoutTask);
        mHandler.postDelayed(mAutoLogoutTask, MS_IN_MIN);
    }

    private void showAutoDrivingDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        mAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme_Positive)
                .setTitle(R.string.driving_dialog_title)
                .setMessage(R.string.driving_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.driving_accept, (dialog, which) -> onActionDone())
                .setNegativeButton(R.string.driving_cancel, (dialog, which) -> mPresenter.onDrivingClick())
                .show();

        mIsAutoDrivingDialogShown = true;
    }

    private void showAutoOnDutyDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        mAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme_Positive)
                .setTitle(R.string.on_duty_dialog_title)
                .setMessage(R.string.on_duty_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.on_duty_accept, (dialog, which) -> onActionDone())
                .setNegativeButton(R.string.on_duty_cancel, (dialog, which) -> mPresenter.onOnDutyClick())
                .show();

        mHandler.removeCallbacks(mAutoOnDutyTask);
        mHandler.postDelayed(mAutoOnDutyTask, MS_IN_MIN);
    }
}
