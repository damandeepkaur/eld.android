package com.bsmwireless.schedulers.alarmmanager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bsmwireless.common.utils.SchedulerUtils;

public class AlarmBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SchedulerUtils.scheduleExactAlarmManager();
            SchedulerUtils.scheduleInExactAlarmManager();
        }
    }
}
