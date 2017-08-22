package com.bsmwireless.common.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import app.bsmuniversal.com.R;

public class DateUtils {
    public static final int MS_IN_SEC = 1000;
    public static final int SEC_IN_MIN = 60;
    public static final int MIN_IN_HOUR = 60;
    public static final int HOUR_IN_DAY = 24;
    public static final int SEC_IN_DAY = HOUR_IN_DAY * MIN_IN_HOUR * SEC_IN_MIN;
    private final static int MS_IN_MIN = SEC_IN_MIN * MS_IN_SEC;
    private final static int MS_IN_HOUR = MIN_IN_HOUR * MS_IN_MIN;
    public static final long MS_IN_DAY = MS_IN_SEC * SEC_IN_DAY;
    public static final long MS_IN_WEEK = MS_IN_DAY * 7;

    /**
     * @param zone user timezone for example "America/Los_Angeles"
     * @param time time in ms
     * @return formatted date according to the timezone
     */
    public static String getLocalDate(String zone, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss z", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(zone));

        return simpleDateFormat.format(new Date(time));
    }

    /**
     * @param zone  user timezone for example "America/Los_Angeles"
     * @param day   day in month
     * @param month month (0 is for Jan)
     * @param year  year
     * @return start date in ms
     */
    public static long getStartDate(String zone, int day, int month, int year) {
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(year, month, day, 0, 0, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * @param zone  user timezone for example "America/Los_Angeles"
     * @param day   day in month
     * @param month month (0 is for Jan)
     * @param year  year
     * @return end date in ms
     */
    public static long getEndDate(String zone, int day, int month, int year) {
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis();
    }

    /**
     * @param time in Unix ms
     * @return start date in ms
     */
    public static long getStartDayTimeInMs(String zone, long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone));
        calendar.setTimeInMillis(time);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param time in Unix ms
     * @return end date in ms
     */
    public static long getEndDayTimeInMs(String zone, long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone));
        calendar.setTimeInMillis(time);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 59, 59);
        return calendar.getTimeInMillis();
    }

    /**
     * @param zone user timezone for example "America/Los_Angeles"
     * @return Full time zone like "GMT -4.0, Eastern Standard Time (Canada/Eastern)"
     */
    public static String getFullTimeZone(String zone, long time) {
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        long minutes = TimeUnit.MINUTES.convert(timeZone.getOffset(time), TimeUnit.MILLISECONDS);

        return String.format(Locale.US, "GMT %s.%s, %s (%s)",
                minutes / MIN_IN_HOUR,
                minutes % MIN_IN_HOUR,
                timeZone.getDisplayName(Locale.US),
                zone);
    }

    /**
     * @param time unix time in ms
     * @return string with format time like "128:35"
     */
    public static String convertTotalTimeInMsToStringTime(long time) {
        int hours = (int) (time / MS_IN_HOUR);
        int minutes = (int) ((time - hours * MS_IN_HOUR) / MS_IN_MIN);
        return String.format(Locale.US, "%02d:%02d", hours, minutes);
    }

    /**
     * @param time unix time in ms
     * @return string with format time like "128:35"
     */
    public static String convertTotalTimeInMsToFullStringTime(long time) {
        int hours = (int) (time / MS_IN_HOUR);
        int minutes = (int) ((time - hours * MS_IN_HOUR) / MS_IN_MIN);
        int seconds = (int) ((time - hours * MS_IN_HOUR - minutes * MS_IN_MIN) / MS_IN_SEC);
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * @param time unix time in ms
     * @return string with format time like "2 hrs 35 mins"
     */
    public static String convertTimeInMsToDurationString(long time, Context context) {
        String hrs = context.getResources().getString(R.string.hours);
        String mins = context.getResources().getString(R.string.minutes);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        String duration = "";
        int hours = (int) (time / MS_IN_HOUR);
        int minutes = (int) ((time - hours * MS_IN_HOUR) / MS_IN_MIN);
        if (hours > 0) {
            duration = String.format(Locale.US, "%02d " + hrs + " ", hours);
        }
        return duration + String.format(Locale.US, "%02d " + mins, minutes);
    }

    /**
     * @param time unix time in ms
     * @return string with format time like "01:19:24"
     */
    public static String convertTimeInMsToDayTime(String timezone, long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        calendar.setTimeInMillis(time);
        return String.format(Locale.US, "%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    /**
     * @param zone user timezone for example "America/Los_Angeles"
     * @param time unix time in ms
     * @return long with format time like 20170708
     */
    public static long convertTimeToDayNumber(String zone, long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        calendar.setTimeZone(timeZone);
        calendar.setTimeInMillis(time);
        String todayDate = dateFormat.format(calendar.getTime());
        return Long.parseLong(todayDate);
    }

    /**
     * @param logday long with format time like 20170708
     * @return long unix time in ms
     */
    public static long convertDayNumberToUnixMs(long logday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(logday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * @param time long unix time in ms
     * @return string with format time like "12:35 AM"
     */
    public static String convertTimeToAMPMString(long time, String timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aaa");
        TimeZone timeZone = TimeZone.getTimeZone(timezone);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(time);
    }

    /**
     * @param time string with format time like "12:35 AM"
     * @param day current day time
     * @return long unix time in ms
     */
    public static Long convertStringAMPMToTime(String time, long day, String timezone) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aaa", Locale.US);
        TimeZone timeZone = TimeZone.getTimeZone(timezone);
        format.setTimeZone(timeZone);
        try {
            // Parse hour of day and minute
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTime(date);

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Time of day
            calendar.setTimeInMillis(day);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * @param time unix time in ms
     * @return string with format time like "Sunday, July 4"
     */
    public static String convertTimeInMsToDate(String timezone, long time) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

}
