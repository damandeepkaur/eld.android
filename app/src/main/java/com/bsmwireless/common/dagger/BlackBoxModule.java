package com.bsmwireless.common.dagger;

import com.bsmwireless.common.utils.BlackBoxSimpleChecker;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManagerImpl;
import com.bsmwireless.data.network.blackbox.BlackBoxImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BlackBoxModule {

    @Provides
    static BlackBox provideBlackBox() {
        return new BlackBoxImpl();
    }

    @Provides
    @Singleton
    static BlackBoxConnectionManager provideConnectionManager(BlackBox blackBox) {
        return new BlackBoxConnectionManagerImpl(blackBox);
    }

    @Provides
    @Singleton
    static BlackBoxStateChecker provideChecker() {
        return new BlackBoxSimpleChecker();
    }
}
