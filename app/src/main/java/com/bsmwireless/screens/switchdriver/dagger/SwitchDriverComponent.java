package com.bsmwireless.screens.switchdriver.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.switchdriver.SwitchDriverDialog;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {SwitchDriverModule.class})
public interface SwitchDriverComponent {
    void inject(SwitchDriverDialog switchDriverDialog);
}
