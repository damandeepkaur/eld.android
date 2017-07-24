package com.bsmwireless.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.bsmwireless.common.App;


public class NetworkUtils {

    public static boolean isOnlineMode() {
        ConnectivityManager cm = (ConnectivityManager) App.getComponent().context().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

}