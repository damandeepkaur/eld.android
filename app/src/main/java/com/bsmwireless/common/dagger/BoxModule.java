package com.bsmwireless.common.dagger;


import com.bsmwireless.data.network.connection.TelematicDeviceConnectionManager;
import com.bsmwireless.data.network.connection.device.WiFiTelematicDevice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class BoxModule {

    @Singleton
    @Provides
    WiFiTelematicDevice provideDevice() {
        return new WiFiTelematicDevice();
    }

    @Singleton
    @Provides
    TelematicDeviceConnectionManager provideConnectionManager(WiFiTelematicDevice wiFiDevice) {
        return new TelematicDeviceConnectionManager(wiFiDevice);
    }

}
