package com.bsmwireless.data.network;


import android.content.Context;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.ELDEvent;
import com.instacart.library.truetime.TrueTimeRx;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Flowable;

public final class NtpClientManager {

    private TrueTimeRx mTrueTimeRx;
    private AtomicLong mRealTimeInMillisDiff;

    public NtpClientManager() {
        mRealTimeInMillisDiff = new AtomicLong(0);
        mTrueTimeRx = TrueTimeRx.build();
    }

    public Flowable<Date> init(Context context) {
        return mTrueTimeRx.withConnectionTimeout((int) Constants.CONNECTION_TIMEOUT)
                .withRetryCount(Constants.SYNC_NTP_RETRY_COUNT)
                .withSharedPreferences(context)
                .initializeRx(Constants.NTP_POOL_SERVER);
    }

    public void setRealTimeInMillisDiff(Date date) {
        long realTimeInMillis = date.getTime();
        long systemTimeInMillis = System.currentTimeMillis();
        mRealTimeInMillisDiff.set(realTimeInMillis - systemTimeInMillis);
    }

    public static boolean isInitialized() {
        return TrueTimeRx.isInitialized();
    }

    public long getRealTimeInMillisDiff() {
        return mRealTimeInMillisDiff.get();
    }
}
