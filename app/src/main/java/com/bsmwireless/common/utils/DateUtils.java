package com.bsmwireless.common.utils;

import android.content.Context;

import com.bsmwireless.common.App;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.data.network.NtpClientManager;

import java.text.DateFormat;
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
    public static final int SEC_IN_HOUR = MIN_IN_HOUR * SEC_IN_MIN;
    public static final int SEC_IN_DAY = HOUR_IN_DAY * MIN_IN_HOUR * SEC_IN_MIN;
    public final static int MS_IN_MIN = SEC_IN_MIN * MS_IN_SEC;
    public final static int MS_IN_HOUR = MIN_IN_HOUR * MS_IN_MIN;
    public static final long MS_IN_DAY = MS_IN_SEC * SEC_IN_DAY;
    public static final long MS_IN_WEEK = MS_IN_DAY * 7;

    /**
     * @param zone user timezone for example "America/Los_Angeles"
     * @param time time in ms
     * @return formatted date according to the timezone
     */
    public static String getLocalDate(String zone, long time) {
        return getDateFormat(zone, "yyyy-MMM-dd HH:mm:ss z").format(new Date(time));
    }

    /**
     * @param zone     user timezone for example "America/Los_Angeles"
     * @param calendar calendar with set appropriate day.
     * @return start date in ms
     */
    public static long getStartDate(String zone, Calendar calendar) {
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        Calendar calendarWithTimezone = Calendar.getInstance(timeZone);
        calendarWithTimezone.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendarWithTimezone.set(Calendar.MILLISECOND, 0);
        long timeInMs = calendarWithTimezone.getTimeInMillis();
        return timeInMs - timeInMs % 1000;
    }

    /**
     * @param time in Unix ms
     * @return start date in ms
     */
    public static long getStartDayTimeInMs(String zone, long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone));
        calendar.setTimeInMillis(time);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
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
        calendar.set(Calendar.MILLISECOND, 999);
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
     * @return string with format time like "128:35:11"
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
    public static long convertTimeToLogDay(String zone, long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        dateFormat.setTimeZone(timeZone);
        String todayDate = dateFormat.format(time);
        return Long.parseLong(todayDate);
    }

    /**
     * @param timeZone user timezone object"
     * @param time     unix time in ms
     * @return long with format time like 20170708
     */
    public static long convertTimeToLogDay(TimeZone timeZone, long time) {
        String todayDate = getDateFormat(timeZone.getID(), "yyyyMMdd").format(time);
        return Long.parseLong(todayDate);
    }

    /**
     * @param timeZone user timezone object"
     * @param time     unix time in ms
     * @return long with format time like 07-07-09
     */
    public static String convertTimeToDDMMYY(TimeZone timeZone, long time) {
        return getDateFormat(timeZone.getID(), "dd-MM-yy").format(time);
    }

    /**
     * @param timeZone user timezone object"
     * @param time     unix time in ms
     * @return long with format time like 12-29-09
     */
    public static String convertTimeToMMDDYY(TimeZone timeZone, long time) {
        return getDateFormat(timeZone.getID(), "MM-dd-yy").format(time);
    }

    /**
     * @param timeZone user timezone object"
     * @param time     unix time in ms
     * @return long with format time like 11:12
     */
    public static String convertTimeToHHMM(TimeZone timeZone, long time) {
        return getDateFormat(timeZone.getID(), "HH:mm").format(time);
    }

    /**
     * @param daysAgo  days ago
     * @param timezone user timezone
     * @return long with format time like 20170708
     */
    public static long getLogDayForDaysAgo(int daysAgo, String timezone) {
        return DateUtils.convertTimeToLogDay(timezone, DateUtils.currentTimeMillis()
                - MS_IN_DAY * daysAgo);
    }

    /**
     * @param logDay long with format time like 20170708
     * @return long unix time in ms
     */
    public static long convertLogDayToUnixMs(long logDay, TimeZone timeZone) {
        Date date = null;
        try {
            date = getDateFormat(timeZone.getID(), "yyyyMMdd").parse(String.valueOf(logDay));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * @param logday long with format time like 20170708
     * @param zone   timezone
     * @return long unix time in ms
     */
    public static long getStartDayTimeInMs(long logday, String zone) throws ParseException {
        Date date = getDateFormat(zone, "yyyyMMdd").parse(String.valueOf(logday));
        return date.getTime();
    }

    /**
     * @param time long unix time in ms
     * @return string with format time like "12:35 AM"
     */
    public static String convertTimeToAMPMString(long time, String timezone) {
        return getDateFormat(timezone, "hh:mm:ss aaa").format(time);
    }

    /**
     * @param time string with format time like "12:35 AM"
     * @param day  current day time
     * @return long unix time in ms
     */
    public static Long convertStringAMPMToTime(String time, long day, String timezone) {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(timezone);
            // Parse hour of day and minute
            Date date = getDateFormat(timezone, "hh:mm:ss aaa").parse(time);
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTime(date);

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);

            // Time of day
            calendar.setTimeInMillis(day);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, seconds);
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

    /**
     * @return real time which is sync with the ntp server
     */
    public static long currentTimeMillis() {
        AppComponent appComponent = App.getComponent();
        if (appComponent == null) {
            return System.currentTimeMillis();
        }

        NtpClientManager ntpClientManager = appComponent.ntpClientManager();
        long realTimeInMillisecondsDiff = ntpClientManager.getRealTimeInMillisDiff();
        return System.currentTimeMillis() + realTimeInMillisecondsDiff;
    }

    public static String convertToFullTime(String timezone, Date date) {
        return getDateFormat(timezone, "HH:mm MMM dd, yyyy").format(date);
    }

    /**
     * @param durations calculated durations in ms
     * @param isToday   if calculated day is current (no fix needed)
     * @return rounded to min durations
     */
    public static long[] getRoundedDurations(long[] durations, boolean isToday) {
        int index = 0;
        long dif = MS_IN_DAY;
        for (int i = 0; i < durations.length; i++) {
            //round duration
            durations[i] = durations[i] / MS_IN_MIN * MS_IN_MIN;

            //find non-zero duty
            if (durations[i] > 0) {
                index = i;
            }

            //calculate round error
            dif -= durations[i];
        }

        if (!isToday) {
            durations[index] += dif;
        }

        return durations;
    }

    private static DateFormat getDateFormat(String timezone, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat;
    }
}
