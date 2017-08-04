package com.bsmwireless.widgets.logs.graphview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.common.FontTextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GraphLayout extends LinearLayout {

    ELDGraphView mELDGraphView;
    @BindView(R.id.time_on)
    FontTextView mHOSTimerOnDuty;
    @BindView(R.id.time_off)
    FontTextView mHOSTimerOffDuty;
    @BindView(R.id.time_s)
    FontTextView mHOSTimerSleeperBerth;
    @BindView(R.id.time_dr)
    FontTextView mHOSTimerDriving;

    private Unbinder mUnbinder;
    private View mRootView;
    private List<ELDEvent> mLogs;

    public GraphLayout(Context context) {
        super(context);
        init(context);
    }

    public GraphLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GraphLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setELDEvents(List<ELDEvent> logs) {
        mLogs = logs;
        if (mELDGraphView != null) {
            mELDGraphView.setLogs(logs);
        }
    }

    public void setHOSTimerOnDuty(Long time) {
        String t = convertTimeToString(time);
        mHOSTimerOnDuty.setText(t);
    }

    public void setHOSTimerOffDuty(Long time) {
        mHOSTimerOffDuty.setText(convertTimeToString(time));
    }

    public void setHOSTimerSleeperBerth(long time) {
        mHOSTimerSleeperBerth.setText(convertTimeToString(time));
    }

    public void setHOSTimerDriving(Long time) {
        mHOSTimerDriving.setText(convertTimeToString(time));
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.eld_graph, this);
        mELDGraphView = (ELDGraphView) mRootView.findViewById(R.id.hos_graph);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUnbinder.unbind();
    }

    private String convertTimeToString(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        return String.format(Locale.US, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }
}
