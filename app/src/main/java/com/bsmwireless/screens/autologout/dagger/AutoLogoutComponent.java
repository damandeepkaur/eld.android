package com.bsmwireless.screens.autologout.dagger;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.autologout.AutoDutyDialogActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = {AutoLogoutModule.class} )
public interface AutoLogoutComponent {
    void inject(AutoDutyDialogActivity autoDutyDialogActivity);
}
