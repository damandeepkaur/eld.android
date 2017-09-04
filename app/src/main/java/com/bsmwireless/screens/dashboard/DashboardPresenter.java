package com.bsmwireless.screens.dashboard;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.widgets.alerts.DutyType;

import javax.inject.Inject;

import timber.log.Timber;

@ActivityScope
public class DashboardPresenter {
    private DashboardView mView;

    private DutyTypeManager mDutyTypeManager;

    private DutyTypeManager.DutyTypeListener mListener = dutyType -> mView.setDutyType(dutyType);

    @Inject
    public DashboardPresenter(DashboardView view, DutyTypeManager dutyTypeManager) {
        mView = view;

        mDutyTypeManager = dutyTypeManager;
        Timber.d("CREATED");
    }

    void onResume() {
        mView.setDutyType(mDutyTypeManager.getDutyType());
        mDutyTypeManager.addListener(mListener);
    }

    void onPause() {
        mDutyTypeManager.removeListener(mListener);
    }

    public void onDestroy() {
        Timber.d("DESTROYED");
    }

    long getDutyTypeTime(DutyType dutyType) {
        return mDutyTypeManager.getDutyTypeTime(dutyType);
    }
}
