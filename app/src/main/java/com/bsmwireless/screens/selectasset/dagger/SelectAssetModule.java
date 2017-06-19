package com.bsmwireless.screens.selectasset.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.selectasset.SelectAssetPresenter;
import com.bsmwireless.screens.selectasset.SelectAssetView;

import dagger.Module;
import dagger.Provides;

@Module
public class SelectAssetModule {

    private final SelectAssetView mHomeView;

    public SelectAssetModule(@NonNull SelectAssetView view) {
        mHomeView = view;
    }

    @SelectAssetScope
    @Provides
    SelectAssetView provideView() {
        return mHomeView;
    }

    @SelectAssetScope
    @Provides
    VehiclesInteractor provideSelectAssetUserInteractor() {
        return new VehiclesInteractor();
    }

    @SelectAssetScope
    @Provides
    SelectAssetPresenter providePresenter(@NonNull SelectAssetView view, @NonNull VehiclesInteractor interactor) {
        return new SelectAssetPresenter(view, interactor);
    }

}