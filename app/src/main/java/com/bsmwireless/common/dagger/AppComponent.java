package com.bsmwireless.common.dagger;

import android.content.Context;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.screens.lockscreen.dagger.LockScreenComponent;
import com.bsmwireless.widgets.common.FontTextView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, NetworkModule.class, DatabaseModule.class, TokenModule.class, PreferencesModule.class, CacheModule.class, DutyModule.class})
public interface AppComponent {
    Context context();

    ServiceApi serviceApi();

    AppDatabase appDatabase();

    TokenManager tokenManager();

    PreferencesManager prefsManager();

    DutyManager dutyManager();

    void inject(FontTextView fontTextView);

    LockScreenComponent.Builder lockScreenBuilder();
}
