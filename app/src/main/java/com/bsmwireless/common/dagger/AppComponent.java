package com.bsmwireless.common.dagger;

import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.login.LoginPresenter;
import com.bsmwireless.screens.selectasset.SelectAssetPresenter;
import com.bsmwireless.widgets.FontTextView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ThreadsModule.class, ContextModule.class, NetworkModule.class, DatabaseModule.class, TokenModule.class, PreferencesModule.class, CacheModule.class})
public interface AppComponent {
    void inject(LoginPresenter loginPresenter);

    void inject(LoginUserInteractor loginUserInteractor);

    void inject(SelectAssetPresenter selectAssetPresenter);

    void inject(VehiclesInteractor vehiclesInteractor);

    void inject(FontTextView fontTextView);
}
