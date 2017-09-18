package com.bsmwireless.schedulers.jobscheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import com.bsmwireless.common.App;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.models.BlackBoxModel;

import javax.inject.Inject;

/**
 * Created by osminin on 15.09.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VerifyTokenScheduler extends JobService {

    @Inject
    BlackBoxInteractor mBlackBoxInteractor;

    @Override
    public void onCreate() {
        super.onCreate();
        App.getComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        BlackBoxModel lastModel = mBlackBoxInteractor.getLastData();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
