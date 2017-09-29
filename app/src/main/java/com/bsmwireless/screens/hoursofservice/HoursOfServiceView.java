package com.bsmwireless.screens.hoursofservice;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

public interface HoursOfServiceView extends BaseMenuView {
    void showReassignDialog(ELDEvent event);

    SnackBarLayout getSnackBar();

    void setResetTime(long time);

    void setTitle(long boxId, String driverName);

}
