package com.bsmwireless.schedulers.jobscheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.autologout.AutoLogoutActivity;

@TargetApi(21)
public class AutoLogoutJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent dialogIntent = new Intent(App.getComponent().context(), AutoLogoutActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getComponent().context().startActivity(dialogIntent);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
