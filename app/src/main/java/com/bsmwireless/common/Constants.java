package com.bsmwireless.common;

public interface Constants {
    long READ_TIMEOUT = 60;
    long CONNECTION_TIMEOUT = 60;
    int DEBOUNCE_TIMEOUT = 500;

    int MAX_LAST_VEHICLE = 5;

    String BASE_URL = "https://develd.bsmtechnologies.com/sdmobile/rest/";
    String DEVICE_TYPE = "Android";


    String WIFI_GATEWAY_IP="192.168.1.1";
    int WIFI_REMOTE_PORT=2880;
    int STATUS_UPDATE_RATE_SECS=60;
}
