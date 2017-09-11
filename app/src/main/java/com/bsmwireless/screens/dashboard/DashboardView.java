package com.bsmwireless.screens.dashboard;

import com.bsmwireless.widgets.alerts.DutyType;

public interface DashboardView {
    void setDutyType(DutyType dutyType);
    void showDutyTypeDialog();
    void showNotInVehicleDialog();
}
