package com.bsmwireless.screens.navigation.dagger;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.navigation.NavigationActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {NavigationModule.class})
public interface NavigationComponent {
    void inject(NavigationActivity navigationActivity);
}
