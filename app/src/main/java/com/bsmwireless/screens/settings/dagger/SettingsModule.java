package com.bsmwireless.screens.settings.dagger;


import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.settings.SettingsView;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsModule {

    private final SettingsView mSettingsView;

    public SettingsModule(@NonNull SettingsView mView) {
        mSettingsView = mView;
    }

    @ActivityScope
    @Provides
    SettingsView provideView() {
        return mSettingsView;
    }
}
