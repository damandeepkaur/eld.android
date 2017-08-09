package com.bsmwireless.data.network.connection;


import com.bsmwireless.data.network.connection.device.TelematicDevice;
import com.bsmwireless.models.Vehicle;

/**
 *  Common Interface for connection with the box
 */

public interface TelematicDeviceConnectionInterface {

    void setDevice(TelematicDevice telematicDevice);
    void connect(Vehicle vehicle);
    void disconnect();
    boolean isConnected();
}
