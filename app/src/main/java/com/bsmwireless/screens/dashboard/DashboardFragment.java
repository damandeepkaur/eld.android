package com.bsmwireless.screens.dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.ViewUtils;
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

public final class DashboardFragment extends BaseFragment implements DashboardView {
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

    @BindView(R.id.dashboard_indicator_layout)
    CardView mIndicatorView;

    @BindView(R.id.dashboard_indicator)
    AppCompatButton mIndicatorButton;

    @BindView(R.id.dashboard_indicator_title)
    TextView mIndicatorTitleText;

    @BindView(R.id.dashboard_status_layout)
    LinearLayout mStatusLayout;

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

    private AlertDialog mAlertDialog;

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
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

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
        mPresenter.onDutyClick();
    }

    @OnClick(R.id.dashboard_indicator)
    public void onClearClick() {
        mNavigateView.changeDutyType(mDutyType == DutyType.PERSONAL_USE ? DutyType.OFF_DUTY : DutyType.ON_DUTY, null);
    }

    @OnClick(R.id.dashboard_pu_status)
    public void onPersonalUseClick() {
        changeToSpecialStatus(DutyType.PERSONAL_USE);
    }

    @OnClick(R.id.dashboard_ym_status)
    public void onYardMovesClick() {
        changeToSpecialStatus(DutyType.YARD_MOVES);
    }

    private void changeToSpecialStatus(DutyType dutyType) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.default_margin);

        View view = LayoutInflater.from(new ContextThemeWrapper(mContext, R.style.TextInputEditText)).inflate(R.layout.view_comment, null);
        view.setPadding(padding, padding, padding, 0);

        mAlertDialog = new AlertDialog.Builder(mContext)
                .setTitle(dutyType.getName())
                .setMessage(dutyType == DutyType.PERSONAL_USE ? R.string.pu_dialog_message : R.string.ym_dialog_message)
                .setView(view)
                .setPositiveButton(R.string.special_status_dialog_accept, null)
                .setNegativeButton(R.string.special_status_dialog_cancel, null)
                .setOnDismissListener(dialog -> {
                    if (getActivity() != null) {
                        ViewUtils.hideSoftKeyboard(getActivity());
                    }
                })
                .show();

        TextInputLayout inputLayout = ButterKnife.findById(mAlertDialog, R.id.comment_layout);
        TextInputEditText inputText = ButterKnife.findById(mAlertDialog, R.id.comment);

        inputLayout.setHintEnabled(false);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputLayout.setError(null);
            }
        });

        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            String comment = inputText.getText().toString();
            Error error = mPresenter.validateComment(comment);

            if (error == Error.INVALID_COMMENT_LENGTH) {
                inputLayout.setError(mContext.getString(R.string.edit_event_comment_length_error));
            } else if (error == Error.INVALID_COMMENT) {
                inputLayout.setError(mContext.getString(R.string.edit_event_comment_error));
            } else {
                mNavigateView.changeDutyType(dutyType, comment);
                mAlertDialog.dismiss();
            }
        });
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

        mStatusLayout.setVisibility(mPresenter.isConnected() ? View.VISIBLE : View.GONE);

        initTimer();
    }

    @Override
    public void showDutyTypeDialog() {
        mNavigateView.showDutyTypeDialog(mDutyType);
    }

    @Override
    public void showNotInVehicleDialog() {
        mNavigateView.showNotInVehicleDialog();
    }

    private void initTimer() {
        //TODO: set timer for cycle when cycle time is read
        if (mDutyType != DutyType.OFF_DUTY && mDutyType != DutyType.PERSONAL_USE) {
            mHandler.post(mTimerTask);
        }
    }

    private void stopTimer() {
        mHandler.removeCallbacks(mTimerTask);
    }

    private void updateDutyView(DutyView dutyView, DutyType dutyType) {
        if (dutyView != null) {
            dutyView.setDutyType(dutyType);
            dutyView.setTime(mPresenter.getDutyTypeTime(dutyView.getDutyType()));
        }
    }

    private void updateIndicator(DutyType dutyType) {
        if (dutyType == DutyType.PERSONAL_USE || dutyType == DutyType.YARD_MOVES) {
            int color = ContextCompat.getColor(getContext(), dutyType.getColor());
            String currentDuty = mContext.getString(dutyType.getName());
            mIndicatorDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
            mIndicatorTitleText.setText(String.format(Locale.US, mContext.getString(R.string.duty_indicator_title), currentDuty));
            mIndicatorButton.setText(String.format(Locale.US, mContext.getString(R.string.duty_indicator), currentDuty));
            mIndicatorButton.setSupportBackgroundTintList(ColorStateList.valueOf(color));
            mIndicatorView.setVisibility(View.VISIBLE);
            mStatusLayout.setVisibility(View.GONE);
        } else {
            mIndicatorView.setVisibility(View.GONE);
            mStatusLayout.setVisibility(View.VISIBLE);
        }
    }
}
