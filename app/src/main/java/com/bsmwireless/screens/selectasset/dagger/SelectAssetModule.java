package com.bsmwireless.screens.selectasset.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.selectasset.SelectAssetPresenter;
import com.bsmwireless.screens.selectasset.SelectAssetView;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;

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
    VehiclesInteractor provideSelectAssetUserInteractor(@NonNull ServiceApi serviceApi, @NonNull @Named(Constants.IO_THREAD) Scheduler ioThread,
                                                        @NonNull AppDatabase appDatabase, @NonNull HttpClientManager clientManager,
                                                        @NonNull TokenManager tokenManager, @NonNull PreferencesManager preferencesManager) {
        return new VehiclesInteractor(serviceApi, ioThread, appDatabase, clientManager, tokenManager, preferencesManager);
    }

    @SelectAssetScope
    @Provides
    SelectAssetPresenter providePresenter(@NonNull SelectAssetView view, @NonNull VehiclesInteractor interactor,
                                          @Named(Constants.UI_THREAD) Scheduler scheduler) {
        return new SelectAssetPresenter(view, interactor, scheduler);
    }

}