package com.bsmwireless.screens.login.dagger;

import android.support.annotation.NonNull;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.screens.login.LoginPresenter;
import com.bsmwireless.screens.login.LoginView;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
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
    LoginUserInteractor provideLoginUserInteractor(@NonNull ServiceApi serviceApi, @NonNull @Named(Constants.IO_THREAD) Scheduler ioThread,
                                                   @NonNull AppDatabase appDatabase, @NonNull HttpClientManager clientManager,
                                                   @NonNull TokenManager tokenManager, @NonNull PreferencesManager prefsManager) {
        return new LoginUserInteractor(serviceApi, ioThread, appDatabase, clientManager, tokenManager, prefsManager);
    }

    @LoginScope
    @Provides
    LoginPresenter providePresenter(@NonNull LoginView view, @NonNull LoginUserInteractor interactor,
                                    @NonNull @Named(Constants.UI_THREAD) Scheduler scheduler) {
        return new LoginPresenter(view, interactor, scheduler);
    }

}