package com.bsmwireless.common.utils;

import android.app.Activity;
import android.view.View;
import android.view.Window;

public class UiVisibilityHelper {

    public final static int NO_STATUS_BAR =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
    public final static int IMMERSIVE_FULL_SCREEN =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    public final static int NAVIGATION_STICKY =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    public static void updateUiVisibility(Activity activity, int visibility) {
        Window window = activity.getWindow();
        if (window != null) {
            window.getDecorView().setSystemUiVisibility(visibility);
        }
    }
}
