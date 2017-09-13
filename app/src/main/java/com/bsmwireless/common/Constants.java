package com.bsmwireless.common;

import com.bsmwireless.models.Malfunction;

public interface Constants {
    long READ_TIMEOUT = 60;
    long CONNECTION_TIMEOUT = 60;
    int DEBOUNCE_TIMEOUT = 500;

    int MAX_LAST_VEHICLE = 5;
    int DEFAULT_CALENDAR_DAYS_COUNT = 30;
    int MAX_CODRIVERS = 2;
    int SYNC_TIMEOUT_IN_MIN = 1;
    int SYNC_NTP_RETRY_COUNT = 3;

    String BASE_URL = "https://develd.bsmtechnologies.com/sdmobile/rest/";
    String DEVICE_TYPE = "Android";

    String SUCCESS = "ACK";

    String NTP_POOL_SERVER = "time.google.com";

    String[] MALFUNCTION_CODES = new String[]{
            Malfunction.POWER_COMPLIANCE.getCode(),
            Malfunction.ENGINE_SYNCHRONIZATION_COMPLIANCE.getCode(),
            Malfunction.TIMING_COMPLIANCE.getCode(),
            Malfunction.POSITIONING_COMPLIANCE.getCode(),
            Malfunction.DATA_RECORDING_COMPLIANCE.getCode(),
            Malfunction.DATA_TRANSFER_COMPLIANCE.getCode(),
            Malfunction.OTHER_COMPLIANCE.getCode()
    };

    String[] DIAGNOSTIC_CODES = new String[]{
            Malfunction.POWER_DATA_DIAGNOSTIC.getCode(),
            Malfunction.ENGINE_SYNCHRONIZATION.getCode(),
            Malfunction.MISSING_REQUIRED_DATA_ELEMENTS.getCode(),
            Malfunction.DATA_TRANSFER.getCode(),
            Malfunction.UNIDENTIFIED_DRIVING.getCode(),
            Malfunction.OTHER_COMPLIANCE.getCode()
    };
}
