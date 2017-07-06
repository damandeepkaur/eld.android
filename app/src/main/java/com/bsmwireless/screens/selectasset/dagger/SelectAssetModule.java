package com.bsmwireless.screens.selectasset.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.selectasset.SelectAssetPresenter;
import com.bsmwireless.screens.selectasset.SelectAssetView;

import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
    VehiclesInteractor provideSelectAssetUserInteractor(@NonNull ServiceApi serviceApi,
                                                        @NonNull PreferencesManager preferencesManager,
                                                        @NonNull AppDatabase appDatabase) {
        return new VehiclesInteractor(serviceApi, preferencesManager, appDatabase);
    }

    @SelectAssetScope
    @Provides
    SelectAssetPresenter providePresenter(@NonNull SelectAssetView view, @NonNull VehiclesInteractor interactor) {
        return new SelectAssetPresenter(view, interactor, AndroidSchedulers.mainThread());
    }

}