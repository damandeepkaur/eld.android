package com.bsmwireless.common;

import android.app.Application;

import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.common.dagger.ContextModule;
import com.bsmwireless.common.dagger.DaggerAppComponent;
import com.bsmwireless.common.logger.ReleaseTree;

import app.bsmuniversal.com.BuildConfig;
import timber.log.Timber;

public class App extends Application {
    private static AppComponent mComponent;

    public static AppComponent getComponent() {
        return mComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = buildComponent();

        //We shouldn't show home screen if app process was killed
        mComponent.prefsManager().setShowHomeScreenEnabled(false);

        Timber.plant(BuildConfig.USE_LOG ? new Timber.DebugTree() : new ReleaseTree());
    }

    protected AppComponent buildComponent() {
         return DaggerAppComponent.builder().contextModule(new ContextModule(this)).build();
    }
}