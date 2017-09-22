package com.bsmwireless.screens.roadsidenavigation.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import dagger.Module;
import dagger.Provides;

@Module
public final class RoadsideNavigationModule {

    private final BaseMenuView mView;

    public RoadsideNavigationModule(@NonNull BaseMenuView view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    BaseMenuView provideView() {
        return mView;
    }
}