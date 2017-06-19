package com.bsmwireless.screens.login.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.screens.login.LoginPresenter;
import com.bsmwireless.screens.login.LoginView;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    private final LoginView mHomeView;

    public LoginModule(@NonNull LoginView view) {
        mHomeView = view;
    }

    @LoginScope
    @Provides
    LoginView provideView() {
        return mHomeView;
    }

    @LoginScope
    @Provides
    LoginUserInteractor provideLoginUserInteractor() {
        return new LoginUserInteractor();
    }

    @LoginScope
    @Provides
    LoginPresenter providePresenter(@NonNull LoginView view,@NonNull LoginUserInteractor interactor) {
        return new LoginPresenter(view, interactor);
    }

}