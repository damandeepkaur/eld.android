package com.bsmwireless.common.dagger;

import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.login.LoginPresenter;
import com.bsmwireless.screens.selectasset.SelectAssetPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ThreadsModule.class, ContextModule.class, NetworkModule.class, DatabaseModule.class, TokenModule.class, PreferencesModule.class})
public interface AppComponent {
    void inject(LoginPresenter loginPresenter);

    void inject(LoginUserInteractor loginUserInteractor);

    void inject(SelectAssetPresenter selectAssetPresenter);

    void inject(VehiclesInteractor vehiclesInteractor);
}
