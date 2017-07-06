package com.bsmwireless.common.dagger;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.widgets.common.FontTextView;
import com.bsmwireless.widgets.graphview.HOSGraphLabelView;
import com.bsmwireless.widgets.graphview.HOSGraphView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, NetworkModule.class, DatabaseModule.class, TokenModule.class, PreferencesModule.class, CacheModule.class})
public interface AppComponent {
    ServiceApi serviceApi();

    AppDatabase appDatabase();

    TokenManager tokenManager();

    PreferencesManager prefsManager();

    void inject(FontTextView fontTextView);

    void inject(HOSGraphView hosGraphView);

    void inject(HOSGraphLabelView hosGraphLabelView);
}
