package com.bsmwireless.data.network.Connection;

import com.bsmwireless.models.Vehicle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by hsudhagar on 2017-07-23.
 */

public interface DeviceInterface {

    public boolean connect();
    public void disconnect();

    public boolean isConnected();

    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;
}
