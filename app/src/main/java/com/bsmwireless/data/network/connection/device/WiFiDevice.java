package com.bsmwireless.data.network.connection.device;


import com.bsmwireless.common.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import timber.log.Timber;

/**
 *  Communication through WiFi in the Device.
 */

public class WiFiDevice implements Device {

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
            OutputStream oStream = getOutputStream();
            if (iStream != null) {
                try {
                    iStream.close();
                }catch(IOException e){ Timber.e("Exception in InputStream:" ,e);}
            }

            if (oStream != null) {
                try {
                    oStream.close();
                }catch(IOException e){Timber.e("Exception in OutputStream:" ,e);}
            }

            if (mSocket!=null)
            {
                try{
                mSocket.close();
                }catch(IOException e){ Timber.e("Exception in Socket close:" ,e);}
            }
            mSocket =null;
        }
        catch(Exception e)
        {
            Timber.e("Exception in disconnect:" ,e);
        }
    }



    @Override
    public InputStream getInputStream() throws IOException {
        return mSocket!=null?mSocket.getInputStream():null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return mSocket!=null?mSocket.getOutputStream():null;
    }
}
