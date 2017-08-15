package com.bsmwireless.screens.multyday.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.multyday.MultydayFragment;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {MultydayModule.class})
public interface MultydayComponent {
    void inject(MultydayFragment multydayFragment);
}
