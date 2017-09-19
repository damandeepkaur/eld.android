package com.bsmwireless.common.dagger;

import android.content.Context;

import com.bsmwireless.data.network.authenticator.TokenManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class TokenModule {
    @Singleton
    @Provides
    public TokenManager provideTokenManager(Context context) {
        return new TokenManager(context);
    }
}
