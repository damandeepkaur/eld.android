package com.bsmwireless.schedulers.jobscheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

/**
 * Created by osminin on 15.09.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VerifyTokenScheduler extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
