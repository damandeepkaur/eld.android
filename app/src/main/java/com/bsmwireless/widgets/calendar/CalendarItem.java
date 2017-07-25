package com.bsmwireless.widgets.calendar;

import android.support.annotation.Nullable;

import com.bsmwireless.models.LogSheetHeader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarItem {

    public static final int ONE_DAY_MS = 24 * 60 * 60 * 1000;

    private Calendar mCalendar = Calendar.getInstance();

    private LogSheetHeader mAssociatedLog;
    private Long mDate;

    public CalendarItem(Long date, LogSheetHeader log) {
        mAssociatedLog = log;
        mDate = date;
        mCalendar.setTime(new Date(mDate));
    }

    public String getMonth() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.US);
        return monthFormat.format(mCalendar.getTime());
    }

    public int getDay() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public String getDayOfWeek() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        return dayFormat.format(mCalendar.getTime());
    }

    public Long getDate() {
        return mDate;
    }

    @Nullable
    public LogSheetHeader getAssociatedLog() {
        return mAssociatedLog;
    }

    public void setAssociatedLog(LogSheetHeader log) {
        mAssociatedLog = log;
    }

    public boolean isDateValid(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        return calendar.get(Calendar.DAY_OF_YEAR) == mCalendar.get(Calendar.DAY_OF_YEAR);
    }
}
