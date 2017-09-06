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

import com.bsmwireless.common.App;
import com.bsmwireless.schedulers.alarmmanager.AlarmBootReceiver;
import com.bsmwireless.schedulers.jobscheduler.AutoLogoutJobService;
import com.bsmwireless.schedulers.alarmmanager.AlarmReceiver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.os.SystemClock;

public class SchedulerUtils {

    private static final int JOB_ID = 111;
    private static final int AUTO_LOGOUT_TRIGGER_DURATION = 60;
    private static final int AUTO_LOGOUT_TRIGGER_DURATION_MIN = 55;
    private static final int AUTO_LOGOUT_TRIGGER_DURATION_MAX = 65;

    private static PendingIntent mPendingIntent;

    public static void schedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleExactJobScheduler();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            scheduleExactAlarmManager();
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

        setExactAlarmManager(alarmManager);
    }

    private static void setExactAlarmManager(AlarmManager alarmManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(AUTO_LOGOUT_TRIGGER_DURATION), mPendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(AUTO_LOGOUT_TRIGGER_DURATION), mPendingIntent);
        }
    }

    public static void cancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelAllJobs();
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
    private static void cancelAllJobs() {
        JobScheduler jobScheduler = (JobScheduler) App.getComponent().context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
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
}
