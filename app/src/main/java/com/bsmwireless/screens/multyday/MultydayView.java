package com.bsmwireless.screens.multyday;

import java.util.List;

public interface MultydayView {
    void setItems(List<MultydayItemModel> items);
    void setTotalOffDuty(String time);
    void setTotalSleeping(String time);
    void setTotalDriving(String time);
    void setTotalOnDuty(String time);
}
