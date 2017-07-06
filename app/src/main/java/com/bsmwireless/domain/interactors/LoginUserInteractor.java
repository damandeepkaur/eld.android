package com.bsmwireless.domain.interactors;

import android.os.Build;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.models.LoginData;

import app.bsmuniversal.com.BuildConfig;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.bsmwireless.common.Constants.DEVICE_TYPE;

public class LoginUserInteractor {

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private TokenManager mTokenManager;
    private PreferencesManager mPreferencesManager;

    public LoginUserInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase, TokenManager tokenManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mTokenManager = tokenManager;
    }

    public Observable<Boolean> loginUser(final String name, final String password, final String domain, boolean keepToken) {
        LoginData request = new LoginData();
        request.setUsername(name);
        request.setPassword(password);
        request.setDomain(domain);
        request.setDriverType(0);
        request.setAppVersion(BuildConfig.VERSION_NAME);
        request.setDeviceType(DEVICE_TYPE);
        request.setOsVersion(Build.VERSION.RELEASE);

        return mServiceApi.loginUser(request)
                .subscribeOn(Schedulers.io())
                .doOnNext(user -> {
                    String accountName = mTokenManager.getAccountName(name, domain);

                    //TODO if !keepToken remove account on exit and clear shared preferences
                    mPreferencesManager.setAccountName(accountName);
                    mPreferencesManager.setRememberUserEnabled(keepToken);

                    mTokenManager.setToken(accountName, name, user.getAuth());
                    mAppDatabase.userModel().insertUser(UserConverter.toEntity(accountName, user));
                })
                .map(user -> user != null);
    }

    public String getUserName() {
        return mTokenManager.getName(mPreferencesManager.getAccountName());
    }

    public String getDomainName() {
        return mTokenManager.getDomain(mPreferencesManager.getAccountName());
    }
}
