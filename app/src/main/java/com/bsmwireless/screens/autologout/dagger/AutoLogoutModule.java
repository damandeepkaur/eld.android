package com.bsmwireless.screens.autologout.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.autologout.AutoDutyDialogView;

import dagger.Module;
import dagger.Provides;

@Module
public class AutoLogoutModule {

    private final AutoDutyDialogView mAutoDutyDialogView;

    public AutoLogoutModule(@NonNull AutoDutyDialogView view) {
        mAutoDutyDialogView = view;
    }

    @ActivityScope
    @Provides
    AutoDutyDialogView provideView() {
        return mAutoDutyDialogView;
    }
}
