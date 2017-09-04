package app.bsmuniversal.com.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

public class SystemUtil {

    public static Context getAppContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    public static String resourceToString(int resId) {
        return getAppContext().getString(resId);
    }

}
