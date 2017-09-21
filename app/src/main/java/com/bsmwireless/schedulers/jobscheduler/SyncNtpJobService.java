package com.bsmwireless.schedulers.jobscheduler;


import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import com.bsmwireless.common.App;
import com.bsmwireless.data.network.NtpClientManager;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class SyncNtpJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        NtpClientManager NtpClientManager = App.getComponent().ntpClientManager();
        NtpClientManager.init(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        date -> {
                            Timber.i("Ntp sync date = %s", date.toString());
                            NtpClientManager.setRealTimeInMillisDiff(date);
                            jobFinished(params, false);
                        },
                        throwable -> {
                            Timber.e("Something went wrong when trying to initializeRx TrueTime: %s", throwable);
                            NtpClientManager.resetRealTimeInMillisDiff();
                            jobFinished(params, false);
                        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
