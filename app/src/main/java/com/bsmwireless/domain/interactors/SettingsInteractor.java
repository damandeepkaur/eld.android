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

    public void saveBoxGPSEnabled(boolean boxGPSEnabled) {
        mPreferencesManager.setBoxGPSEnabled(boxGPSEnabled);
    }

    public void saveFixedAmountEnabled(boolean fixedAmountEnabled) {
        mPreferencesManager.setFixedAmountEnabled(fixedAmountEnabled);
    }

    public boolean isBoxGPSEnabled() {
        return mPreferencesManager.isBoxGPSEnabled();
    }

    public boolean isFixedAmountEnabled() {
        return mPreferencesManager.isFixedAmountEnabled();
    }

    public void saveKMOdometerUnitsSelected(boolean kmOdometerUnitsSelected) {
        mPreferencesManager.setKMOdometerUnits(kmOdometerUnitsSelected);
    }

    public boolean isKMOdometerUnitsSelected() {
        return mPreferencesManager.isKMOdometerUnitsSelected();
    }
}
