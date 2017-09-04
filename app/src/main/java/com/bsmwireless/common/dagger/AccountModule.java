package com.bsmwireless.common.dagger;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.PreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class AccountModule {
    @Singleton
    @Provides
    AccountManager provideDutyManager(PreferencesManager preferencesManager) {
        return new AccountManager(preferencesManager);
    }
}
