package com.bsmwireless.screens.switchdriver.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.switchdriver.SwitchDriverView;

import dagger.Module;
import dagger.Provides;

@Module
public class SwitchDriverModule {
    private final SwitchDriverView mHomeView;

    public SwitchDriverModule(@NonNull SwitchDriverView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    SwitchDriverView provideView() {
        return mHomeView;
    }
}
