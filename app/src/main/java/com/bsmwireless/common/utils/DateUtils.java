package com.bsmwireless.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private final static int MINUTES_IN_HOUR = 60;

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
    public static long getStartDayTimeInMs(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
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
                minutes / MINUTES_IN_HOUR,
                minutes % MINUTES_IN_HOUR,
                timeZone.getDisplayName(Locale.US),
                zone);
    }

    /**
     * @param time unix time in ms
     * @return string with format time like "12:35"
     */
    public static String convertTimeInMsToStringTime(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        return String.format(Locale.US, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
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
}
