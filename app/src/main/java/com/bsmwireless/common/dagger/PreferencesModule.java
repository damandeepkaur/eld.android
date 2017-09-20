package com.bsmwireless.common.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bsmwireless.data.storage.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class PreferencesModule {
    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Singleton
    @Provides
    PreferencesManager providePreferencesManager(SharedPreferences prefs) {
        return new PreferencesManager(prefs);
    }
}
