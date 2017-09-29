package com.bsmwireless.screens.hoursofservice;

import com.bsmwireless.common.dagger.ActivityScope;

import dagger.BindsInstance;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface HoursOfServiceComponent {

    void inject(HoursOfServiceActivity hoursOfServiceActivity);

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        Builder view(HoursOfServiceView baseMenuView);

        HoursOfServiceComponent build();
    }
}
