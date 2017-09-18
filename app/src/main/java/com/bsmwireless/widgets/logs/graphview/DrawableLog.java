package com.bsmwireless.widgets.logs.graphview;

import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

public final class DrawableLog {
    private DutyType mType;
    private long mTime;
    private long mDuration;

    public DrawableLog() {
    }

    public DrawableLog(DutyType type, long time, long duration) {
        mType = type;
        mTime = time;
        mDuration = duration;
    }

    public static List<DrawableLog> convertToDrawableLog(List<EventLogModel> events) {
        List<DrawableLog> result = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            EventLogModel event = events.get(i);
            DutyType dutyType = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());
            DrawableLog log;
            if (event.isActive() && event.isDutyEvent()) {
                if (DutyType.CLEAR.equals(dutyType) || DutyType.CLEAR_PU.equals(dutyType)
                        || DutyType.CLEAR_YM.equals(dutyType)) {
                    DutyType type = event.getDutyType();
                    log = new DrawableLog(type, event.getEventTime(), event.getDuration());
                } else {
                    log = new DrawableLog(dutyType, event.getEventTime(), event.getDuration());
                }
                result.add(log);
            }
        }
        return result;
    }

    public Integer getEventType() {
        return mType.getType();
    }

    public DutyType getEventDutyType() {
        return mType;
    }

    public Integer getEventCode() {
        return mType.getOriginalCode() - 1;
    }

    public Long getEventTime() {
        return mTime;
    }

    public boolean isSpecialStatus() {
        return mType.equals(DutyType.PERSONAL_USE) || mType.equals(DutyType.YARD_MOVES);
    }

    public long getDuration() {
        return mDuration;
    }
}
