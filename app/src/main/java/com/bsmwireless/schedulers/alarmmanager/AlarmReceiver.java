package com.bsmwireless.schedulers.alarmmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.bsmwireless.common.App;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(App.getComponent().context(),
                new Intent(App.getComponent().context(), AutoLogoutService.class));
    }
}
