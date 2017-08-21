package com.bsmwireless.common.dagger;

import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.data.storage.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class DutyModule {
    @Singleton
    @Provides
    DutyManager provideDutyManager(PreferencesManager preferencesManager) {
        return new DutyManager(preferencesManager);
    }
}
