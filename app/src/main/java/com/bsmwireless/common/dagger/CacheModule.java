package com.bsmwireless.common.dagger;

import android.content.Context;

import com.bsmwireless.data.storage.FontCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;

import static com.bsmwireless.common.Constants.CACHE_SIZE;

/**
 * Created by anartem on 14.06.2017.
 */
@Module
public class CacheModule {
    @Singleton
    @Provides
    FontCache provideFontCache(Context context) {
        return new FontCache(context);
    }

    @Singleton
    @Provides
    Cache provideHttpCache(Context context) {
        return new Cache(context.getCacheDir(), CACHE_SIZE);
    }
}
