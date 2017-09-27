package com.bsmwireless.screens.roadsidenavigation.dagger;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.roadsidenavigation.RoadsideNavigationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {RoadsideNavigationModule.class})
public interface RoadsideNavigationComponent {
    void inject(RoadsideNavigationActivity navigationActivity);
}
