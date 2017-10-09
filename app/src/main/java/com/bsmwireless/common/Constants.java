package com.bsmwireless.common;

import com.bsmwireless.models.Malfunction;
import com.bsmwireless.screens.lockscreen.LockScreenActivity;
import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public interface Constants {
    long READ_TIMEOUT = 60;
    long CONNECTION_TIMEOUT = 60;
    int DEBOUNCE_TIMEOUT = 500;
    int SYNC_DELAY = 5;

    int MAX_LAST_VEHICLE = 5;
    int DEFAULT_CALENDAR_DAYS_COUNT = 30;
    int MAX_CODRIVERS = 2;
    int SYNC_TIMEOUT_IN_MIN = 1;
    int SYNC_ALL_EVENTS_IN_MIN = 60;
    int SYNC_NTP_RETRY_COUNT = 3;

    long LOCK_SCREEN_IDLE_MONITORING_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5);
    long LOCK_SCREEN_DISCONNECTION_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5);

    long DIFF_FOR_TRIGGER_TIMING_MALFUNCTION_MS = TimeUnit.MINUTES.toMillis(10);
    long CHECK_TIME_INTERVAL = TimeUnit.MINUTES.toMillis(1);

    double DEFAULT_STORAGE_CAPACITY_THRESHOLD = 0.10;

    String BASE_URL = "https://develd.bsmtechnologies.com/sdmobile/rest/";
    String DEVICE_TYPE = "Android";

    String SUCCESS = "ACK";

    String NTP_POOL_SERVER = "time.google.com";

    Pattern COMMENT_VALIDATE_PATTERN = Pattern.compile("[^A-Za-z0-9`!@#$%^&* ()_\\-+=\\[\\]\\\\/?><.,;:'|\"{}~]", Pattern.CASE_INSENSITIVE);

    @SuppressWarnings("PublicStaticArrayField")
    String[] MALFUNCTION_CODES = new String[]{
            Malfunction.POWER_COMPLIANCE.getCode(),
            Malfunction.ENGINE_SYNCHRONIZATION_COMPLIANCE.getCode(),
            Malfunction.TIMING_COMPLIANCE.getCode(),
            Malfunction.POSITIONING_COMPLIANCE.getCode(),
            Malfunction.DATA_RECORDING_COMPLIANCE.getCode(),
            Malfunction.DATA_TRANSFER_COMPLIANCE.getCode(),
            Malfunction.OTHER_COMPLIANCE.getCode()
    };

    @SuppressWarnings("PublicStaticArrayField")
    String[] DIAGNOSTIC_CODES = new String[]{
            Malfunction.POWER_DATA_DIAGNOSTIC.getCode(),
            Malfunction.ENGINE_SYNCHRONIZATION.getCode(),
            Malfunction.MISSING_REQUIRED_DATA_ELEMENTS.getCode(),
            Malfunction.DATA_TRANSFER.getCode(),
            Malfunction.UNIDENTIFIED_DRIVING.getCode(),
            Malfunction.OTHER_COMPLIANCE.getCode()
    };

    @SuppressWarnings("PublicStaticCollectionField")
    Set<Class> NOT_RUNNING_DRIVING_MONITORING_ACTIVITY = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(LoginActivity.class,
                    LockScreenActivity.class,
                    SelectAssetActivity.class)));

    @SuppressWarnings("PublicStaticCollectionField")
    Set<Class> NOT_RUNNING_MALFUNCTIONS_MONITORING_ACTIVITY = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(LoginActivity.class,
                    SelectAssetActivity.class)));
}
