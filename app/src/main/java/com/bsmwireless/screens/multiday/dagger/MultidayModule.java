package com.bsmwireless.screens.multiday.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.multiday.MultidayView;

import dagger.Module;
import dagger.Provides;

@Module
public final class MultidayModule {
    private final MultidayView mHomeView;

    public MultidayModule(@NonNull MultidayView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    MultidayView provideView() {
        return mHomeView;
    }
}
