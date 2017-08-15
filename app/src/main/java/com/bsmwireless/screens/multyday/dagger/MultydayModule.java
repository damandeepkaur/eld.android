package com.bsmwireless.screens.multyday.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.multyday.MultydayView;

import dagger.Module;
import dagger.Provides;

@Module
public class MultydayModule {
    private final MultydayView mHomeView;

    public MultydayModule(@NonNull MultydayView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    MultydayView provideView() {
        return mHomeView;
    }
}
