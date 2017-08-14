package com.bsmwireless.common;

public interface Constants {
    long READ_TIMEOUT = 60;
    long CONNECTION_TIMEOUT = 60;
    int DEBOUNCE_TIMEOUT = 500;

    int MAX_LAST_VEHICLE = 5;
    int DEFAULT_CALENDAR_DAYS_COUNT = 30;

    String BASE_URL = "https://develd.bsmtechnologies.com/sdmobile/rest/";
    String DEVICE_TYPE = "Android";

    int MS_IN_SEC = 1000;
    int SEC_IN_MIN = 60;
    int MIN_IN_HOUR = 60;
    int HOUR_IN_DAY = 24;
    int SEC_IN_DAY = HOUR_IN_DAY * MIN_IN_HOUR * SEC_IN_MIN;
}
