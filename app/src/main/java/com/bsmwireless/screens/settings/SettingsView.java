package com.bsmwireless.screens.settings;

public interface SettingsView {

    enum OdometerUnits {
        ODOMETER_UNITS_KM,
        ODOMETER_UNITS_MI
    }

    void setBoxGPSSwitchEnabled(boolean isEnabled);

    void setFixedAmountSwitchEnabled(boolean isEnabled);

    void showPopupMenu();

    void showOdometerUnits(OdometerUnits odometerUnits);
}
