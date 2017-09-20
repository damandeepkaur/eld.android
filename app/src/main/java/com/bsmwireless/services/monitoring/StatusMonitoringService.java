package com.bsmwireless.services.monitoring;

import android.content.Context;
import android.content.Intent;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.lockscreen.LockScreenActivity;
import com.bsmwireless.services.BaseMonitoringService;

import javax.inject.Inject;


public final class StatusMonitoringService extends BaseMonitoringService implements MonitoringServiceView {

    @Inject
    MonitoringServicePresenter mPresenter;

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
    public void startLockScreen() {
        final Intent intent = LockScreenActivity.createIntent(getApplicationContext());
        startActivity(intent);
    }
}
