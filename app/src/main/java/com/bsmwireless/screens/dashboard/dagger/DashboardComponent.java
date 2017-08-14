package com.bsmwireless.screens.dashboard.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.dashboard.DashboardFragment;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {DashboardModule.class})
public interface DashboardComponent {
    void inject(DashboardFragment dashboardFragment);
}
