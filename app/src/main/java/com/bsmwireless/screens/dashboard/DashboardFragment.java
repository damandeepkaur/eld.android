package com.bsmwireless.screens.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.dashboard.dagger.DaggerDashboardComponent;
import com.bsmwireless.screens.dashboard.dagger.DashboardModule;
import com.bsmwireless.screens.navigation.NavigateView;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.dashboard.DutyView;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DashboardFragment extends BaseFragment implements DashboardView {
    private static final int TIMER_DELAY = 1000;

    @Inject
    DashboardPresenter mPresenter;

    @BindView(R.id.dashboard_current)
    DutyView mCurrentDutyView;

    @BindView(R.id.dashboard_left)
    DutyView mLeftDutyView;

    @BindView(R.id.dashboard_right)
    DutyView mRightDutyView;

    @BindView(R.id.dashboard_layout)
    View mLayout;

    private View mRootView;

    private Unbinder mUnbinder;
    private NavigateView mNavigateView;

    private DutyType mDutyType = DutyType.OFF_DUTY;

    private Handler mHandler = new Handler();
    private Runnable mTimerTask = new Runnable() {
        @Override
        public void run() {
            mCurrentDutyView.setTime(mPresenter.getDutyTypeTime(mDutyType));
            mHandler.postDelayed(this, TIMER_DELAY);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigateView) {
            mNavigateView = (NavigateView) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NavigateView");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);

        DaggerDashboardComponent.builder().appComponent(App.getComponent()).dashboardModule(new DashboardModule(this)).build().inject(this);

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mPresenter.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
        stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @OnClick(R.id.dashboard_current)
    public void onDutyClick() {
        mNavigateView.showDutyDialog();
    }

    @Override
    public void setDutyType(DutyType dutyType) {
        mDutyType = dutyType;

        stopTimer();

        updateDutyView(mCurrentDutyView, dutyType);
        updateDutyView(mLeftDutyView, mCurrentDutyView.getDutyType() == DutyType.ON_DUTY ? DutyType.DRIVING : DutyType.ON_DUTY);
        updateDutyView(mRightDutyView, mCurrentDutyView.getDutyType() == DutyType.SLEEPER_BERTH ? DutyType.DRIVING : DutyType.SLEEPER_BERTH);

        initTimer();
    }

    private void initTimer() {
        if (mDutyType != DutyType.OFF_DUTY) {
            mHandler.post(mTimerTask);
        }
    }

    private void stopTimer() {
        mHandler.removeCallbacks(mTimerTask);
    }

    private void updateDutyView(DutyView dutyView, DutyType dutyType) {
        switch (dutyType) {
            case OFF_DUTY:
            case DRIVING:
            case PERSONAL_USE:
                dutyView.setDutyType(DutyType.DRIVING);
                break;

            case ON_DUTY:
            case YARD_MOVES:
                dutyView.setDutyType(DutyType.ON_DUTY);
                break;

            case SLEEPER_BERTH:
                dutyView.setDutyType(DutyType.SLEEPER_BERTH);
                break;
        }

        dutyView.setTime(mPresenter.getDutyTypeTime(dutyView.getDutyType()));
    }
}
