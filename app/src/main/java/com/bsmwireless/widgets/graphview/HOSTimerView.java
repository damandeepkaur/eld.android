package com.bsmwireless.widgets.graphview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.common.FontTextView;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HOSTimerView extends RelativeLayout {

    private Unbinder mUnbinder;

    private View mRootView;

    @BindView(R.id.type_textview)
    FontTextView mTypeTV;
    @BindView(R.id.time_textview)
    FontTextView mHoursTV;

    private int mType;
    private boolean mShowHeader;

    public HOSTimerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public HOSTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public HOSTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void setTime(String time) {
        mHoursTV.setText(time);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mRootView = inflate(context, R.layout.hos_timer_view, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HOSTimerView, defStyleAttr, 0);
        try {
            mType = typedArray.getInteger(R.styleable.HOSTimerView_duty, 0);
            mShowHeader = typedArray.getBoolean(R.styleable.HOSTimerView_showHeader, true);

            mRootView.setBackgroundColor(ContextCompat.getColor(getContext(), DutyType.getColorById(mType)));

            mTypeTV.setText(getResources().getString(DutyType.getNameById(mType)));
            mTypeTV.setVisibility(mShowHeader ? VISIBLE : GONE);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    protected void onDetachedFromWindow() {
        mUnbinder.unbind();
        super.onDetachedFromWindow();
    }
}
