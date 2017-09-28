package com.bsmwireless.screens.dashboard.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.screens.dashboard.HoursOfServiceActivity;

import dagger.BindsInstance;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface HoursOfServiceComponent {

    void inject(HoursOfServiceActivity hoursOfServiceActivity);

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        Builder view(BaseMenuView baseMenuView);

        HoursOfServiceComponent build();
    }
}
