package com.bsmwireless.data.network.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Base64;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.models.Auth;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.screens.autologout.AutoDutyDialogActivity;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.R.attr.accountType;
import static com.bsmwireless.screens.autologout.AutoDutyDialogActivity.EXTRA_TOKEN_EXPIRED;

public class TokenManager {
    private final Context mContext;
    private TokenExpirationHandler mTokenExpirationHandler;

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

            SchedulerUtils.scheduleTokenExpiration(accountName, name, auth.getExpire());
        }

        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_NAME, name);
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_PASS, pass);
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_DOMAIN, domain);
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_DRIVER, String.valueOf(auth.getDriverId()));
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_ORG, String.valueOf(auth.getOrgId()));
        accountManager.setUserData(account, BsmAuthenticator.ACCOUNT_CLUSTER, auth.getCluster());
    }

    public Boolean refreshToken(String accountName, String name, Auth auth) {
        AccountManager accountManager = AccountManager.get(mContext);
        Account account = getAccount(name);
        if (account == null) {
            return false;
        }
        SchedulerUtils.cancelTokenExpiration(name);
        accountManager.setAuthToken(account, BsmAuthenticator.TOKEN_TYPE, auth.getToken());
        SchedulerUtils.scheduleTokenExpiration(accountName, name, auth.getExpire());
        return true;
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

    public TokenExpirationHandler getTokenExpirationHandler() {
        if (mTokenExpirationHandler == null) {
            mTokenExpirationHandler = new TokenExpirationHandler();
            App.getComponent().inject(mTokenExpirationHandler);
        }
        return mTokenExpirationHandler;
    }

    public class TokenExpirationHandler {
        @Inject
        BlackBoxInteractor mBlackBoxInteractor;
        @Inject
        BlackBoxStateChecker mChecker;
        @Inject
        ServiceApi mServiceApi;
        @Inject
        com.bsmwireless.data.storage.AccountManager mAccountManager;

        public boolean onTokenExpired(Context context, String accountType, String name) {
            Timber.d("onTokenExpired: accType: %s, name: %s", accountType, name);
            final BlackBoxModel lastModel = mBlackBoxInteractor.getLastData();
            boolean retVal = false;
            if (mChecker.isMoving(lastModel)) {
                Timber.d("onTokenExpired: moving");
                mServiceApi.refreshToken()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(auth -> Observable.fromCallable(() -> refreshToken(accountType, name, auth)))
                        .subscribe();
                retVal = true;
            } else if (mAccountManager.getCurrentUserAccountName().equals(accountType)) {
                Timber.d("onTokenExpired: logout");
                Intent dialogIntent = new Intent(context, AutoDutyDialogActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.putExtra(EXTRA_TOKEN_EXPIRED, true);
                context.startActivity(dialogIntent);
                retVal = true;
            }
            Timber.d("onTokenExpired: getCurrentUserAccountName: %s", mAccountManager.getCurrentUserAccountName());
            return retVal;
        }
    }
}
