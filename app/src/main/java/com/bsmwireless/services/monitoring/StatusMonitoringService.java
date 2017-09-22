package com.bsmwireless.services.monitoring;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.lockscreen.LockScreenActivity;

import javax.inject.Inject;


public class StatusMonitoringService extends Service implements MonitoringServiceView {

    private final IBinder binder = new LocalBinder();

    @Inject
    MonitoringServicePresenter presenter;

    public static Intent createIntent(Context context) {
        return new Intent(context, StatusMonitoringService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getComponent().monitoringServiceBuilder()
                .view(this)
                .build()
                .inject(this);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        presenter.startMonitoring();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        presenter.startMonitoring();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        presenter.stopMonitoring();
        //we want to get onRebuild call when service will be bound again
        return true;
    }

    @Override
    public void startLockScreen() {
        final Intent intent = LockScreenActivity.createIntent(getApplicationContext());
        startActivity(intent);
    }

    public class LocalBinder extends Binder {
        StatusMonitoringService getService() {
            return StatusMonitoringService.this;
        }
    }
}
