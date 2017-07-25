package com.bsmwireless.domain.interactors;

import android.content.SharedPreferences;

import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;

import javax.inject.Inject;

import io.reactivex.Completable;

public class SettingsInteractor {

    private AppDatabase mAppDatabase;
    private PreferencesManager mPreferencesManager;

    @Inject
    public SettingsInteractor(AppDatabase appDatabase, PreferencesManager preferencesManager) {
        mAppDatabase = appDatabase;
        mPreferencesManager = preferencesManager;
    }

    public Completable registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        return Completable.fromAction(
                () -> mPreferencesManager.registerOnSharedPreferenceChangeListener(listener));

    }

    public Completable unRegisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        return Completable.fromAction(
                () -> mPreferencesManager.unRegisterOnSharedPreferenceChangeListener(listener));
    }
}
