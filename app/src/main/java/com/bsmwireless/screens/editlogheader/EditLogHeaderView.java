package com.bsmwireless.screens.editlogheader;

import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.screens.logs.LogHeaderModel;

public interface EditLogHeaderView extends BaseMenuView {
    LogHeaderModel getLogHeader(LogHeaderModel logHeaderModel);

    void saveLogHeader(LogHeaderModel logHeaderModel);

    void setLogHeaderModel(LogHeaderModel logHeaderModel);
}
