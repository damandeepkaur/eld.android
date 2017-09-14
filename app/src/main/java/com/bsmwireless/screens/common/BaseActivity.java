package com.bsmwireless.screens.common;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.bsmwireless.services.monitoring.StatusMonitoringService;

import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity{
    protected Unbinder mUnbinder;
    private boolean mDoBind = true;
    boolean mIsBound = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (mDoBind) {
            final Intent intent = StatusMonitoringService.createIntent(this);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(serviceConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    /**
     * Set flag for binding to service.
     * <p/>
     * Call this method in {@link android.app.Activity#onCreate(Bundle)}
     * @param doBind true if need to bind service, otherwise false
     */
    protected void doBindToService(boolean doBind) {
        this.mDoBind = doBind;
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsBound = false;
        }
    };
}
