package com.bsmwireless.schedulers.alarmmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import timber.log.Timber;

import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_NAME;
import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_TYPE;

public final class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.v("onReceive: ");
        if (intent.hasExtra(ACCOUNT_NAME) && intent.hasExtra(ACCOUNT_TYPE)) {
            Intent expirationServiceIntent = new Intent(context, TokenExpirationService.class);
            expirationServiceIntent.putExtras(intent.getExtras());
            startWakefulService(context, expirationServiceIntent);
        } else {
            startWakefulService(context, new Intent(context, AutoLogoutService.class));
        }
    }
}
