package com.bsmwireless.widgets.logs.graphview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.logs.HOSTimesModel;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.common.FontTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;

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
    private long mStartDayUnixTimeInMs;
    private ELDEvent mPrevDayEvent;

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

    public void setELDEvents(List<EventLogModel> logs) {
        if (mELDGraphView != null) {
            if (!logs.isEmpty() && (mPrevDayEvent == null)) {
                mStartDayUnixTimeInMs = DateUtils.getStartDayTimeInMs(logs.get(0).getDriverTimezone(),
                        logs.get(0).getEventTime());
            }
            updateHOSTimes(logs, mStartDayUnixTimeInMs);
            List<DrawableLog> drawableLog = prepareEvents(logs, mStartDayUnixTimeInMs);
            mELDGraphView.setLogs(drawableLog, mStartDayUnixTimeInMs);
        }
    }

    public void setPrevDayEvent(ELDEvent event) {
        if (mELDGraphView != null) {
            mPrevDayEvent = event;
            if (event != null) {
                mStartDayUnixTimeInMs = event.getEventTime();
            }
        }
    }

    private void updateHOSTimes(final List<EventLogModel> events, final long startDayTime) {
        HOSTimesModel hosTimesModel = new HOSTimesModel();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long endDayTime = startDayTime + MS_IN_DAY;

        List<DutyTypeManager.DutyTypeCheckable> checkableEvents = new ArrayList<>(events);
        if (mPrevDayEvent != null) {
            checkableEvents.add(0, mPrevDayEvent);
        }

        long[] times = DutyTypeManager.getDutyTypeTimes(checkableEvents, startDayTime,
                Math.min(endDayTime, currentTime));

        hosTimesModel.setSleeperBerthTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.SLEEPER_BERTH.ordinal()]));
        hosTimesModel.setDrivingTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.DRIVING.ordinal()]));
        hosTimesModel.setOffDutyTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.OFF_DUTY.ordinal()]));
        hosTimesModel.setOnDutyTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.ON_DUTY.ordinal()]));
        setHOSTimer(hosTimesModel);
    }

    public void setHOSTimer(HOSTimesModel hosTimes) {
        setHOSTimerSleeperBerth(hosTimes.getSleeperBerthTime());
        setHOSTimerDriving(hosTimes.getDrivingTime());
        setHOSTimerOffDuty(hosTimes.getOffDutyTime());
        setHOSTimerOnDuty(hosTimes.getOnDutyTime());
    }

    public void setHOSTimerOnDuty(String time) {
        if (mHOSTimerOnDuty != null) {
            mHOSTimerOnDuty.setText(time);
        }
    }

    public void setHOSTimerOffDuty(String time) {
        if (mHOSTimerOffDuty != null) {
            mHOSTimerOffDuty.setText(time);
        }
    }

    public void setHOSTimerSleeperBerth(String time) {
        if (mHOSTimerSleeperBerth != null) {
            mHOSTimerSleeperBerth.setText(time);
        }
    }

    public void setHOSTimerDriving(String time) {
        if (mHOSTimerDriving != null) {
            mHOSTimerDriving.setText(time);
        }
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

    private List<DrawableLog> prepareEvents(List<EventLogModel> events, long startDayTime) {
        List<DrawableLog> result = DrawableLog.convertToDrawableLog(events);

        if (mPrevDayEvent != null) {
            long duration = result.isEmpty() ? MS_IN_DAY : result.get(0).getEventTime() - startDayTime;
            DutyType type = DutyType.getTypeByCode(ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue(), mPrevDayEvent.getEventCode());
            DrawableLog log = new DrawableLog(type, startDayTime, duration);
            result.add(0, log);
        }
        return result;
    }
}
