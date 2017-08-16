package com.bsmwireless.models;

public enum BlackBoxSensorState {
    Ignition(256),
    GPS(512),
    TimeValid(1024),
    PTO(2048),
    HighRail(4096),
    VehiclePower(8192),
    ECMCable(16384),
    ECMSync(32768),
    Moving(65536),
    RPM(131072),
    TERTSource(262144),
    Odometer(524288),
    SpeedSource(1048576),
    TDQueueFull(2097152),
    TDServerHostConnection(4194304),
    ValidMDTSubscription(8388608),
    WIFIClientConnected(16777216);

    private final int mask;

    BlackBoxSensorState(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }
}
