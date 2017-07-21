package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.User;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.bsmwireless.models.ELDEvent.EventType.LOGIN_LOGOUT;

public class LoginUserInteractor {

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private TokenManager mTokenManager;
    private PreferencesManager mPreferencesManager;
    private BlackBoxInteractor mBlackBoxInteractor;

    @Inject
    public LoginUserInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase,
                               TokenManager tokenManager, BlackBoxInteractor blackBoxInteractor) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mTokenManager = tokenManager;
        mBlackBoxInteractor = blackBoxInteractor;
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

                    String lastVehicles = mAppDatabase.userDao().getUserLastVehiclesSync(user.getId());
                    mAppDatabase.userDao().insertUser(UserConverter.toEntity(user));

                    if (lastVehicles != null) {
                        mAppDatabase.userDao().setUserLastVehicles(user.getId(), lastVehicles);
                    }
                })
                .map(user -> user != null);
    }

    public Observable<Boolean> logoutUser() {
        ELDEvent logoutEvent = new ELDEvent();
        int driverId = getDriverId();
        logoutEvent.setEventType(LOGIN_LOGOUT.getValue());
        logoutEvent.setEventCode(ELDEvent.StatusCode.ACTIVE.getValue());
        logoutEvent.setEventTime(System.currentTimeMillis());
        logoutEvent.setMobileTime(System.currentTimeMillis());
        logoutEvent.setDriverId(getDriverId());
        //TODO: get real data for hos
        logoutEvent.setEngineHours(50);

        return mBlackBoxInteractor.getData()
                .flatMap(blackBox -> {
                    logoutEvent.setTimezone(getTimezone(driverId));
                    logoutEvent.setOdometer(blackBox.getOdometer());
                    logoutEvent.setLat(blackBox.getLat());
                    logoutEvent.setLng(blackBox.getLon());

                    return mServiceApi.logout(logoutEvent)
                            .map(responseMessage -> responseMessage.getMessage().equals("ACK"));
                });
    }

    public Observable<ResponseMessage> updateUser(User user) {
        return mServiceApi.updateProfile(user);
    }

    public String getUserName() {
        return mTokenManager.getName(mPreferencesManager.getAccountName());
    }

    public int getCoDriversNumber() {
        //TODO: implement getting co drivers number
        return 1;
    }

    public String getDomainName() {
        return mTokenManager.getDomain(mPreferencesManager.getAccountName());
    }

    public boolean isLoginActive() {
        return mTokenManager.getToken(mPreferencesManager.getAccountName()) != null;
    }

    public Integer getDriverId() {
        String id = mTokenManager.getDriver(mPreferencesManager.getAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public String getTimezone(int driverId) {
        return mAppDatabase.userDao().getUserTimezoneSync(driverId);
    }
}
