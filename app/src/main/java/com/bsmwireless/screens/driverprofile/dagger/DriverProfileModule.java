package com.bsmwireless.screens.driverprofile.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.driverprofile.DriverProfileView;

import dagger.Module;
import dagger.Provides;

@Module
public final class DriverProfileModule {

    private final DriverProfileView mHomeView;

    public DriverProfileModule(@NonNull DriverProfileView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    DriverProfileView provideView() {
        return mHomeView;
    }
}
