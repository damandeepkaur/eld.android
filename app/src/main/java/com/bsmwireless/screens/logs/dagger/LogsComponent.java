package com.bsmwireless.screens.logs.dagger;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.logs.LogsFragment;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {LogsModule.class})
public interface LogsComponent {
    void inject(LogsFragment logsFragmentActivity);
}
