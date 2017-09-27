package com.bsmwireless.screens.settings;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.SettingsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

import timber.log.Timber;

@ActivityScope
public final class SettingsPresenter extends BaseMenuPresenter {

    private SettingsView mView;
    private SettingsInteractor mSettingsInteractor;

    @Inject
    public SettingsPresenter(SettingsView view,
                             SettingsInteractor settingsInteractor,
                             DutyTypeManager dutyTypeManager,
                             UserInteractor userInteractor,
                             ELDEventsInteractor eldEventsInteractor,
                             AccountManager accountManager) {
        super(dutyTypeManager, eldEventsInteractor, userInteractor, accountManager);
        mView = view;
        mSettingsInteractor = settingsInteractor;

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        mView.setBoxGPSSwitchEnabled(mSettingsInteractor.isBoxGPSEnabled());
        mView.setFixedAmountSwitchEnabled(mSettingsInteractor.isFixedAmountEnabled());

        // set current selected value for odometer units
        mView.checkOdometerUnit(loadLastSelectedOdometerUnit());
    }

    public void onBoxGPSSwitchChecked(boolean isBoxGPSEnabled) {
        mSettingsInteractor.saveBoxGPSEnabled(isBoxGPSEnabled);
    }

    public void onFixedAmountSwitchChecked(boolean isFixedAmountEnabled) {
        mSettingsInteractor.saveFixedAmountEnabled(isFixedAmountEnabled);
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    public void onUnitsSelected(boolean isKMOdometerUnitsSelected) {
        if (isKMOdometerUnitsSelected) {
            mView.checkOdometerUnit(SettingsView.OdometerUnits.ODOMETER_UNITS_KM);
        } else {
            mView.checkOdometerUnit(SettingsView.OdometerUnits.ODOMETER_UNITS_MI);
        }

        mSettingsInteractor.saveKMOdometerUnitsSelected(isKMOdometerUnitsSelected);
    }

    private SettingsView.OdometerUnits loadLastSelectedOdometerUnit() {
        if (mSettingsInteractor.isKMOdometerUnitsSelected()) {
            return SettingsView.OdometerUnits.ODOMETER_UNITS_KM;
        }
        return SettingsView.OdometerUnits.ODOMETER_UNITS_MI;
    }
}
