package com.bsmwireless.screens.editlogheader.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.editlogheader.EditLogHeaderView;

import dagger.Module;
import dagger.Provides;

@Module
public class EditLogHeaderModule {
    private final EditLogHeaderView mHomeView;

    public EditLogHeaderModule(@NonNull EditLogHeaderView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    EditLogHeaderView provideView() {
        return mHomeView;
    }
}
