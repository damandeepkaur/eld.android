package com.bsmwireless.screens.settings;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.SettingsInteractor;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
public class SettingsPresenter {

    private SettingsView mView;
    private SettingsInteractor mSettingsInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SettingsPresenter(SettingsView view, SettingsInteractor settingsInteractor) {
        mView = view;
        mSettingsInteractor = settingsInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        mView.setBoxGPSSwitchEnabled(mSettingsInteractor.isBoxGPSEnabled());
        mView.setFixedAmountSwitchEnabled(mSettingsInteractor.isFixedAmountEnabled());

        // set default value to km for odometer units
        mView.showOdometerUnits(SettingsView.OdometerUnits.ODOMETER_UNITS_KM);
    }

    public void onBoxGPSSwitchChecked(boolean isBoxGPSEnabled) {
        mSettingsInteractor.saveBoxGPSEnabled(isBoxGPSEnabled);
    }

    public void onFixedAmountSwitchChecked(boolean isFixedAmountEnabled) {
        mSettingsInteractor.saveFixedAmountEnabled(isFixedAmountEnabled);
    }

    public void onKMOdometerUnitsSelected(boolean isKMOdometerUnitsSelected) {
        if (isKMOdometerUnitsSelected) {
            mView.showOdometerUnits(SettingsView.OdometerUnits.ODOMETER_UNITS_KM);
        } else {
            mView.showOdometerUnits(SettingsView.OdometerUnits.ODOMETER_UNITS_MI);
        }

        mSettingsInteractor.saveKMOdometerUnitsSelected(isKMOdometerUnitsSelected);
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
