package com.bsmwireless.data.network.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;

public class TokenManager {
    Context mContext;

    public TokenManager(Context context) {
        mContext = context;
    }

    public void setToken(String accountName, String name, String password, String id, String domain, String token) {
        Account account = new Account(accountName, BsmAuthenticator.ACCOUNT_TYPE);

        Bundle bundle = new Bundle();
        bundle.putString(BsmAuthenticator.ACCOUNT_NAME, name);
        bundle.putString(BsmAuthenticator.ACCOUNT_ID, id);
        bundle.putString(BsmAuthenticator.ACCOUNT_DOMAIN, domain);

        //TODO: add password if we are going to provide auto re-login

        AccountManager accountManager = AccountManager.get(mContext);
        accountManager.addAccountExplicitly(account, null, bundle);
        accountManager.setAuthToken(account, BsmAuthenticator.TOKEN_TYPE, token);
    }

    public String getName(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_NAME);
    }

    public String getId(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_ID);
    }

    public String getDomain(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_DOMAIN);
    }

    public String getToken(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).peekAuthToken(account, BsmAuthenticator.TOKEN_TYPE);
    }

    public void clearToken(String token) {
        if (token != null) {
            AccountManager.get(mContext).invalidateAuthToken(BsmAuthenticator.ACCOUNT_TYPE, token);
        }
    }

    private Account getAccount(String accountName) {
        return accountName == null ? null : new Account(accountName, BsmAuthenticator.ACCOUNT_TYPE);
    }
}
