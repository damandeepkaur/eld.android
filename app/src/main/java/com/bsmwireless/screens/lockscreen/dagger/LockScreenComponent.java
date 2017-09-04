package com.bsmwireless.screens.lockscreen.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.lockscreen.LockScreenActivity;
import com.bsmwireless.screens.lockscreen.LockScreenView;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface LockScreenComponent {

    void inject(LockScreenActivity lockScreenActivity);

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance Builder view(LockScreenView view);
        @BindsInstance Builder disconnectionTimeout(@Named("disconnectTimeout") long timeoutMillis);
        @BindsInstance Builder idleTimeout(@Named("idleTimeout") long timeoutMillis);
        LockScreenComponent build();
    }

}
