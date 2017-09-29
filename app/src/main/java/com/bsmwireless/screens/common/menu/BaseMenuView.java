package com.bsmwireless.screens.common.menu;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.ELDType;
import com.bsmwireless.widgets.alerts.OccupancyType;

public interface BaseMenuView {
    void setELDType(ELDType type);
    void setDutyType(DutyType type);
    void setOccupancyType(OccupancyType type);
    void showDutyTypeDialog(DutyType dutyType);
    void showNotInVehicleDialog();
    void changeDutyType(DutyType dutyType, String comment);
    void showSwitchDriverDialog();
    void showReassignEventDialog(ELDEvent event);
    void showCoDriverView(String name);
    void hideCoDriverView();
    void showMalfunctionDialog();
    void showDiagnosticEvents();
    void changeMalfunctionStatus(boolean hasMalfunctionEvents);
    void changeDiagnosticStatus(boolean hasMalfunctionEvents);
}
