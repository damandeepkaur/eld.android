package com.bsmwireless.common.dagger;


import com.bsmwireless.data.network.connection.ConnectionManager;
import com.bsmwireless.data.network.connection.device.WiFiDevice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hsudhagar on 2017-07-17.
 */
@Module
public class BoxModule {

 @Singleton
    @Provides
 WiFiDevice provideDevice() {
        return new WiFiDevice();
    }

    @Singleton
    @Provides
    ConnectionManager provideConnectionManager(WiFiDevice wiFiDevice) {
        return new ConnectionManager(wiFiDevice);
    }

}
