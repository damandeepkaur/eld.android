package com.bsmwireless.screens.editlogheader;

import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.screens.logs.LogHeaderModel;

import java.util.List;

public interface EditLogHeaderView extends BaseMenuView {
    LogHeaderModel getLogHeader(LogHeaderModel logHeaderModel);

    void saveLogHeader(LogHeaderModel logHeaderModel);

    void setLogHeaderModel(LogHeaderModel logHeaderModel);

    List<EditLogHeaderActivity.ExemptionModel> getExemptions();
}
