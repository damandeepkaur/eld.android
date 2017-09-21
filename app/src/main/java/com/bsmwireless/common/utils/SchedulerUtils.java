package com.bsmwireless.common.utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;

import com.bsmwireless.common.App;
import com.bsmwireless.schedulers.alarmmanager.AlarmBootReceiver;
import com.bsmwireless.schedulers.alarmmanager.AlarmReceiver;
import com.bsmwireless.schedulers.alarmmanager.SyncNtpAlarmReceiver;
import com.bsmwireless.schedulers.jobscheduler.AutoLogoutJobService;
import com.bsmwireless.schedulers.jobscheduler.SyncNtpJobService;
import com.bsmwireless.schedulers.jobscheduler.VerifyTokenScheduler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static android.app.AlarmManager.RTC_WAKEUP;
import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_NAME;
import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_TYPE;

public final class SchedulerUtils {

    private static final int JOB_ID = 111;
    private static final int SYNC_NTP_JOB_ID = 222;
    private static final int AUTO_LOGOUT_TRIGGER_DURATION = 60;
    private static final int AUTO_LOGOUT_TRIGGER_DURATION_MIN = 55;
    private static final int AUTO_LOGOUT_TRIGGER_DURATION_MAX = 65;
    private static final int SYNC_NTP_TRIGGER_PERIOD_MIN = 5;

    private static final int[] CANCELABLE_JOBS = {JOB_ID, SYNC_NTP_JOB_ID};

    private static PendingIntent mPendingIntent;
    private static Map<String, PendingIntent> mTokenExpirationMap = new HashMap<>();

    public static void schedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleExactJobScheduler();
            scheduleInExactJobScheduler();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            scheduleExactAlarmManager();
            scheduleInExactAlarmManager();
        }
    }

    public static void scheduleExactAlarmManager() {
        // If the alarm is set up, don't set it again.
        if (isAlarmSetUp()) {
            return;
        }

        Intent intent = new Intent(App.getComponent().context(), AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(App.getComponent().context(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) App.getComponent().context().getSystemService(Context.ALARM_SERVICE);

        setExactAlarmManager(alarmManager,
                SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(AUTO_LOGOUT_TRIGGER_DURATION),
                mPendingIntent, AlarmManager.ELAPSED_REALTIME_WAKEUP);
    }

    private static void setExactAlarmManager(AlarmManager alarmManager, long time,
                                             PendingIntent intent, int alarmType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(alarmType, time, intent);
        } else {
            alarmManager.set(alarmType, time, intent);
        }
    }

    public static void scheduleInExactAlarmManager() {
        Intent intent = new Intent(App.getComponent().context(), SyncNtpAlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(App.getComponent().context(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) App.getComponent().context().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(SYNC_NTP_TRIGGER_PERIOD_MIN),
                TimeUnit.MINUTES.toMillis(SYNC_NTP_TRIGGER_PERIOD_MIN),
                mPendingIntent);
    }

    public static void cancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelAllCancelableJobs();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            cancelAlarm();
        }
    }

    private static void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) App.getComponent().context().getSystemService(Context.ALARM_SERVICE);
        // If the alarm has been set, cancel it.
        if (alarmManager != null) {
            alarmManager.cancel(mPendingIntent);
            mPendingIntent.cancel();
        }

        // Disable {@code AlarmBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(App.getComponent().context(), AlarmBootReceiver.class);
        PackageManager pm = App.getComponent().context().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @TargetApi(21)
    private static void scheduleExactJobScheduler() {
        // Don't schedule new job if there is pending one.
        List<JobInfo> allPendingJobs = getAllPendingJobs();
        if (allPendingJobs.size() > 0) {
            return;
        }

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(App.getComponent().context(), AutoLogoutJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(TimeUnit.MINUTES.toMillis(AUTO_LOGOUT_TRIGGER_DURATION_MIN))
                .setOverrideDeadline(TimeUnit.MINUTES.toMillis(AUTO_LOGOUT_TRIGGER_DURATION_MAX))
                .setPersisted(true)
                .setRequiresDeviceIdle(true);

        JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @TargetApi(21)
    private static void scheduleInExactJobScheduler() {
        JobInfo.Builder builder = new JobInfo.Builder(SYNC_NTP_JOB_ID, new ComponentName(App.getComponent().context(), SyncNtpJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(TimeUnit.MINUTES.toMillis(SYNC_NTP_TRIGGER_PERIOD_MIN))
                .setPersisted(false);

        JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @TargetApi(21)
    private static void cancelAllCancelableJobs() {
        JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (int id : CANCELABLE_JOBS) {
            jobScheduler.cancel(id);
        }
    }

    @TargetApi(21)
    public static void cancelJob() {
        JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> allPendingJobs = getAllPendingJobs();
        if (allPendingJobs.size() > 0) {
            // Finish the last one
            int jobId = allPendingJobs.get(0).getId();
            jobScheduler.cancel(jobId);
        }
    }

    @TargetApi(21)
    private static List<JobInfo> getAllPendingJobs() {
        JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        return jobScheduler.getAllPendingJobs();
    }

    private static boolean isAlarmSetUp() {
        return (PendingIntent.getBroadcast(App.getComponent().context(), 0,
                new Intent(App.getComponent().context(), AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static void scheduleTokenExpiration(String accountType, String name, long timestamp) {
        Timber.d("scheduleTokenExpiration: " + accountType + " " + name);
        long diff = timestamp - Calendar.getInstance().getTimeInMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString(ACCOUNT_NAME, name);
            bundle.putString(ACCOUNT_TYPE, accountType);
            JobInfo.Builder builder = new JobInfo.Builder(name.hashCode(), new ComponentName(App.getComponent().context(), VerifyTokenScheduler.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(diff)
                    .setExtras(bundle)
                    .setPersisted(true);

            JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        } else {
            if (mTokenExpirationMap.containsKey(name)) {
                cancelTokenExpiration(name);
            }
            Intent intent = new Intent(App.getComponent().context(), AlarmReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putString(ACCOUNT_NAME, name);
            bundle.putString(ACCOUNT_TYPE, accountType);
            intent.putExtras(bundle);
            mTokenExpirationMap.put(name, PendingIntent.getBroadcast(App.getComponent().context(), name.hashCode(), intent, 0));
            AlarmManager alarmManager = (AlarmManager) App.getComponent().context().getSystemService(Context.ALARM_SERVICE);
            setExactAlarmManager(alarmManager, timestamp, mTokenExpirationMap.get(name), RTC_WAKEUP);
        }
    }

    public static void cancelTokenExpiration(String name) {
        Timber.v("cancelTokenExpiration: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(name.hashCode());
        } else {
            AlarmManager alarmManager = (AlarmManager) App.getComponent().context().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && mTokenExpirationMap.containsKey(name)) {
                alarmManager.cancel(mTokenExpirationMap.get(name));
                mTokenExpirationMap.remove(name).cancel();
            }
        }
    }
}
