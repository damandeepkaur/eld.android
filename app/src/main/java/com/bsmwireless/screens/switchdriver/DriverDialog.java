package com.bsmwireless.screens.switchdriver;

import com.bsmwireless.models.ELDEvent;

public interface DriverDialog {
    void show();
    void showReassignEventDialog(ELDEvent event);
}
