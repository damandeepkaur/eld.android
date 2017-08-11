package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.carriers.CarrierConverter;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalConverter;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.DriverHomeTerminal;
import com.bsmwireless.models.DriverProfileModel;
import com.bsmwireless.models.DriverSignature;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.PasswordModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.RuleSelectionModel;
import com.bsmwireless.models.User;

import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

import static com.bsmwireless.models.ELDEvent.EventType.LOGIN_LOGOUT;

public class LoginUserInteractor {

    private static final String SUCCESS = "ACK";

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

                    mPreferencesManager.setAccountName(accountName);
                    mPreferencesManager.setRememberUserEnabled(keepToken);
                    mPreferencesManager.setShowHomeScreenEnabled(true);

                    mTokenManager.setToken(accountName, name, domain, user.getAuth());

                    String lastVehicles = mAppDatabase.userDao().getUserLastVehiclesSync(user.getId());

                    mAppDatabase.userDao().insertUser(UserConverter.toEntity(user));

                    if (user.getCarriers() != null) {
                        mAppDatabase.carrierDao().insertCarriers(CarrierConverter
                                .toEntityList(user.getCarriers(), user.getId()));
                    }

                    if (user.getHomeTerminals() != null) {
                        mAppDatabase.homeTerminalDao().insertHomeTerminals(HomeTerminalConverter
                                .toEntityList(user.getHomeTerminals(), user.getId()));
                    }

                    if (lastVehicles != null) {
                        mAppDatabase.userDao().setUserLastVehicles(user.getId(), lastVehicles);
                    }
                }).map(user -> user != null);
    }

    public Observable<Boolean> logoutUser() {
        ELDEvent logoutEvent = new ELDEvent();
        int driverId = getDriverId();
        logoutEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        logoutEvent.setOrigin(ELDEvent.EventOrigin.MANUAL_ENTER.getValue());
        logoutEvent.setEventType(LOGIN_LOGOUT.getValue());
        logoutEvent.setEventCode(ELDEvent.LoginLogoutCode.LOGOUT.getValue());
        logoutEvent.setEventTime(System.currentTimeMillis());
        logoutEvent.setMobileTime(System.currentTimeMillis());
        logoutEvent.setDriverId(getDriverId());
        logoutEvent.setBoxId(mPreferencesManager.getBoxId());
        logoutEvent.setVehicleId(mPreferencesManager.getVehicleId());

        return mBlackBoxInteractor.getData()
                .flatMap(blackBox -> {
                    logoutEvent.setTimezone(getTimezoneSync(driverId));
                    logoutEvent.setEngineHours(blackBox.getEngineHours());
                    logoutEvent.setOdometer(blackBox.getOdometer());
                    logoutEvent.setLat(blackBox.getLat());
                    logoutEvent.setLng(blackBox.getLon());

                    return mServiceApi.logout(logoutEvent)
                            .doOnNext(responseMessage -> {
                                if (!mPreferencesManager.isRememberUserEnabled()) {
                                    mAppDatabase.userDao().deleteUser(getDriverId());
                                    mTokenManager.removeAccount(mPreferencesManager.getAccountName());
                                    mPreferencesManager.clearValues();
                                } else {
                                    mTokenManager.clearToken(mTokenManager.getToken(mPreferencesManager.getAccountName()));
                                }
                            })
                            .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
                });
    }

    public Observable<Boolean> syncDriverProfile(User user) {
        UserEntity userEntity = UserConverter.toEntity(user);
        return Observable.fromCallable(() -> mAppDatabase.userDao().insertUser(userEntity))
                         .map(userId -> userId > 0)
                         .flatMap(userInserted -> {
                             if (userInserted) {
                                 return mServiceApi.updateDriverProfile(new DriverProfileModel(userEntity));
                             }
                             return Observable.just(new ResponseMessage(""));
                         })
                         .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Observable<Boolean> updateDriverPassword(String oldPassword, String newPassword) {
        return mServiceApi.updateDriverPassword(getPasswordModel(oldPassword, newPassword))
                          .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Observable<Boolean> updateDriverSignature(String signature) {
        return mServiceApi.updateDriverSignature(getDriverSignature(signature))
                          .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Observable<Boolean> updateDriverRule(String ruleException) {
        return mServiceApi.updateDriverRule(getRuleSelectionModel(ruleException))
                          .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Observable<Boolean> updateDriverHomeTerminal(Integer homeTerminalId) {
        return mServiceApi.updateDriverHomeTerminal(getHomeTerminal(homeTerminalId))
                          .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public String getUserName() {
        return mTokenManager.getName(mPreferencesManager.getAccountName());
    }

    public Flowable<String> getFullName() {
        return mAppDatabase.userDao().getUser(getDriverId())
                .map(userEntity -> userEntity.getFirstName() + " " + userEntity.getLastName());
    }

    public int getCoDriversNumber() {
        //TODO: implement getting co drivers number
        return 1;
    }

    public String getDomainName() {
        return mTokenManager.getDomain(mPreferencesManager.getAccountName());
    }

    public Flowable<UserEntity> getUser() {
        return mAppDatabase.userDao().getUser(getDriverId());
    }

    public Flowable<FullUserEntity> getFullUser() {
        return mAppDatabase.userDao().getFullUser(getDriverId());
    }

    public boolean isLoginActive() {
        return mPreferencesManager.isShowHomeScreenEnabled() && mTokenManager.getToken(mPreferencesManager.getAccountName()) != null;
    }

    public Integer getDriverId() {
        String id = mTokenManager.getDriver(mPreferencesManager.getAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public String getTimezoneSync(int driverId) {
        return mAppDatabase.userDao().getUserTimezoneSync(driverId);
    }

    public Flowable<String> getTimezone() {
        return mAppDatabase.userDao().getUserTimezone(getDriverId());
    }

    public boolean isRememberMeEnabled() {
        return mPreferencesManager.isRememberUserEnabled();
    }

    private PasswordModel getPasswordModel(String oldPassword, String newPassword) {
        PasswordModel passwordModel = new PasswordModel();

        passwordModel.setId(getDriverId());
        passwordModel.setUsername(getUserName());
        passwordModel.setPassword(oldPassword);
        passwordModel.setNewPassword(newPassword);

        return passwordModel;
    }

    private DriverHomeTerminal getHomeTerminal(Integer homeTerminalId) {
        DriverHomeTerminal homeTerminal = new DriverHomeTerminal();

        homeTerminal.setDriverId(getDriverId());
        homeTerminal.setHomeTermId(homeTerminalId);

        return homeTerminal;
    }

    private RuleSelectionModel getRuleSelectionModel(String ruleException) {
        RuleSelectionModel ruleSelectionModel = new RuleSelectionModel();

        ruleSelectionModel.setDriverId(getDriverId());
        ruleSelectionModel.setRuleException(ruleException);
        ruleSelectionModel.setApplyTime(Calendar.getInstance().getTimeInMillis());

        return ruleSelectionModel;
    }

    private DriverSignature getDriverSignature(String signature) {
        DriverSignature signatureInfo = new DriverSignature();

        signatureInfo.setDriverId(getDriverId());
        signatureInfo.setSignature(signature);

        return signatureInfo;
    }
}

