package com.bsmwireless.screens.logs;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.Collections;
import java.util.List;

public final class GraphModel {
    private long mStartDayTime;
    private ELDEvent prevDayEvent;
    private List<EventLogModel> mEventLogModels = Collections.emptyList();

    public GraphModel() {
    }

    public long getStartDayTime() {
        return mStartDayTime;
    }

    public void setStartDayTime(long startDayTime) {
        mStartDayTime = startDayTime;
    }

    public ELDEvent getPrevDayEvent() {
        return prevDayEvent;
    }

    public void setPrevDayEvent(ELDEvent prevDayEvent) {
        this.prevDayEvent = prevDayEvent;
    }

    public List<EventLogModel> getEventLogModels() {
        return mEventLogModels;
    }

    public void setEventLogModels(List<EventLogModel> eventLogModels) {
        mEventLogModels = eventLogModels;
    }
}
