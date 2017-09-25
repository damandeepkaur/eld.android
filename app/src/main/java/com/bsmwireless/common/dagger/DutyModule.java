package com.bsmwireless.common.dagger;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class DutyModule {
    @Singleton
    @Provides
    DutyTypeManager provideDutyTypeManager(PreferencesManager preferencesManager) {
        return new DutyTypeManager(preferencesManager);
    }
}
