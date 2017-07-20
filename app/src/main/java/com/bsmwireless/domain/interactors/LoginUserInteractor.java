package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserLastVehiclesEntity;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.User;

import javax.inject.Inject;

import io.reactivex.Observable;

public class LoginUserInteractor {

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private TokenManager mTokenManager;
    private PreferencesManager mPreferencesManager;

    @Inject
    public LoginUserInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase, TokenManager tokenManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mTokenManager = tokenManager;
    }

    public Observable<Boolean> loginUser(final String name, final String password, final String domain, boolean keepToken, User.DriverType driverType) {
        LoginModel request = new LoginModel();
        request.setUsername(name);
        request.setPassword(password);
        request.setDomain(domain);
        request.setDriverType(driverType.ordinal());

        return mServiceApi.loginUser(request)
                .doOnNext(user -> {
                    String accountName = mTokenManager.getAccountName(name, domain);

                    //TODO if !keepToken remove account on exit and clear shared preferences
                    mPreferencesManager.setAccountName(accountName);
                    mPreferencesManager.setRememberUserEnabled(keepToken);

                    mTokenManager.setToken(accountName, name, domain, user.getAuth());

                    UserLastVehiclesEntity lastVehicles = mAppDatabase.userDao().getUserLastVehiclesSync(user.getId());
                    mAppDatabase.userDao().insertUser(UserConverter.toEntity(user));

                    if (lastVehicles != null) {
                        mAppDatabase.userDao().setUserLastVehicles(user.getId(), lastVehicles.getIds());
                    }
                })
                .map(user -> user != null);
    }

    public Observable<ResponseMessage> logoutUser(ELDEvent event) {
        return mServiceApi.logout(event);
    }

    public Observable<ResponseMessage> updateUser(User user) {
        return mServiceApi.updateProfile(user);
    }

    public String getUserName() {
        return mTokenManager.getName(mPreferencesManager.getAccountName());
    }

    public String getDomainName() {
        return mTokenManager.getDomain(mPreferencesManager.getAccountName());
    }

    public Integer getDriverId() {
        String id = mTokenManager.getDriver(mPreferencesManager.getAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public String getTimezone(int driverId) {
        return mAppDatabase.userDao().getUserTimezoneSync(driverId).getTimezone();
    }

    public void clearToken() {
        String account = mPreferencesManager.getAccountName();
        if (mPreferencesManager.isRememberUserEnabled()) {
            mTokenManager.clearToken(mTokenManager.getToken(account));
        } else {
            mTokenManager.removeAccount(account);
        }
    }
}
