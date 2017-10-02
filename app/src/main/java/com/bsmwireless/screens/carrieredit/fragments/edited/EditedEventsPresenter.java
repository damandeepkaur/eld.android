package com.bsmwireless.screens.carrieredit.fragments.edited;

import com.bsmwireless.widgets.logs.calendar.CalendarItem;

public interface EditedEventsPresenter {

    void setView(EditedEventsView view);

    void onCalendarDaySelected(CalendarItem calendarItem);

    void updateDataForDay(long logDay);

}
