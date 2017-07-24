package com.bsmwireless.data.network.Connection;


import com.bsmwireless.common.Constants;
import com.bsmwireless.models.Vehicle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import timber.log.Timber;

/**
 * Created by hsudhagar on 2017-07-23.
 */

public class Device implements DeviceInterface {

    private String mConnectionString =Constants.WIFI_GATEWAY_IP;
    private int mPort = Constants.WIFI_REMOTE_PORT;
    private Socket mSocket;

    @Override
    public boolean connect() {
        boolean connected =false;
        try {
            //disconnect first if a connection is still open.
            if (mSocket!=null)  disconnect();
            //Start a new socket connection
            InetAddress serverAddr = InetAddress.getByName(mConnectionString);
            mSocket = new Socket(serverAddr, mPort);
            if (mSocket!=null)
                return true;
        }
        catch (Exception ex)
        {
            Timber.e(ex);
        }
        return false;
    }
    @Override
    public boolean isConnected() {
        if (mSocket!=null && mSocket.isConnected())
            return true;

        return false;
    }
    @Override
    public void disconnect() {
        try {

            InputStream iStream = getInputStream();
            if (iStream != null) {
                iStream.close();
            }
            OutputStream oStream = getOutputStream();
            if (oStream != null) {
                oStream.close();
            }

            if (mSocket!=null)
            {
                mSocket.close();
            }

        }
        catch(Exception e)
        {
            Timber.e("Exception in disconnect:" ,e);
        }
    }



    @Override
    public InputStream getInputStream() throws IOException {
        return mSocket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return mSocket.getOutputStream();
    }
}
