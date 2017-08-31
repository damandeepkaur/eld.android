package com.bsmwireless.screens.lockscreen;

import com.bsmwireless.widgets.alerts.DutyType;

public interface LockScreenView {
    void setTimeForDutyType(DutyType dutyType, long time);
    void setCurrentTime(long interval);

    void openCoDriverDialog();
}
