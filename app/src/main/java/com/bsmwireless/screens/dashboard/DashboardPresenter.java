package com.bsmwireless.screens.dashboard;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.widgets.alerts.DutyType;

import javax.inject.Inject;

import timber.log.Timber;

@ActivityScope
public class DashboardPresenter {
    private DashboardView mView;

    private DutyManager mDutyManager;

    private DutyManager.DutyTypeListener mListener = new DutyManager.DutyTypeListener() {
        @Override
        public void onDutyTypeChanged(DutyType dutyType) {
            mView.setDutyType(dutyType);
        }
    };

    @Inject
    public DashboardPresenter(DashboardView view, DutyManager dutyManager) {
        mView = view;

        mDutyManager = dutyManager;
        Timber.d("CREATED");
    }

    void onResume() {
        mView.setDutyType(mDutyManager.getDutyType());
        mDutyManager.addListener(mListener);
    }

    void onPause() {
        mDutyManager.removeListener(mListener);
    }

    public void onDestroy() {
        Timber.d("DESTROYED");
    }

    int getDutyTypeTime(DutyType dutyType) {
        return mDutyManager.getDutyTypeTime(dutyType);
    }
}
