package com.bsmwireless.common.dagger;


import com.bsmwireless.data.network.connection.ConnectionManager;
import com.bsmwireless.data.network.connection.device.WiFiTelematicDevice;

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
    WiFiTelematicDevice provideDevice() {
        return new WiFiTelematicDevice();
    }

    @Singleton
    @Provides
    ConnectionManager provideConnectionManager(WiFiTelematicDevice wiFiDevice) {
        return new ConnectionManager(wiFiDevice);
    }

}
