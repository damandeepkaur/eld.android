package com.bsmwireless.models;

public enum BlackBoxSensorState {
    IGNITION(256),      // 00000000 00000000 00000001 00000000 - bit mask for ignition
    GPS(512),           // 00000000 00000000 00000010 00000000 - bit mask for GPS
    TIME_VALID(1024),   // 00000000 00000000 00000100 00000000 - bit mask for Time valid field
    PTO(2048),          // ...etc
    HIGH_RAIL(4096),
    VEHICLE_POWER(8192),
    ECM_CABLE(16384),
    ECM_SYNC(32768),
    MOVING(65536),
    RPM(131072),
    TERT_SOURCE(262144),
    ODOMETER(524288),
    SPEED_SOURCE(1048576),
    TD_QUEUE_FULL(2097152),
    TD_SERVER_HOST_CONNECTION(4194304),
    VALID_MDT_SUBSCRIPTION(8388608),
    WIFI_CLIENT_CONNECTED(16777216);

    private final int mask;

    BlackBoxSensorState(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }
}
