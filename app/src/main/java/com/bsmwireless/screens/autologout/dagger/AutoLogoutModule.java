package com.bsmwireless.screens.autologout.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.autologout.AutoLogoutView;

import dagger.Module;
import dagger.Provides;

@Module
public class AutoLogoutModule {

    private final AutoLogoutView mAutoLogoutView;

    public AutoLogoutModule(@NonNull AutoLogoutView view) {
        mAutoLogoutView = view;
    }

    @ActivityScope
    @Provides
    AutoLogoutView provideView() {
        return mAutoLogoutView;
    }
}
