package com.bsmwireless.common;

public interface Constants {
    long READ_TIMEOUT = 60;
    long CONNECTION_TIMEOUT = 60;
    int DEBOUNCE_TIMEOUT = 500;

    int MAX_LAST_VEHICLE = 5;
    int DEFAULT_CALENDAR_DAYS_COUNT = 30;
    int MAX_CODRIVERS = 3;
    int SYNC_TIMEOUT_IN_MIN = 1;

    String BASE_URL = "https://develd.bsmtechnologies.com/sdmobile/rest/";
    String DEVICE_TYPE = "Android";

    String SUCCESS = "ACK";
}
