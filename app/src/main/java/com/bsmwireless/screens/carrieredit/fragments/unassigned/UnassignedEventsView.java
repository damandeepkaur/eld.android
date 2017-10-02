package com.bsmwireless.screens.carrieredit.fragments.unassigned;

import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.List;

public interface UnassignedEventsView {
    void setEvents(List<EventLogModel> events);

    void removeEvent(int position);
}
