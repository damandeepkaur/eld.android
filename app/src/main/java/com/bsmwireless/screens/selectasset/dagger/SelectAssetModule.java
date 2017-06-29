package com.bsmwireless.screens.selectasset.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.selectasset.SelectAssetView;

import dagger.Module;
import dagger.Provides;

@Module
public class SelectAssetModule {

    private final SelectAssetView mHomeView;

    public SelectAssetModule(SelectAssetView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    SelectAssetView provideView() {
        return mHomeView;
    }
}