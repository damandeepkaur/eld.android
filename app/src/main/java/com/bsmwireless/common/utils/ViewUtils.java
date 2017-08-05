package com.bsmwireless.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ViewUtils {
    private final static int MINUTES_IN_HOUR = 60;

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixels(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     *
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
     *
     * @param zone user timezone for example "America/Los_Angeles"
     * @param day day in month
     * @param month month (0 is for Jan)
     * @param year year
     * @return start date in ms
     */
    public static long getStartDate(String zone, int day, int month, int year) {
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(year, month, day, 0, 0, 0);

        return calendar.getTimeInMillis();
    }

    /**
     *
     * @param zone user timezone for example "America/Los_Angeles"
     * @param day day in month
     * @param month month (0 is for Jan)
     * @param year year
     * @return end date in ms
     */
    public static long getEndDate(String zone, int day, int month, int year) {
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(year, month, day, 23, 59, 59);
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
    public static  String convertTimeInMsToStringTime(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        return String.format(Locale.US, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }
}
