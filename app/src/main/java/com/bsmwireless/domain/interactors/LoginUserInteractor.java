package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.PasswordModel;
import com.bsmwireless.models.ResponseMessage;
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

                    if (lastVehicles != null) {
                        mAppDatabase.userDao().setUserLastVehicles(user.getId(), lastVehicles);
                    }
                })
                  .flatMap(user -> {
                      UserEntity userEntity = mAppDatabase.userDao().getUserSync(user.getId());
                      if (userEntity != null) {
                          Long lastModified = userEntity.getLastModified();
                          if (lastModified != null && lastModified > user.getSyncTime()) {
                              return mServiceApi.updateProfile(getUpdatedUser(userEntity))
                                                .map(responseMessage -> responseMessage.getMessage().equals("ACK"));
                          }
                      }
                      return Observable.create((ObservableOnSubscribe<Long>) e -> e.onNext(mAppDatabase.userDao().insertUser(UserConverter.toEntity(user))))
                                       .map(userID -> userID > 0);
                  });
    }

    public Observable<Boolean> logoutUser() {
        ELDEvent logoutEvent = new ELDEvent();
        int driverId = getDriverId();
        logoutEvent.setEventType(LOGIN_LOGOUT.getValue());
        logoutEvent.setEventCode(ELDEvent.StatusCode.ACTIVE.getValue());
        logoutEvent.setEventTime(System.currentTimeMillis());
        logoutEvent.setMobileTime(System.currentTimeMillis());
        logoutEvent.setDriverId(getDriverId());
        logoutEvent.setBoxId(mPreferencesManager.getBoxId());
        logoutEvent.setVehicleId(mPreferencesManager.getVehicleId());

        return mBlackBoxInteractor.getData()
                .flatMap(blackBox -> {
                    logoutEvent.setTimezone(getTimezone(driverId));
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
                            .map(responseMessage -> responseMessage.getMessage().equals("ACK"));
                });
    }

    public Observable<Boolean> updateUser(User user) {
        UserEntity userEntity = UserConverter.toEntity(user);
        return Observable.create((ObservableOnSubscribe<Long>) e -> {
                                    userEntity.setLastModified(Calendar.getInstance().getTimeInMillis());
                                    e.onNext(mAppDatabase.userDao().insertUser(userEntity));
                         })
                         .map(userId -> userId > 0)
                         .flatMap(userInserted -> {
                             if (userInserted) {
                                 return mServiceApi.updateProfile(getUpdatedUser(userEntity));
                             }
                             ResponseMessage responseMessage = new ResponseMessage();
                             responseMessage.setMessage("");
                             return Observable.just(responseMessage);
                         })
                         .map(responseMessage -> responseMessage.getMessage().equals("ACK"));
    }

    public Observable<Boolean> updateDriverPassword(String oldPassword, String newPassword) {
        return mServiceApi.updateDriverPassword(getPasswordModel(oldPassword, newPassword))
                          .map(responseMessage -> responseMessage.getMessage().equals("ACK"));
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

    public boolean isLoginActive() {
        return mPreferencesManager.isShowHomeScreenEnabled() && mTokenManager.getToken(mPreferencesManager.getAccountName()) != null;
    }

    public Integer getDriverId() {
        String id = mTokenManager.getDriver(mPreferencesManager.getAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public String getTimezone(int driverId) {
        return mAppDatabase.userDao().getUserTimezoneSync(driverId);
    }

    public boolean isRememberMeEnabled() {
        return mPreferencesManager.isRememberUserEnabled();
    }

    // TODO: change server logic
    private User getUpdatedUser(UserEntity userEntity) {
        User user = new User();

        user.setId(userEntity.getId());
        user.setTimezone(userEntity.getTimezone());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setCycleCountry(userEntity.getCycleCountry());
        user.setSignature(userEntity.getSignature());
        user.setAddress(userEntity.getAddress());

        return user;
    }

    private PasswordModel getPasswordModel(String oldPassword, String newPassword) {
        PasswordModel passwordModel = new PasswordModel();

        passwordModel.setId(getDriverId());
        passwordModel.setUsername(getUserName());
        passwordModel.setPassword(oldPassword);
        passwordModel.setNewpswd(newPassword);

        return passwordModel;
    }
}

