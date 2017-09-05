package com.bsmwireless.common.dagger;

import android.content.Context;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.screens.lockscreen.dagger.LockScreenComponent;
import com.bsmwireless.services.monitoring.dagger.MonitoringServiceComponent;
import com.bsmwireless.widgets.common.FontTextView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, NetworkModule.class, DatabaseModule.class, TokenModule.class, PreferencesModule.class, CacheModule.class, BlackBoxModule.class, DutyModule.class, AutoDutyModule.class, AccountModule.class})
public interface AppComponent {
    Context context();

    ServiceApi serviceApi();

    AppDatabase appDatabase();

    TokenManager tokenManager();

    PreferencesManager prefsManager();

    DutyTypeManager dutyTypeManager();

    AccountManager accountManager();

    AutoDutyTypeManager autoDutyTypeManager();

    BlackBoxConnectionManager blackBoxConnectionManager();

    void inject(FontTextView fontTextView);

    LockScreenComponent.Builder lockScreenBuilder();
    MonitoringServiceComponent.Builder monitoringServiceBuilder();
}
