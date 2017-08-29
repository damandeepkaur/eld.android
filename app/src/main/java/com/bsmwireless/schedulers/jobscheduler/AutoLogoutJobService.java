package com.bsmwireless.schedulers.jobscheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.bsmwireless.screens.autologout.AutoDutyDialogActivity;

import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_AUTO_LOGOUT;

@TargetApi(21)
public class AutoLogoutJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent dialogIntent = new Intent(this, AutoDutyDialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(EXTRA_AUTO_LOGOUT, true);

        startActivity(dialogIntent);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
