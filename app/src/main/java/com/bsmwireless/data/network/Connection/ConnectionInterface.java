package com.bsmwireless.data.network.connection;


import com.bsmwireless.data.network.connection.device.Device;
import com.bsmwireless.models.Vehicle;

/**
 *  Common Interface for connection with the box
 */

public interface ConnectionInterface {

    void setDevice(Device device);
    void connect(Vehicle vehicle);
    void disconnect();
    boolean isConnected();
}
