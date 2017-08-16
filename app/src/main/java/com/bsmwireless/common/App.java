package com.bsmwireless.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.common.dagger.ContextModule;
import com.bsmwireless.common.dagger.DaggerAppComponent;
import com.bsmwireless.common.logger.ReleaseTree;

import java.lang.ref.WeakReference;

import app.bsmuniversal.com.BuildConfig;
import timber.log.Timber;

public class App extends Application {
    private static AppComponent mComponent;
    private WeakReference<Activity> mCurrentActivity;

    private static final int ACTIVITY_LIFECYCLE_DURATION = 500;

    private static boolean mIsInBackground = false;

    public static AppComponent getComponent() {
        return mComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        mComponent = buildComponent();

        //We shouldn't show home screen if app process was killed
        mComponent.prefsManager().setShowHomeScreenEnabled(false);

        Timber.plant(BuildConfig.USE_LOG ? new Timber.DebugTree() : new ReleaseTree());
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder().contextModule(new ContextModule(this)).build();
    }

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            mCurrentActivity = new WeakReference<>(activity);
            if (mIsInBackground) {
                mIsInBackground = false;
                onAppGoesForeground();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            mCurrentActivity = null;

            Handler mHandler = new Handler();
            mHandler.postDelayed(() -> {
                if (mCurrentActivity == null) {
                    mIsInBackground = true;
                    onAppGoesBackground();
                }
            }, ACTIVITY_LIFECYCLE_DURATION);
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    private void onAppGoesBackground() {

    }

    private void onAppGoesForeground() {

    }

    public static boolean isAppInBackground() {
        return mIsInBackground;
    }
}
