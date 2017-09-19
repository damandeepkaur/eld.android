package com.bsmwireless.common.dagger;

import android.content.Context;

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
}