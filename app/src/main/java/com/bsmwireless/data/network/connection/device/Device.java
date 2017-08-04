package com.bsmwireless.data.network.connection.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 *  Interface to connect to the Device, it can be WIFI or Serial
 */


public interface Device {
    boolean connect();
    void disconnect();
    boolean isConnected();
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
}
