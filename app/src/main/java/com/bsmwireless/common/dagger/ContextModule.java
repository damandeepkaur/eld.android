package com.bsmwireless.common.dagger;

import android.content.Context;

import com.bsmwireless.common.utils.StorageUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class ContextModule {
    private final Context mContext;

    public ContextModule(Context context) {
        mContext = context.getApplicationContext();
    }

    @Singleton
    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    static StorageUtil storageUtil(){
        return new StorageUtil();
    }
}