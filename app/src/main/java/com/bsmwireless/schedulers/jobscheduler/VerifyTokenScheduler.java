package com.bsmwireless.schedulers.jobscheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.PersistableBundle;

import com.bsmwireless.common.App;
import com.bsmwireless.data.network.authenticator.TokenManager;

import javax.inject.Inject;

import timber.log.Timber;

import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_NAME;
import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_TYPE;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class VerifyTokenScheduler extends JobService {
    @Inject
    TokenManager mTokenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("onCreate: ");
        App.getComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Timber.d("onStartJob: ");
        final PersistableBundle pb = jobParameters.getExtras();
        final String name = pb.getString(ACCOUNT_NAME);
        final String accountType = pb.getString(ACCOUNT_TYPE);
        return mTokenManager.getTokenExpirationHandler().onTokenExpired(this, accountType, name);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
