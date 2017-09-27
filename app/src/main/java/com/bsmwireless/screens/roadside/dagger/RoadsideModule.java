package com.bsmwireless.screens.roadside.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.roadside.RoadsideView;

import dagger.Module;
import dagger.Provides;

@Module
public final class RoadsideModule {

    private final RoadsideView mView;

    public RoadsideModule(@NonNull RoadsideView view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    RoadsideView provideView() {
        return mView;
    }
}
