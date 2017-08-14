package com.bsmwireless.screens.settings;

import com.bsmwireless.screens.common.menu.BaseMenuView;

public interface SettingsView extends BaseMenuView {

    void setBoxGPSSwitchEnabled(boolean isEnabled);

    void setFixedAmountSwitchEnabled(boolean isEnabled);
}
