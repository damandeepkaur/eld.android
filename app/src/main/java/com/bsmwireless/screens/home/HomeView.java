package com.bsmwireless.screens.home;

import com.bsmwireless.widgets.alerts.DutyType;

public interface HomeView {

    void dutyStatusChanged(DutyType dutyType);

    void startHoursOfService();
}
