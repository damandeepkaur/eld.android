package com.bsmwireless.screens.carrieredit.fragments.edited;

import android.content.Context;

import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.logs.GraphModel;
import com.bsmwireless.screens.logs.LogHeaderModel;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.List;

public interface EditedEventsView {
    void setEvents(List<EventLogModel> events);

    void setLogSheetHeaders(List<LogSheetHeader> logs);

    void updateGraph(GraphModel graphModel);

    void setLogHeader(LogHeaderModel logHeader);

    void updateCalendarItems(List<CalendarItem> calendarItems);

    Context getContext();

    CalendarItem getSelectedDay();

    void showConnectionError();
}
