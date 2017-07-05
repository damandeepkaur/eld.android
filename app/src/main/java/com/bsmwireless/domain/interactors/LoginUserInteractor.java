package com.bsmwireless.domain.interactors;

import android.os.Build;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.LoginData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import app.bsmuniversal.com.BuildConfig;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

import static com.bsmwireless.common.Constants.DEVICE_TYPE;

public class LoginUserInteractor {

    @Inject
    ServiceApi mServiceApi;

    @Inject
    @Named(Constants.IO_THREAD)
    Scheduler mIoThread;

    @Inject
    AppDatabase mAppDatabase;

    @Inject
    HttpClientManager mClientManager;

    @Inject
    TokenManager mTokenManager;

    @Inject
    PreferencesManager mPreferencesManager;

    public LoginUserInteractor() {
        App.getComponent().inject(this);
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

    public Observable<List<ELDDriverStatus>> loginPair() {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.loginPairVehicle(boxId).subscribeOn(mIoThread);
        }
    }

    public String getUserName() {
        return mTokenManager.getName(mPreferencesManager.getAccountName());
    }

    public String getDomainName() {
        return mTokenManager.getDomain(mPreferencesManager.getAccountName());
    }
}
