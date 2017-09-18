package com.bsmwireless.screens.logs.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.logs.LogsView;

import dagger.Module;
import dagger.Provides;

@Module
public final class LogsModule {

    private final LogsView mLogsView;

    public LogsModule(@NonNull LogsView view) {
        mLogsView = view;
    }

    @ActivityScope
    @Provides
    LogsView provideView() {
        return mLogsView;
    }
}