package com.bsmwireless.screens.settings;

import com.bsmwireless.screens.common.menu.BaseMenuView;

public interface SettingsView extends BaseMenuView {

    enum OdometerUnits {
        ODOMETER_UNITS_KM,
        ODOMETER_UNITS_MI
    }

    void setBoxGPSSwitchEnabled(boolean isEnabled);

    void setFixedAmountSwitchEnabled(boolean isEnabled);

    void showPopupMenu();

    void showOdometerUnits(OdometerUnits odometerUnits);
}
