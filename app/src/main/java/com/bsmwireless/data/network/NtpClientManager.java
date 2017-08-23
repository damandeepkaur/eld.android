package com.bsmwireless.data.network;

import android.support.annotation.NonNull;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.instacart.library.truetime.TrueTimeRx;

import java.util.Date;

import io.reactivex.Flowable;

public class NtpClientManager {

    private TrueTimeRx mTrueTimeRx;

    public NtpClientManager() {
        mTrueTimeRx = TrueTimeRx.build();
    }

    public Flowable<Date> init() {
        return mTrueTimeRx.withSharedPreferences(App.getComponent().context())
                .withLoggingEnabled(true)
                .initializeRx(Constants.NTP_POOL_SERVER);
    }

    @NonNull
    public Date getNtpDateTime() {
        return TrueTimeRx.now();
    }

    public static boolean isInitialized() {
        return TrueTimeRx.isInitialized();
    }
}
