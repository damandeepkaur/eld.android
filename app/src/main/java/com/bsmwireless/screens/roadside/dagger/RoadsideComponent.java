package com.bsmwireless.screens.roadside.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.roadside.RoadsideFragment;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {RoadsideModule.class})
public interface RoadsideComponent {
    void inject(RoadsideFragment roadsideFragment);
}
