package com.bsmwireless.schedulers.alarmmanager;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class SyncNtpAlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, new Intent(context, SyncNtpService.class));
    }
}
