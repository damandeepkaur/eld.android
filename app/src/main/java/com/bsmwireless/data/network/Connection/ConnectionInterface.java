package com.bsmwireless.data.network.Connection;


import com.bsmwireless.models.Vehicle;

/**
 * Created by hsudhagar on 2017-07-17.
 */

public interface ConnectionInterface {

    public void connect(Vehicle vehicle);
    public void disconnect();
    public boolean isConnected();
}
