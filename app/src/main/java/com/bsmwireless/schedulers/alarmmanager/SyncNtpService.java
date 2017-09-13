package com.bsmwireless.schedulers.alarmmanager;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.bsmwireless.common.App;
import com.bsmwireless.data.network.NtpClientManager;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SyncNtpService extends IntentService {

    public static final String TAG = SyncNtpService.class.getSimpleName();

    public SyncNtpService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NtpClientManager NtpClientManager = App.getComponent().ntpClientManager();
        NtpClientManager.init(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        date -> {
                            Timber.i("Ntp sync date = %s", date.toString());
                            NtpClientManager.setRealTimeInMillisDiff(date);
                        },
                        throwable -> Timber.e("Something went wrong when trying to initializeRx TrueTime: %s", throwable));

        SyncNtpAlarmReceiver.completeWakefulIntent(intent);
    }
}
