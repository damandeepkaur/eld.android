package com.bsmwireless.common.dagger;

import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManagerImpl;
import com.bsmwireless.data.network.blackbox.BlackBoxImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by osminin on 10.08.2017.
 */

@Module
public class BlackBoxModule {

    @Provides
    BlackBox provideBlackBox() {
        return new BlackBoxImpl();
    }

    @Provides
    @Singleton
    BlackBoxConnectionManager provideConnectionManager(BlackBox blackBox) {
        return new BlackBoxConnectionManagerImpl(blackBox);
    }
}
