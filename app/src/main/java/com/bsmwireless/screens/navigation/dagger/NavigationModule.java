package com.bsmwireless.screens.navigation.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.navigation.NavigateView;

import dagger.Module;
import dagger.Provides;

@Module
public class NavigationModule {

    private final NavigateView mView;

    public NavigationModule(@NonNull NavigateView view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    NavigateView provideView() {
        return mView;
    }
}