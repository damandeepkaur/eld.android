package com.bsmwireless.screens.lockscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.dashboard.DutyView;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LockScreenActivity extends BaseActivity implements LockScreenView {

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
        return new Intent(context, LockScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);
        App.getComponent().lockScreenBuilder().view(this).build().inject(this);
        mCurrentDutyView.setCanChangingSatusView(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
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

    }

    @OnClick(R.id.switch_co_driver_button)
    void switchCoDriverClick() {
        presenter.switchCoDriver();
    }

    private void updateDutyView(DutyView dutyView, DutyType dutyType, long time) {
        dutyView.setTime(time);
        dutyView.setDutyType(dutyType);
    }
}
