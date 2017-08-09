package com.bsmwireless.data.network.connection.device;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import timber.log.Timber;

/**
 *  Communication through WiFi in the TelematicDevice.
 */

public class WiFiTelematicDevice implements TelematicDevice {

    private String mConnectionString =Constants.WIFI_GATEWAY_IP;
    private int mPort = Constants.WIFI_REMOTE_PORT;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = null;
    private Socket mSocket;



    @Override
    public boolean connect() {
        Context appContext = App.getComponent().context();
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){

                    SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                    if (supplicantState ==  SupplicantState.DISCONNECTED) {
                        disconnect();
                    }
                }

            }
        };
        appContext.registerReceiver(mReceiver,intentFilter);

        try {
            //disconnect first if a connection is still open.
            if (mSocket!= null)  disconnect();
            //Start a new socket connection
            InetAddress serverAddr = InetAddress.getByName(mConnectionString);
            mSocket = new Socket(serverAddr, mPort);
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
        return  (mSocket!=null && mSocket.isConnected());
    }
    @Override
    public void disconnect() {
        App.getComponent().context().unregisterReceiver(mReceiver);

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
