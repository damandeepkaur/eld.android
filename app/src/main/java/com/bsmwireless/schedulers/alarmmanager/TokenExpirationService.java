package com.bsmwireless.schedulers.alarmmanager;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bsmwireless.common.App;
import com.bsmwireless.data.network.authenticator.TokenManager;

import javax.inject.Inject;

import timber.log.Timber;

import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_NAME;
import static com.bsmwireless.data.network.authenticator.BsmAuthenticator.ACCOUNT_TYPE;

public final class TokenExpirationService extends IntentService {
    @Inject
    TokenManager mTokenManager;

    public TokenExpirationService() {
        super(TokenExpirationService.class.getSimpleName());
        Timber.v("TokenExpirationService: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.v("onCreate: ");
        App.getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.v("onHandleIntent: ");
        final Bundle pb = intent.getExtras();
        final String name = pb.getString(ACCOUNT_NAME);
        final String accountType = pb.getString(ACCOUNT_TYPE);
        mTokenManager.getTokenExpirationHandler().onTokenExpired(this, accountType, name);
    }
}
