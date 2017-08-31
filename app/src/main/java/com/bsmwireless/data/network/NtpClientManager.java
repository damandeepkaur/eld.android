package com.bsmwireless.data.network;


import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.instacart.library.truetime.TrueTimeRx;

import java.util.Date;

import io.reactivex.Flowable;

public class NtpClientManager {

    private TrueTimeRx mTrueTimeRx;
    private Long realTimeInMillisDiff;

    public NtpClientManager() {
        mTrueTimeRx = TrueTimeRx.build();
    }

    public Flowable<Date> init() {
        return mTrueTimeRx.withConnectionTimeout((int) Constants.CONNECTION_TIMEOUT)
                .withRetryCount(Constants.RETRY_COUNT_NTP_SYNC)
                .withSharedPreferences(App.getComponent().context())
                .withLoggingEnabled(true)
                .initializeRx(Constants.NTP_POOL_SERVER);
    }

    public void setRealTimeInMillisDiff(Date date) {
        long realTimeInMillis = date.getTime();
        long systemTimeInMillis = System.currentTimeMillis();
        realTimeInMillisDiff = realTimeInMillis - systemTimeInMillis;
    }

    public static boolean isInitialized() {
        return TrueTimeRx.isInitialized();
    }

    public Long getRealTimeInMillisDiff() {
        return realTimeInMillisDiff != null ? realTimeInMillisDiff : 0;
    }
}
