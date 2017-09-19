package com.bsmwireless.screens.common;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.bsmwireless.common.Constants;
import com.bsmwireless.screens.lockscreen.RetainFragment;
import com.bsmwireless.services.malfunction.MalfunctionMonitoringService;
import com.bsmwireless.services.monitoring.StatusMonitoringService;

import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity{
    private static final String RETAIN_FRAGMENT = "RETAIN_FRAGMENT";
    protected Unbinder mUnbinder;
    boolean mIsLockScreenDetectionServiceBound = false;
    boolean mIsMalfunctionServiceBound = false;

    @SuppressWarnings("DesignForExtension")
    @Override
    protected void onStart() {
        super.onStart();
        if (needStartDrivingDetection()) {
            final Intent intent = StatusMonitoringService.createIntent(this);
            bindService(intent, lockScreenServiceConnection, BIND_AUTO_CREATE);
        }

        if (needStartMalfunctionDetection()) {
            Intent intent = MalfunctionMonitoringService.createIntent(this);
            bindService(intent, malfunctionServiceConnection, BIND_AUTO_CREATE);
        }
    }

    @SuppressWarnings("DesignForExtension")
    @Override
    protected void onStop() {
        super.onStop();
        if (mIsLockScreenDetectionServiceBound) {
            unbindService(lockScreenServiceConnection);
            mIsLockScreenDetectionServiceBound = false;
        }
        if (mIsMalfunctionServiceBound) {
            unbindService(malfunctionServiceConnection);
            mIsMalfunctionServiceBound = false;
        }
    }

    @SuppressWarnings("DesignForExtension")
    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }

    protected final RetainFragment getRetainFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(RETAIN_FRAGMENT);
        if (fragment == null) {
            fragment = RetainFragment.createFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragment, RETAIN_FRAGMENT)
                    .commit();
        }
        return (RetainFragment) fragment;
    }

    private boolean needStartDrivingDetection(){
        return !Constants.NOT_RUNNING_DRIVING_MONITORING_ACTIVITY.contains(getClass());
    }

    private boolean needStartMalfunctionDetection(){
        return !Constants.NOT_RUNNING_MALFUNCTIONS_MONITORING_ACTIVITY.contains(getClass());
    }

    ServiceConnection lockScreenServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIsLockScreenDetectionServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsLockScreenDetectionServiceBound = false;
        }
    };

    ServiceConnection malfunctionServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIsMalfunctionServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsMalfunctionServiceBound = false;
        }
    };
}
