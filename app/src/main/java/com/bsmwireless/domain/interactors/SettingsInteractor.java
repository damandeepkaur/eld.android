package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;

import javax.inject.Inject;

public class SettingsInteractor {

    private AppDatabase mAppDatabase;
    private PreferencesManager mPreferencesManager;

    @Inject
    public SettingsInteractor(AppDatabase appDatabase, PreferencesManager preferencesManager) {
        mAppDatabase = appDatabase;
        mPreferencesManager = preferencesManager;
    }
}
