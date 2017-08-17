package com.bsmwireless.screens.multiday.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.multiday.MultidayFragment;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {MultidayModule.class})
public interface MultidayComponent {
    void inject(MultidayFragment multidayFragment);
}
