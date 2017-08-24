package com.bsmwireless.screens.dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.dashboard.dagger.DaggerDashboardComponent;
import com.bsmwireless.screens.dashboard.dagger.DashboardModule;
import com.bsmwireless.screens.navigation.NavigateView;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.dashboard.DutyView;

import java.util.Locale;

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

    @BindView(R.id.dashboard_break)
    DutyView mBreakDutyView;

    @BindView(R.id.dashboard_workday)
    DutyView mWorkdayDutyView;

    @BindView(R.id.dashboard_cycle)
    DutyView mCycleDutyView;

    @BindView(R.id.dashboard_layout)
    View mLayout;

    @BindView(R.id.dashboard_indicator_layout)
    CardView mIndicatorView;

    @BindView(R.id.dashboard_indicator)
    TextView mIndicatorText;

    @BindView(R.id.dashboard_indicator_title)
    TextView mIndicatorTitleText;

    private Drawable mIndicatorDrawable;

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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        DaggerDashboardComponent.builder().appComponent(App.getComponent()).dashboardModule(new DashboardModule(this)).build().inject(this);

        mIndicatorDrawable = mIndicatorTitleText.getCompoundDrawables()[0];

        return view;
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

    @OnClick(R.id.dashboard_indicator)
    public void onClearClick() {
        mNavigateView.changeDutyType(mDutyType == DutyType.PERSONAL_USE ? DutyType.OFF_DUTY : DutyType.ON_DUTY);
    }

    @Override
    public void setDutyType(DutyType dutyType) {
        mDutyType = dutyType;

        stopTimer();

        updateDutyView(mCurrentDutyView, dutyType);
        updateDutyView(mBreakDutyView, dutyType == DutyType.SLEEPER_BERTH ? DutyType.DRIVING : DutyType.SLEEPER_BERTH);
        updateDutyView(mWorkdayDutyView, dutyType == DutyType.ON_DUTY || dutyType == DutyType.YARD_MOVES ? DutyType.DRIVING : DutyType.ON_DUTY);
        updateDutyView(mCycleDutyView, dutyType == DutyType.OFF_DUTY || dutyType == DutyType.PERSONAL_USE ? DutyType.DRIVING : DutyType.OFF_DUTY);

        updateIndicator(dutyType);

        initTimer();
    }

    private void initTimer() {
        if (mDutyType != DutyType.OFF_DUTY && mDutyType != DutyType.PERSONAL_USE) {
            mHandler.post(mTimerTask);
        }
    }

    private void stopTimer() {
        mHandler.removeCallbacks(mTimerTask);
    }

    private void updateDutyView(DutyView dutyView, DutyType dutyType) {
        dutyView.setDutyType(dutyType);
        dutyView.setTime(mPresenter.getDutyTypeTime(dutyView.getDutyType()));
    }

    private void updateIndicator(DutyType dutyType) {
        if (dutyType == DutyType.PERSONAL_USE || dutyType == DutyType.YARD_MOVES) {
            int color = ContextCompat.getColor(getContext(), dutyType.getColor());
            String newDuty = mContext.getString(dutyType == DutyType.PERSONAL_USE ? DutyType.OFF_DUTY.getName() : DutyType.ON_DUTY.getName());
            String currentDuty = mContext.getString(dutyType.getName());
            mIndicatorDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
            mIndicatorTitleText.setText(String.format(Locale.US, mContext.getString(R.string.duty_indicator_title), currentDuty));
            mIndicatorText.setText(String.format(Locale.US, mContext.getString(R.string.duty_indicator), newDuty));
            mIndicatorText.setBackgroundColor(color);
            mIndicatorView.setVisibility(View.VISIBLE);
        } else {
            mIndicatorView.setVisibility(View.GONE);
        }
    }
}
