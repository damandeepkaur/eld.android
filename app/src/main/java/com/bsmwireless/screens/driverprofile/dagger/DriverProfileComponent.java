package com.bsmwireless.screens.driverprofile.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.driverprofile.DriverProfileActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {DriverProfileModule.class})
public interface DriverProfileComponent {
    void inject(DriverProfileActivity driverProfileActivity);
}
