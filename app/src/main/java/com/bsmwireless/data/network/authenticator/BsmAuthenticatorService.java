package com.bsmwireless.data.network.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BsmAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        BsmAuthenticator authenticator = new BsmAuthenticator(this);
        return authenticator.getIBinder();
    }
}
