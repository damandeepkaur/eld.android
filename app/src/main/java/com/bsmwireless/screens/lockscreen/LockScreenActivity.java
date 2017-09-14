package com.bsmwireless.screens.lockscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.switchdriver.SwitchDriverDialog;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.dashboard.DutyView;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LockScreenActivity extends BaseActivity implements LockScreenView {

    public static final String PROMT_DIALOG = "PROMT_DIALOG";
    @BindView(R.id.dashboard_current)
    DutyView mCurrentDutyView;

    @BindView(R.id.dashboard_break)
    DutyView mBreakDutyView;

    @BindView(R.id.dashboard_workday)
    DutyView mWorkdayDutyView;

    @BindView(R.id.dashboard_cycle)
    DutyView mCycleDutyView;

    @Inject
    LockScreenPresenter presenter;


    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LockScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindToService(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);
        App.getComponent().lockScreenBuilder()
                .view(this)
                .disconnectionTimeout(Constants.LOCK_SCREEN_DISCONNECTION_TIMEOUT_MS)
                .idleTimeout(Constants.LOCK_SCREEN_IDLE_MONITORING_TIMEOUT_MS)
                .build()
                .inject(this);
        mCurrentDutyView.setCanChangingSatusView(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
        setListenersForPromtDialog(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
        setListenersForPromtDialog(true);
    }

    @Override
    public void onBackPressed() {
        /*no op*/
    }

    @Override
    public void setTimeForDutyType(DutyType dutyType, long time) {

        DutyView dutyView;
        switch (dutyType) {
            case DRIVING:
                dutyView = mCurrentDutyView;
                break;

            case SLEEPER_BERTH:
                dutyView = mBreakDutyView;
                break;

            case ON_DUTY:
                dutyView = mWorkdayDutyView;
                break;

            case OFF_DUTY:
                dutyView = mCycleDutyView;
                break;

            default:
                return;

        }

        updateDutyView(dutyView, dutyType, time);
    }

    @Override
    public void setCurrentTime(long time) {
        mCurrentDutyView.setTime(time);
    }

    @Override
    public void openCoDriverDialog() {
        SwitchDriverDialog dialog = new SwitchDriverDialog(this);
        dialog.show(SwitchDriverDialog.SwitchDriverStatus.SWITCH_DRIVER);
    }

    @Override
    public void showIgnitionOffDetectedDialog() {
        showPromtDialog(PromtDialog.newInstance(PromtDialog.DialogType.IGNITION_OFF));
    }

    @Override
    public void closeLockScreen() {
        finish();
    }

    @Override
    public void removeAnyPopup() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PROMT_DIALOG);
        if (fragment instanceof PromtDialog) {
            ((PromtDialog) fragment).dismiss();
        }
    }

    @Override
    public void showDisconnectionPopup() {
        showPromtDialog(PromtDialog.newInstance(PromtDialog.DialogType.IGNITION_OFF));
    }

    @OnClick(R.id.switch_co_driver_button)
    void switchCoDriverClick() {
        presenter.switchCoDriver();
    }

    private void showPromtDialog(PromtDialog dialog) {
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), PROMT_DIALOG);
    }

    private void updateDutyView(DutyView dutyView, DutyType dutyType, long time) {
        dutyView.setTime(time);
        dutyView.setDutyType(dutyType);
    }

    private void setListenersForPromtDialog(boolean clearListener) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PROMT_DIALOG);
        if (fragment instanceof PromtDialog) {
            if (clearListener) {
                ((PromtDialog) fragment).setPromtDialogListener(null);
            } else {
                ((PromtDialog) fragment).setPromtDialogListener(mPromtDialogListener);
            }
        }
    }

    private PromtDialog.PromtDialogListener mPromtDialogListener = dutyType -> presenter.onDutyTypeSelected(dutyType);
}
