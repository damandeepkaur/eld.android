package com.bsmwireless.screens.dashboard.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.dashboard.DashboardView;

import dagger.Module;
import dagger.Provides;

@Module
public final class DashboardModule {

    private final DashboardView mDashboardView;

    public DashboardModule(@NonNull DashboardView view) {
        mDashboardView = view;
    }

    @ActivityScope
    @Provides
    public DashboardView provideView() {
        return mDashboardView;
    }
}