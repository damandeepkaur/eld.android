package com.bsmwireless.schedulers.alarmmanager;

import android.app.IntentService;
import android.content.Intent;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.autologout.AutoLogoutActivity;

public class AutoLogoutService extends IntentService {

    public static final String TAG = AutoLogoutService.class.getSimpleName();

    public AutoLogoutService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent dialogIntent = new Intent(App.getComponent().context(), AutoLogoutActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getComponent().context().startActivity(dialogIntent);
    }
}
