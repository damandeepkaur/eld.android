package com.bsmwireless.widgets.logs.calendar;

import android.support.annotation.Nullable;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.LogSheetHeader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarItem {

    public static final int ONE_DAY_MS = 24 * 60 * 60 * 1000;

    private static final SimpleDateFormat mMonthFormat = new SimpleDateFormat("MMMM", Locale.US);
    private static final SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE", Locale.US);

    private Calendar mCalendar = Calendar.getInstance();

    private LogSheetHeader mAssociatedLog;
    private Long mTimestamp;

    private String mMonth;
    private String mDay;
    private String mDayOfWeek;

    public CalendarItem(Long timestamp, LogSheetHeader log) {
        mAssociatedLog = log;
        mTimestamp = timestamp;
        mCalendar.setTime(new Date(mTimestamp));

        mDay = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));
        mMonth = mMonthFormat.format(mCalendar.getTime());
        mDayOfWeek = mDayFormat.format(mCalendar.getTime()).substring(0, 3).toUpperCase();
    }

    public String getMonth() {
        return mMonth;
    }

    public String getDay() {
        return mDay;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public Long getTimestamp() {
        return mTimestamp;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    @Nullable
    public LogSheetHeader getAssociatedLogSheet() {
        return mAssociatedLog;
    }

    public void setAssociatedLog(LogSheetHeader log) {
        mAssociatedLog = log;
    }

    public boolean isCurrentDay(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        return calendar.get(Calendar.DAY_OF_YEAR) == mCalendar.get(Calendar.DAY_OF_YEAR);
    }
}
