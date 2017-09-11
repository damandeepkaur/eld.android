package com.bsmwireless.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.common.dagger.ContextModule;
import com.bsmwireless.common.dagger.DaggerAppComponent;
import com.bsmwireless.common.logger.ReleaseTree;

import java.util.concurrent.atomic.AtomicInteger;

import app.bsmuniversal.com.BuildConfig;
import timber.log.Timber;

public class App extends Application {
    private static AppComponent mComponent;

    public static AppComponent getComponent() {
        return mComponent;
    }

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        AtomicInteger mCreated = new AtomicInteger(0);

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mCreated.incrementAndGet();
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (mCreated.decrementAndGet() == 0) {
                mComponent.blackBoxConnectionManager().disconnectBlackBox().subscribe();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        mComponent = buildComponent();

        //We shouldn't show home screen if app process was killed
        mComponent.prefsManager().setShowHomeScreenEnabled(false);

        //We should enable remember me by default
        mComponent.prefsManager().setRememberUserEnabled(true);

        Timber.plant(BuildConfig.USE_LOG ? new Timber.DebugTree() : new ReleaseTree());
    }

    protected AppComponent buildComponent() {
         return DaggerAppComponent.builder().contextModule(new ContextModule(this)).build();
    }
}
