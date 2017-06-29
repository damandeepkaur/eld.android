package com.bsmwireless.domain.interactors;

import android.os.Build;

import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.models.LoginRequest;

import app.bsmuniversal.com.BuildConfig;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

import static com.bsmwireless.common.Constants.DEVICE_TYPE;

public class LoginUserInteractor {

    private ServiceApi mServiceApi;

    private Scheduler mIoThread;

    private AppDatabase mAppDatabase;

    private HttpClientManager mClientManager;

    private TokenManager mTokenManager;

    private PreferencesManager mPreferencesManager;

    public LoginUserInteractor(ServiceApi serviceApi, Scheduler ioThread, AppDatabase appDatabase, HttpClientManager clientManager,
                               TokenManager tokenManager, PreferencesManager preferencesManager) {
        this.mServiceApi = serviceApi;
        this.mIoThread = ioThread;
        this.mAppDatabase = appDatabase;
        this.mClientManager = clientManager;
        this.mTokenManager = tokenManager;
        this.mPreferencesManager = preferencesManager;
    }

    public Observable<Boolean> loginUser(final String name, final String password, final String domain, boolean keepToken) {
        LoginRequest request = new LoginRequest();
        request.setUsername(name);
        request.setPassword(password);
        request.setDomain(domain);
        request.setDriverType(0);
        request.setAppVersion(BuildConfig.VERSION_NAME);
        request.setDeviceType(DEVICE_TYPE);
        request.setOsVersion(Build.VERSION.RELEASE);

        return mServiceApi.loginUser(request)
                .subscribeOn(mIoThread)
                .doOnNext(user -> {
                    mClientManager.setHeaders(String.valueOf(user.getId()),
                            String.valueOf(user.getAuth().getOrgId()),
                            user.getAuth().getCluster(),
                            user.getAuth().getToken());

                    String accountName = mPreferencesManager.setAccountName(name, domain);

                    mAppDatabase.userModel().insertUser(UserConverter.toEntity(accountName, user));

                    if (keepToken) {
                        mTokenManager.setToken(accountName, name, password, String.valueOf(user.getId()), domain, user.getAuth().getToken());
                    }
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
