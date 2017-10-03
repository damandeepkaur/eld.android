package com.bsmwireless.screens.carrieredit.fragments.edited;

import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.List;

public interface EditedEventsPresenter {

    void setView(EditedEventsView view);

    void onCalendarDaySelected(CalendarItem calendarItem);

    void updateDataForDay(long logDay);

    void approveEdits(List<EventLogModel> events, long logDay);

    void disapproveEdits(List<EventLogModel> events, long logDay);

    void markCalendarItems(List<CalendarItem> list);
}
