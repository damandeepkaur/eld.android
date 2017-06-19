package com.bsmwireless.data.network.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bsmwireless.screens.login.LoginActivity;

import javax.inject.Inject;

public class BsmAuthenticator extends AbstractAccountAuthenticator {
    public static final String ACCOUNT_NAME = "com.bsmwireless.account.name";
    public static final String ACCOUNT_TYPE = "com.bsmwireless.account.type";
    public static final String ACCOUNT_DOMAIN = "com.bsmwireless.account.domain";
    public static final String ACCOUNT_ID = "com.bsmwireless.account.id";
    public static final String TOKEN_TYPE = "Full access";
    public static final String TOKEN_LABEL = "Full access to an BSM Wireless account";

    private Context mContext;

    public BsmAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        AccountManager accountManager = AccountManager.get(mContext);
        String authToken = accountManager.peekAuthToken(account, authTokenType);

        final Bundle result = new Bundle();

        if (authToken != null && !authToken.isEmpty()) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        } else {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, accountManager.getUserData(account, ACCOUNT_NAME));
            intent.putExtra(LoginActivity.ARG_DOMAIN_NAME, accountManager.getUserData(account, ACCOUNT_DOMAIN));
            result.putParcelable(AccountManager.KEY_INTENT, intent);
        }

        return result;
    }


    @Override
    public String getAuthTokenLabel(String authTokenType) {
         return TOKEN_LABEL;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
}
