package com.bsmwireless.schedulers.alarmmanager;

import android.app.IntentService;
import android.content.Intent;

import com.bsmwireless.screens.autologout.AutoDutyDialogActivity;

import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_LOGOUT;

public class AutoLogoutService extends IntentService {

    public static final String TAG = AutoLogoutService.class.getSimpleName();

    public AutoLogoutService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent dialogIntent = new Intent(this, AutoDutyDialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_AUTO_LOGOUT, true);

        startActivity(dialogIntent);
    }
}
