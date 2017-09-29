package com.bsmwireless.screens.carrieredit.fragments.edited;

import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.List;

/**
 * Created by osminin on 22.09.2017.
 */

public interface EditedEventsView {
    void setEvents(List<EventLogModel> events);
}
