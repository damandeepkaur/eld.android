package com.bsmwireless.screens.transfer.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.transfer.TransferView;

import dagger.Module;
import dagger.Provides;

@Module
public final class TransferModule {

    private final TransferView mHomeView;

    public TransferModule(@NonNull TransferView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    TransferView provideView() {
        return mHomeView;
    }
}
