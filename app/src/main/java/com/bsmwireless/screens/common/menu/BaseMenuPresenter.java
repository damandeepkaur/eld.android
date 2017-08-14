package com.bsmwireless.screens.common.menu;

import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.widgets.alerts.DutyType;

public abstract class BaseMenuPresenter {
    protected DutyManager mDutyManager;

    private DutyManager.DutyTypeListener mListener = new DutyManager.DutyTypeListener() {
        @Override
        public void onDutyTypeChanged(DutyType dutyType) {
            getView().setDutyType(dutyType);
        }
    };

    protected abstract BaseMenuView getView();

    void onMenuCreated() {
        mDutyManager.addListener(mListener);
    }

    void onDutyChanged(DutyType dutyType) {
        mDutyManager.setDutyType(dutyType);
    }

    public void onDestroy() {
        mDutyManager.removeListener(mListener);
    }
}
