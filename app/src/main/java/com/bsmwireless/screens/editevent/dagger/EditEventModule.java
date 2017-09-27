package com.bsmwireless.screens.editevent.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.editevent.EditEventView;

import dagger.Module;
import dagger.Provides;

@Module
public final class EditEventModule {
    private final EditEventView mHomeView;

    public EditEventModule(@NonNull EditEventView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    EditEventView provideView() {
        return mHomeView;
    }
}
