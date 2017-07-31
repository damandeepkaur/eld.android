package com.bsmwireless.common.dagger;

import android.content.Context;

import com.bsmwireless.data.network.Connection.ConnectionManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.widgets.common.FontTextView;
import com.bsmwireless.widgets.graphview.ELDGraphView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, NetworkModule.class, DatabaseModule.class, TokenModule.class, PreferencesModule.class, CacheModule.class, BoxModule.class})
public interface AppComponent {
    Context context();

    ServiceApi serviceApi();

    AppDatabase appDatabase();

    TokenManager tokenManager();

    PreferencesManager prefsManager();

    ConnectionManager connManager();

    void inject(FontTextView fontTextView);

    void inject(ELDGraphView eldGraphView);
}
