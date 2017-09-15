package com.bsmwireless.common;

import java.util.regex.Pattern;

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

    Pattern COMMENT_VALIDATE_PATTERN = Pattern.compile("[^A-Za-z0-9`!@#$%^&* ()_\\-+=\\[\\]\\\\/?><.,;:'|\"{}~]", Pattern.CASE_INSENSITIVE);
}
