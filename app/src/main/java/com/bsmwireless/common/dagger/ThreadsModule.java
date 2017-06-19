package com.bsmwireless.common.dagger;


import com.bsmwireless.common.Constants;

import javax.inject.Named;
import javax.inject.Singleton;


import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


@Module
public class ThreadsModule {

    @Provides
    @Singleton
    @Named(Constants.UI_THREAD)
    Scheduler provideSchedulerUI() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    @Named(Constants.IO_THREAD)
    Scheduler provideSchedulerIO() {
        return Schedulers.io();
    }
}
