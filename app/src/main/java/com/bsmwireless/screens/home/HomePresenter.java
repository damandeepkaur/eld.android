package com.bsmwireless.screens.home;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyTypeManager;

@ActivityScope
public class HomePresenter {

    private final DutyTypeManager mDutyTypeManager;
    private HomeView mHomeView;

    public HomePresenter(DutyTypeManager dutyTypeManager) {
        mDutyTypeManager = dutyTypeManager;
    }

    public void onStart(HomeView homeView) {
        mHomeView = homeView;
        startDutyTypeMonitoring();
    }

    public void onStop(){
        mHomeView = null;
    }

    public void onHoursOfService() {
        if (mHomeView == null) {
            return;
        }

        mHomeView.startHoursOfService();
    }

    public void onPreTrip() {

    }

    public void onPostTrip() {

    }

    public void onInspections() {

    }

    private void startDutyTypeMonitoring(){
    }
}
