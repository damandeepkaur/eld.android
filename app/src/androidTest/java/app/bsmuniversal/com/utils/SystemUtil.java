package app.bsmuniversal.com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

public class SystemUtil {

    public static Context getAppContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    public static String resourceToString(int resId) {
        return getAppContext().getString(resId);
    }

    public static void clearSharedPrefForKey(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        sharedPreferences.edit().remove(key).apply();
    }

}
