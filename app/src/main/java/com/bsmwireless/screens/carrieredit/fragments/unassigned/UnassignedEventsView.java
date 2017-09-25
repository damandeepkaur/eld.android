package com.bsmwireless.screens.carrieredit.fragments.unassigned;

import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.List;

/**
 * Created by osminin on 22.09.2017.
 */

public interface UnassignedEventsView {
    void setEvents(List<EventLogModel> events);

    void removeEvent(int position);
}
