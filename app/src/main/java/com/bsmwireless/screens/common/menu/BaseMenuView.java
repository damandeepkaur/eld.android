package com.bsmwireless.screens.common.menu;

import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.ELDType;
import com.bsmwireless.widgets.alerts.OccupancyType;

public interface BaseMenuView {
    void setELDType(ELDType type);
    void setDutyType(DutyType type);
    void setOccupancyType(OccupancyType type);
    void showDutyDialog();
    void changeDutyType(DutyType dutyType);
}
