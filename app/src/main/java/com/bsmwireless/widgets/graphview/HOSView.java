package com.bsmwireless.widgets.graphview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsmwireless.models.DriverLog;

import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HOSView extends LinearLayout {

    private Unbinder mUnbinder;

    private View mRootView;

    @BindView(R.id.hos_graph)
    HOSGraphView mHOSGraphView;
    @BindView(R.id.plane_on_duty)
    HOSTimerView mHOSTimerOnDuty;
    @BindView(R.id.plane_off_duty)
    HOSTimerView mHOSTimerOffDuty;
    @BindView(R.id.plane_sleeper_berth)
    HOSTimerView mHOSTimerSleeperBerth;
    @BindView(R.id.plane_driving)
    HOSTimerView mHOSTimerDriving;
    @BindView(R.id.total_time_textview)
    TextView mTotalTimeTV;

    public HOSView(Context context) {
        super(context);
        init(context);
    }

    public HOSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HOSView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void updateGraph(List<DriverLog> logs) {
        mHOSGraphView.setLogs(logs);
    }

    public void setHOSTimerOnDuty(String time) {
        mHOSTimerOnDuty.setTime(time);
    }

    public void setHOSTimerOffDuty(String time) {
        mHOSTimerOffDuty.setTime(time);
    }

    public void setHOSTimerSleeperBerth(String time) {
        mHOSTimerSleeperBerth.setTime(time);
    }

    public void setHOSTimerDriving(String time) {
        mHOSTimerDriving.setTime(time);
    }

    public void setTotalTime(String time) {
        mTotalTimeTV.setText(time);
    }

    private void init(Context context) {
        mRootView = inflate(context,
                            getOrientation() == VERTICAL ? R.layout.hos_view_vertical : R.layout.hos_view_horizontal,
                            this);
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
