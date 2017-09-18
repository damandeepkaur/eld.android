package com.bsmwireless.data.network.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.bsmwireless.models.Auth;

import timber.log.Timber;

public final class TokenManager {
    private Context mContext;

    public TokenManager(Context context) {
        mContext = context;
    }

    public void setToken(String accountName, String name, String pass, String domain, Auth auth) {
        AccountManager accountManager = AccountManager.get(mContext);
        Account account = getAccount(name);

        if (account == null) {
            account = new Account(accountName, BsmAuthenticator.ACCOUNT_TYPE);

            accountManager.addAccountExplicitly(account, null, null);
            accountManager.setAuthToken(account, BsmAuthenticator.TOKEN_TYPE, auth.getToken());

            //TODO: schedule token expiration
        }

        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_NAME, name);
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_PASS, pass);
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_DOMAIN, domain);
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_DRIVER, String.valueOf(auth.getDriverId()));
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_ORG, String.valueOf(auth.getOrgId()));
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_CLUSTER, auth.getCluster());
    }

    public String getName(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_NAME);
    }

    public String getPassword(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_PASS);
    }

    public String getDriver(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_DRIVER);
    }

    public String getDomain(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_DOMAIN);
    }

    public String getOrg(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_ORG);
    }

    public String getCluster(String accountName) {
        Account account = getAccount(accountName);
        return account == null ? null : AccountManager.get(mContext).getUserData(account, BsmAuthenticator.ACCOUNT_CLUSTER);
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
        Account[] accounts = AccountManager.get(mContext).getAccountsByType(BsmAuthenticator.ACCOUNT_TYPE);

        for (Account account : accounts) {
            if (account.name.equals(accountName)) {
                return account;
            }
        }

        return null;
    }

    public void removeAccount(String accountName) {
        Account account = getAccount(accountName);
        if (account != null) {
            if (Build.VERSION.SDK_INT < 22) {
                AccountManager.get(mContext).removeAccount(account, null, null);
            } else {
                AccountManager.get(mContext).removeAccountExplicitly(account);
            }
        }
    }

    //TODO: should we use encryption for account name?
    public String getAccountName(String user, String domain) {
        String accountName = user + ":" + domain;
        try {
            accountName = Base64.encodeToString(accountName.getBytes("UTF-8"), Base64.DEFAULT).trim();
        } catch (Exception e) {
            Timber.e(e, "Account name encoding failed");
        }

        return accountName;
    }
}
