package com.bsmwireless.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;


public abstract class BaseMonitoringService extends Service {

    private final IBinder mBinder = new Binder();

    @Inject
    MonitoringPresenter mMonitoringPresenter;

    @Override
    public final void onRebind(Intent intent) {
        super.onRebind(intent);
        mMonitoringPresenter.startMonitoring();
    }

    @Nullable
    @Override
    public final IBinder onBind(Intent intent) {
        mMonitoringPresenter.startMonitoring();
        return mBinder;
    }

    @Override
    public final boolean onUnbind(Intent intent) {
        mMonitoringPresenter.stopMonitoring();
        //we want to get onRebuild call when service will be bound again
        return true;
    }

}
