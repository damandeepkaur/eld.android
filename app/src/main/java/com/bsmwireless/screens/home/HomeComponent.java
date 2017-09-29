package com.bsmwireless.screens.home;

import com.bsmwireless.common.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface HomeComponent {

    void inject(HomeFragment homeActivity);

    @Subcomponent.Builder
    interface Builder {
        HomeComponent build();
    }
}
