package com.bsmwireless.widgets.logs.graphview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.common.FontTextView;

import java.util.ArrayList;
import java.util.List;

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

    public void setELDEvents(List<ELDEvent> logs, long startDayTime) {
        if (mELDGraphView != null) {
            mELDGraphView.setLogs(filterOffNotDutyTypeEvents(logs), startDayTime);
        }
    }

    public void setHOSTimerOnDuty(String time) {
        mHOSTimerOnDuty.setText(time);
    }

    public void setHOSTimerOffDuty(String time) {
        mHOSTimerOffDuty.setText(time);
    }

    public void setHOSTimerSleeperBerth(String time) {
        mHOSTimerSleeperBerth.setText(time);
    }

    public void setHOSTimerDriving(String time) {
        mHOSTimerDriving.setText(time);
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

    private List<ELDEvent> filterOffNotDutyTypeEvents(List<ELDEvent> events) {
        List<ELDEvent> result = new ArrayList<>();
        for (ELDEvent event : events) {
            if (event.getEventType().equals(ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue())) {
                result.add(event);
            }
        }
        return result;
    }
}
