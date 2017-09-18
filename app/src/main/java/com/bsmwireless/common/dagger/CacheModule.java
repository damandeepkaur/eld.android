package com.bsmwireless.common.dagger;

import android.content.Context;

import com.bsmwireless.data.storage.FontCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class CacheModule {
    @Singleton
    @Provides
    FontCache provideFontCache(Context context) {
        return new FontCache(context);
    }
}
