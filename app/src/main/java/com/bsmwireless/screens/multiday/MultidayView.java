package com.bsmwireless.screens.multiday;

import java.util.List;

public interface MultidayView {
    void setItems(List<MultidayItemModel> items);
    void setTotalOffDuty(String time);
    void setTotalSleeping(String time);
    void setTotalDriving(String time);
    void setTotalOnDuty(String time);
    int getDayCount();
}
