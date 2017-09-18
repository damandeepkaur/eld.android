package com.bsmwireless.screens.selectasset.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.selectasset.SelectAssetView;

import dagger.Module;
import dagger.Provides;

@Module
public final class SelectAssetModule {

    private final SelectAssetView mHomeView;

    public SelectAssetModule(@NonNull SelectAssetView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    SelectAssetView provideView() {
        return mHomeView;
    }
}