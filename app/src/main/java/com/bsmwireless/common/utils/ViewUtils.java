package com.bsmwireless.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;


public class ViewUtils {
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
