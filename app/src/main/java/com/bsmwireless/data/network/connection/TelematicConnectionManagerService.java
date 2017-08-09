package com.bsmwireless.data.network.connection;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.bsmwireless.common.App;

import app.bsmuniversal.com.R;

/*
 * Foreground service to keep the box communication alive
 */
public class TelematicConnectionManagerService extends Service {
    private final int NOTIFICATION_ID=911;
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = new NotificationCompat.Builder(App.getComponent().context())
                .setContentTitle(getString(R.string.app_name))
               // .setContentText("Content Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.icon))
                .setOngoing(true).build();

        notification.flags|=Notification.FLAG_NO_CLEAR;
        startForeground(NOTIFICATION_ID, notification);
    }

    /*
     * Start Sticky service , System will recreate the service after it is killed.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
}

    public void startConnectionService()
    {
        Context appContext = App.getComponent().context();
        Intent intent = new Intent(appContext, TelematicConnectionManagerService.class);
        appContext.startService(intent);

    }
    public void stopConnectionService()
    {
        Context appContext = App.getComponent().context();
        Intent intent = new Intent(appContext, TelematicConnectionManagerService.class);
        appContext.stopService(intent);
    }

}
