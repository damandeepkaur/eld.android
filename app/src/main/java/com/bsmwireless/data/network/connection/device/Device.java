package com.bsmwireless.data.network.connection.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 *  Interface to connect to the Device, it can be WIFI or Serial
 */


public interface Device {

    public boolean connect();
    public void disconnect();

    public boolean isConnected();

    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;
}
