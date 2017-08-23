package com.bsmwireless.widgets.dashboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.Locale;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_SEC;

public class DutyView extends CardView {
    @BindView(R.id.duty_time_title)
    TextView mTimeTitleText;

    @BindView(R.id.duty_time)
    TextView mTimeText;

    @BindView(R.id.duty_status)
    ImageView mStatusImage;

    @BindView(R.id.duty_progress)
    ProgressBar mProgressBar;

    @Nullable
    @BindView(R.id.duty_selection_layout)
    View mSelectionLayout;

    @Nullable
    @BindView(R.id.duty_selection_title)
    TextView mSelectionText;

    private Unbinder mUnbinder;
    private DutyType mDutyType;

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
        boolean isLarge = typedArray.getInt(R.styleable.DutyView_size, 0) == 0;
        typedArray.recycle();

        View rootView = inflate(context, isLarge ? R.layout.view_duty_large : R.layout.view_duty_normal, this);
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

        mProgressBar.getProgressDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

        if (mSelectionLayout != null && mSelectionText != null) {
            mSelectionLayout.getBackground().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
            mSelectionText.setTextColor(color);
            mSelectionText.setText(name);
        }

        mStatusImage.setImageResource(dutyType.getIcon());
        mTimeTitleText.setText(String.format(Locale.US, "%s %s", name, "time"));
    }

    public DutyType getDutyType() {
        return mDutyType;
    }

    public void setTime(long time) {
        mTimeText.setText(DateUtils.convertTotalTimeInMsToFullStringTime(time));
        mProgressBar.setProgress((int) (time / MS_IN_SEC));
    }
}
