package com.bsmwireless.screens.settings.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.settings.SettingsActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {SettingsModule.class})
public interface SettingsComponent {
    void inject(SettingsActivity settingsActivity);
}
