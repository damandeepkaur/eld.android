package com.bsmwireless.widgets.dashboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.widgets.alerts.DutyType;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public final class DutyView extends CardView {
    @BindView(R.id.duty_time_title)
    TextView mTimeTitleText;

    @BindView(R.id.duty_time)
    TextView mTimeText;

    @Nullable
    @BindView(R.id.duty_selection_layout)
    View mSelectionLayout;

    @Nullable
    @BindView(R.id.duty_selection_title)
    TextView mSelectionText;

    @Nullable
    @BindView(R.id.tap_to_change_status_title)
    TextView mTapToChangeTitle;

    @Nullable
    @BindView(R.id.duty_divider)
    View mDutyDivider;

    private Unbinder mUnbinder;
    private DutyType mDutyType;
    private boolean mIsLarge = false;

    public DutyView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DutyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DutyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        if (mSelectionLayout != null) {
            mSelectionLayout.setOnClickListener(listener);
        }
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DutyView, defStyleAttr, 0);
        DutyType dutyType = DutyType.values()[typedArray.getInt(R.styleable.DutyView_dutyType, 0)];
        mIsLarge = typedArray.getInt(R.styleable.DutyView_size, 0) == 0;
        typedArray.recycle();

        View rootView = inflate(context, mIsLarge ? R.layout.view_duty_large : R.layout.view_duty_normal, this);
        mUnbinder = ButterKnife.bind(rootView, this);

        setDutyType(dutyType);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUnbinder.unbind();
    }

    public void setDutyType(DutyType dutyType) {
        mDutyType = dutyType;

        int color = ContextCompat.getColor(getContext(), dutyType.getColor());
        String name = getResources().getString(dutyType.getName());

        if (mSelectionLayout != null && mSelectionText != null) {
            mSelectionLayout.getBackground().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
            mSelectionText.setTextColor(color);
            mSelectionText.setText(name);
            mSelectionText.setCompoundDrawablesWithIntrinsicBounds(0, 0, dutyType.getIcon(), 0);
        }

        int id = R.string.duty_cycle_time;

        switch (dutyType) {
            case ON_DUTY:
            case YARD_MOVES:
                id = R.string.duty_on_duty_time;
                break;

            case DRIVING:
                id = R.string.duty_driving_time;
                break;

            case SLEEPER_BERTH:
                id = R.string.duty_sleeper_berth_time;
                break;
        }

        mTimeTitleText.setText(getResources().getString(id));
    }

    public DutyType getDutyType() {
        return mDutyType;
    }

    public void setTime(long time) {
        mTimeText.setText(mIsLarge ? DateUtils.convertTotalTimeInMsToFullStringTime(time) : DateUtils.convertTotalTimeInMsToStringTime(time));
    }

    public void setCanChangingSatusView(boolean canChange) {
        if (mIsLarge) {

            if (mTapToChangeTitle == null || mDutyDivider == null) {
                return;
            }

            mTapToChangeTitle.setVisibility(canChange ? VISIBLE : GONE);
            mDutyDivider.setVisibility(canChange ? VISIBLE : GONE);
        }
    }
}
