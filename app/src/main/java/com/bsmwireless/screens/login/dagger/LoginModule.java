package com.bsmwireless.screens.login.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.login.LoginView;

import dagger.Module;
import dagger.Provides;

@Module
public final class LoginModule {

    private final LoginView mHomeView;

    public LoginModule(@NonNull LoginView view) {
        mHomeView = view;
    }

    @ActivityScope
    @Provides
    LoginView provideView() {
        return mHomeView;
    }
}