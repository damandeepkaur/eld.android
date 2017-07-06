package com.bsmwireless.screens.login.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.screens.login.LoginPresenter;
import com.bsmwireless.screens.login.LoginView;

import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
    LoginUserInteractor provideLoginUserInteractor(@NonNull ServiceApi serviceApi,
                                                   @NonNull PreferencesManager preferencesManager,
                                                   @NonNull AppDatabase appDatabase,
                                                   @NonNull TokenManager tokenManager) {
        return new LoginUserInteractor(serviceApi, preferencesManager, appDatabase, tokenManager);
    }

    @LoginScope
    @Provides
    LoginPresenter providePresenter(@NonNull LoginView view, @NonNull LoginUserInteractor interactor) {
        return new LoginPresenter(view, interactor, AndroidSchedulers.mainThread());
    }

}