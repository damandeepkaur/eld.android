package com.bsmwireless.common.dagger;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.widgets.common.FontTextView;
import com.bsmwireless.widgets.graphview.HOSGraphLabelView;
import com.bsmwireless.widgets.graphview.HOSGraphView;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.Scheduler;

@Singleton
@Component(modules = {ThreadsModule.class, ContextModule.class, NetworkModule.class, DatabaseModule.class, TokenModule.class, PreferencesModule.class, CacheModule.class})
public interface AppComponent {

    @Named(Constants.UI_THREAD)
    Scheduler uiScheduler();

    @Named(Constants.IO_THREAD)
    Scheduler ioScheduler();

    ServiceApi serviceApi();

    AppDatabase appDatabase();

    HttpClientManager clientManager();

    TokenManager tokenManager();

    PreferencesManager prefsManager();

    void inject(FontTextView fontTextView);

    void inject(HOSGraphView hosGraphView);

    void inject(HOSGraphLabelView hosGraphLabelView);
}
