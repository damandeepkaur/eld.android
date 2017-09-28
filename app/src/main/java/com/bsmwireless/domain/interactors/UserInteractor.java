package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.carriers.CarrierConverter;
import com.bsmwireless.data.storage.configurations.ConfigurationConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalConverter;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.Auth;
import com.bsmwireless.models.DriverHomeTerminal;
import com.bsmwireless.models.DriverProfileModel;
import com.bsmwireless.models.DriverSignature;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.PasswordModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.RuleSelectionModel;
import com.bsmwireless.models.User;

import org.apache.commons.lang3.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.bsmwireless.common.Constants.DEFAULT_CALENDAR_DAYS_COUNT;
import static com.bsmwireless.common.Constants.SUCCESS;
import static com.bsmwireless.common.utils.DateUtils.MS_IN_WEEK;

public final class UserInteractor {

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private TokenManager mTokenManager;
    private PreferencesManager mPreferencesManager;
    private AccountManager mAccountManager;
    private SyncInteractor mSyncInteractor;

    @Inject
    public UserInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase,
                          TokenManager tokenManager, AccountManager accountManager, SyncInteractor syncInteractor) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mTokenManager = tokenManager;
        mAccountManager = accountManager;
        mSyncInteractor = syncInteractor;
    }

    public Observable<Boolean> loginUser(final String name, final String password, final String domain, boolean keepToken, User.DriverType driverType) {
        LoginModel request = new LoginModel();
        request.setUsername(name);
        request.setPassword(password);
        request.setDomain(domain);
        request.setDriverType(driverType.ordinal());

        String accountName = mTokenManager.getAccountName(name, domain);
        return mServiceApi.loginUser(request)
                .doOnNext(user -> saveUserDataInDB(user, accountName))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof RetrofitException || throwable instanceof UnknownHostException) {
                        String driverId = mTokenManager.getDriver(accountName);
                        return Observable.fromCallable(() -> mTokenManager.getPassword(accountName))
                                .filter(accountPassword -> !StringUtils.isAnyEmpty(password, accountPassword) && password.equals(accountPassword))
                                .map(filterIn -> mAppDatabase.userDao().getUserSync(Integer.valueOf(driverId)))
                                .map(UserConverter::toUser)
                                .map(user -> user.setAuth(new Auth(Integer.valueOf(driverId)).setToken(mTokenManager.getToken(accountName))));
                    }
                    return Observable.error(throwable);
                })
                .doOnNext(user -> {
                    mTokenManager.setToken(accountName, name, password, domain, user.getAuth());

                    //save user data
                    mPreferencesManager.setRememberUserEnabled(keepToken);
                    mPreferencesManager.setShowHomeScreenEnabled(true);

                    mAccountManager.setCurrentDriver(user.getAuth().getDriverId(), accountName);
                    mAccountManager.setCurrentUser(user.getAuth().getDriverId(), accountName);

                    mSyncInteractor.syncEventsForDaysAgo(DEFAULT_CALENDAR_DAYS_COUNT, user.getTimezone());
                    mSyncInteractor.syncLogSheetHeadersForDaysAgo(DEFAULT_CALENDAR_DAYS_COUNT, user.getTimezone());
                })
                .switchMap(user -> Observable.just(true));
    }

    public Completable loginCoDriver(final String name, final String password, User.DriverType driverType) {
        String domain = getDriverDomainName();

        LoginModel request = new LoginModel();
        request.setUsername(name);
        request.setPassword(password);
        request.setDomain(domain);
        request.setDriverType(driverType.ordinal());

        String accountName = mTokenManager.getAccountName(name, domain);

        return mServiceApi.loginUser(request)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof RetrofitException || throwable instanceof UnknownHostException) {
                        String driverId = mTokenManager.getDriver(accountName);
                        return Observable.fromCallable(() -> mTokenManager.getPassword(accountName))
                                .filter(accountPassword -> !StringUtils.isAnyEmpty(password, accountPassword) && password.equals(accountPassword))
                                .map(filterIn -> mAppDatabase.userDao().getUserSync(Integer.valueOf(driverId)))
                                .map(UserConverter::toUser)
                                .map(user -> user.setAuth(new Auth(Integer.valueOf(driverId))));
                    }
                    return Observable.error(throwable);
                })
                .doOnNext(user -> {
                    mTokenManager.setToken(accountName, name, password, domain, user.getAuth());
                    saveUserDataInDB(user, accountName);

                    List<Integer> coDriverIds = saveCoDrivers(getDriverId(), Arrays.asList(user.getId()));
                    coDriverIds.add(getDriverId());
                    updateCoDrivers(coDriverIds);
                })
                .flatMap(user -> {
                    // get last 7 days events
                    long current = System.currentTimeMillis();
                    long start = DateUtils.getStartDayTimeInMs(user.getTimezone(), current - MS_IN_WEEK);
                    long end = DateUtils.getEndDayTimeInMs(user.getTimezone(), current);
                    String token = user.getAuth().getToken();
                    int userId = user.getId();
                    return mServiceApi.getELDEvents(start, end, token, String.valueOf(userId));
                })
                .onErrorResumeNext(throwable -> {
                    throwable.printStackTrace();
                    if (throwable instanceof RetrofitException || throwable instanceof UnknownHostException) {
                        return Observable.just(new ArrayList<>());
                    }
                    return Observable.error(throwable);
                })
                .flatMapCompletable(events -> Completable.fromAction(() -> {
                    ELDEventEntity[] entities = ELDEventConverter.toEntityList(events).toArray(new ELDEventEntity[events.size()]);
                    mAppDatabase.ELDEventDao().insertAll(entities);
                }));
    }

    public void deleteDriver() {
        int driverId = getDriverId();
        // Need to remove driver from co-driver's lists
        String coDrivers = mAppDatabase.userDao().getUserCoDriversSync(driverId);
        List<Integer> coDriverIds = ListConverter.toIntegerList(coDrivers);
        for (Integer userId : coDriverIds) {
            removeCoDriver(userId, driverId);
        }

        if (!mPreferencesManager.isRememberUserEnabled()) {
            mAppDatabase.userDao().deleteUser(driverId);
            mTokenManager.removeAccount(mAccountManager.getCurrentDriverAccountName());
            mPreferencesManager.clearValues();
        } else {
            mPreferencesManager.setShowHomeScreenEnabled(false);
            mAppDatabase.userDao().setUserCoDrivers(driverId, null);
        }
    }

    public void deleteCoDriver(UserEntity coDriver) {
        int coDriverId = coDriver.getId();
        // Need to remove driver from co-driver's lists
        String coDrivers = mAppDatabase.userDao().getUserCoDriversSync(coDriverId);
        List<Integer> coDriverIds = ListConverter.toIntegerList(coDrivers);
        for (Integer userId : coDriverIds) {
            removeCoDriver(userId, coDriverId);
        }

        int currentUserId = mAccountManager.getCurrentUserId();
        if (currentUserId == coDriverId) {
            mAccountManager.resetUserToDriver();
        }
    }

    public Observable<Boolean> syncDriverProfile(UserEntity userEntity) {
        return Observable.fromCallable(() -> mAppDatabase.userDao().insertUser(userEntity))
                .map(userId -> userId > 0)
                .flatMap(userInserted -> {
                    if (userInserted) {
                        return mServiceApi.updateDriverProfile(new DriverProfileModel(userEntity));
                    }
                    return Observable.just(new ResponseMessage(""));
                })
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                .onErrorReturn(this::handleOfflineOperation);
    }

    public Observable<Boolean> updateDriverPassword(String oldPassword, String newPassword) {
        return mServiceApi.updateDriverPassword(getPasswordModel(oldPassword, newPassword))
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Observable<Boolean> updateDriverSignature(String signature) {
        return mServiceApi.updateDriverSignature(getDriverSignature(signature))
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                .onErrorReturn(throwable -> false);
    }

    public Observable<Boolean> updateDriverRule(String ruleException, String dutyCycle) {
        return mServiceApi.updateDriverRule(getRuleSelectionModel(ruleException, dutyCycle))
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                .onErrorReturn(throwable -> false);
    }

    public Completable updateUserRuleException(String ruleException) {
        return Completable.fromAction(() -> {
            UserEntity user = mAppDatabase.userDao().getUserSync(mAccountManager.getCurrentUserId());
            user.setRuleException(ruleException);
            mAppDatabase.userDao().insertUser(user);
        });
    }

    public Observable<Boolean> updateDriverHomeTerminal(Integer homeTerminalId) {
        return mServiceApi.updateDriverHomeTerminal(getHomeTerminal(homeTerminalId))
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                .onErrorReturn(throwable -> false);
    }

    private Boolean handleOfflineOperation(Throwable throwable) {
        return throwable instanceof RetrofitException || throwable instanceof UnknownHostException;
    }

    public String getDriverName() {
        return mTokenManager.getName(mAccountManager.getCurrentDriverAccountName());
    }

    public String getUserName() {
        return mTokenManager.getName(mAccountManager.getCurrentUserAccountName());
    }

    public Flowable<String> getFullDriverName() {
        return mAppDatabase.userDao().getUser(getDriverId())
                .map(userEntity -> userEntity.getFirstName() + " " + userEntity.getLastName());
    }

    public String getFullDriverNameSync() {
        UserEntity userEntity = mAppDatabase.userDao().getUserSync(getDriverId());
        return userEntity.getFirstName() + " " + userEntity.getLastName();
    }

    public String getFullUserNameSync() {
        UserEntity userEntity = mAppDatabase.userDao().getUserSync(getUserId());
        return userEntity.getFirstName() + " " + userEntity.getLastName();
    }

    public Flowable<Integer> getCoDriversNumber() {
        return mAppDatabase.userDao()
                .getUserCoDrivers(mAccountManager.getCurrentDriverId())
                .map(ListConverter::toIntegerList)
                .map(List::size);
    }

    public Integer getCoDriversNumberSync() {
        return ListConverter.toIntegerList(mAppDatabase.userDao().getUserCoDriversSync(mAccountManager.getCurrentDriverId())).size();
    }

    public String getDriverDomainName() {
        return mTokenManager.getDomain(mAccountManager.getCurrentDriverAccountName());
    }

    public Flowable<UserEntity> getDriver() {
        return mAppDatabase.userDao().getUser(getDriverId());
    }

    public Flowable<User> getUser() {
        return mAppDatabase.userDao().getUser(getDriverId())
                .map(UserConverter::toUser);
    }


    public Flowable<FullUserEntity> getFullDriver() {
        return mAppDatabase.userDao()
                .getFullUser(getDriverId())
                .doOnNext(userEntity -> userEntity.setHomeTerminalEntities(
                        mAppDatabase.homeTerminalDao()
                                .getHomeTerminalsSync(userEntity.getHomeTerminalIds())
                ));
    }

    public FullUserEntity getFullUserSync() {
        FullUserEntity fullUserEntity = mAppDatabase.userDao().getFullUserSync(getUserId());
        fullUserEntity.setHomeTerminalEntities(mAppDatabase
                .homeTerminalDao()
                .getHomeTerminalsSync(fullUserEntity.getHomeTerminalIds()));
        return fullUserEntity;
    }

    public FullUserEntity getFullUserSync(int id) {
        return mAppDatabase.userDao().getFullUserSync(id);
    }

    public Single<User> getFullUser() {
        return Single.fromCallable(() -> mAppDatabase.userDao().getFullUserSync(mAccountManager.getCurrentUserId()))
                .doOnSuccess(userEntity -> userEntity.setHomeTerminalEntities(
                        mAppDatabase.homeTerminalDao()
                                .getHomeTerminalsSync(userEntity.getHomeTerminalIds())
                ))
                .map(UserConverter::toFullUser);
    }

    public boolean isLoginActive() {
        return mPreferencesManager.isShowHomeScreenEnabled() && mTokenManager.getToken(mAccountManager.getCurrentDriverAccountName()) != null;
    }

    public int getDriverId() {
        String id = mTokenManager.getDriver(mAccountManager.getCurrentDriverAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public int getUserId() {
        String id = mTokenManager.getDriver(mAccountManager.getCurrentUserAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public String getTimezoneSync(int driverId) {
        return mAppDatabase.userDao().getUserTimezoneSync(driverId);
    }

    public Flowable<String> getTimezone() {
        return mAppDatabase.userDao().getUserTimezone(getDriverId());
    }

    public Single<String> getTimezoneOnce() {
        return mAppDatabase.userDao().getUserTimezoneOnce(getDriverId());
    }

    public boolean isRememberMeEnabled() {
        return mPreferencesManager.isRememberUserEnabled();
    }

    public Flowable<List<UserEntity>> getCoDriversFromDB() {
        return mAppDatabase.userDao()
                .getUserCoDrivers(getDriverId())
                .flatMap(coDriversIds -> mAppDatabase.userDao().getDrivers(ListConverter.toIntegerList(coDriversIds)));
    }

    public boolean isUserDriver() {
        return mAccountManager.isCurrentUserDriver();
    }

    public UserEntity getUserFromDBSync(int userId) {
        return mAppDatabase.userDao().getUserSync(userId);
    }

    public List<UserEntity> getUsersFromDBSync(String ids) {
        return mAppDatabase.userDao().getUsersSync(ListConverter.toIntegerList(ids));
    }

    private List<Integer> saveCoDrivers(int driverId, List<Integer> coDriverIds) {
        String coDrivers = mAppDatabase.userDao().getUserCoDriversSync(driverId);
        List<Integer> savedCoDrivers = ListConverter.toIntegerList(coDrivers);

        for (Integer coDriverId : coDriverIds) {
            if (!savedCoDrivers.contains(coDriverId) && !coDriverId.equals(driverId)) {
                savedCoDrivers.add(coDriverId);
            }
        }

        mAppDatabase.userDao().setUserCoDrivers(driverId, ListConverter.integerListToString(savedCoDrivers));

        return savedCoDrivers;
    }

    private void updateCoDrivers(List<Integer> driverIds) {
        for (Integer coDriverId : driverIds) {
            saveCoDrivers(coDriverId, driverIds);
        }
    }

    private void removeCoDriver(int driverId, Integer coDriverId) {
        String coDrivers = mAppDatabase.userDao().getUserCoDriversSync(driverId);
        List<Integer> savedCoDrivers = ListConverter.toIntegerList(coDrivers);

        savedCoDrivers.remove(coDriverId);

        mAppDatabase.userDao().setUserCoDrivers(driverId, ListConverter.integerListToString(savedCoDrivers));
    }

    private PasswordModel getPasswordModel(String oldPassword, String newPassword) {
        PasswordModel passwordModel = new PasswordModel();

        passwordModel.setId(getUserId());
        passwordModel.setUsername(getUserName());
        passwordModel.setPassword(oldPassword);
        passwordModel.setNewPassword(newPassword);

        return passwordModel;
    }

    private DriverHomeTerminal getHomeTerminal(Integer homeTerminalId) {
        DriverHomeTerminal homeTerminal = new DriverHomeTerminal();

        homeTerminal.setDriverId(getUserId());
        homeTerminal.setHomeTermId(homeTerminalId);

        return homeTerminal;
    }

    private RuleSelectionModel getRuleSelectionModel(String ruleException, String dutyCycle) {
        RuleSelectionModel ruleSelectionModel = new RuleSelectionModel();

        ruleSelectionModel.setDriverId(getUserId());
        ruleSelectionModel.setRuleException(ruleException);
        ruleSelectionModel.setDutyCycle(dutyCycle);
        ruleSelectionModel.setApplyTime(Calendar.getInstance().getTimeInMillis());

        return ruleSelectionModel;
    }

    private DriverSignature getDriverSignature(String signature) {
        DriverSignature signatureInfo = new DriverSignature();

        signatureInfo.setDriverId(getUserId());
        signatureInfo.setSignature(signature);

        return signatureInfo;
    }

    private void saveUserDataInDB(User user, String accountName) {
        int userId = user.getId();
        UserDao userDao = mAppDatabase.userDao();
        UserEntity originalUser = userDao.getUserSync(user.getId());
        if (originalUser != null && originalUser.isOfflineChange()) {
            return;
        }

        String lastVehicles = userDao.getUserLastVehiclesSync(userId);
        UserEntity userEntity = UserConverter.toEntity(user);
        userEntity.setAccountName(accountName);
        userDao.insertUser(userEntity);

        if (user.getCarriers() != null) {
            mAppDatabase.carrierDao().deleteByUserId(userId);
            mAppDatabase.carrierDao().insertCarriers(CarrierConverter
                    .toEntityList(user.getCarriers(), userId));
        }

        List<HomeTerminal> homeTerminals = user.getHomeTerminals();
        if (homeTerminals != null) {
            mAppDatabase.userHomeTerminalDao().deleteUserHomeTerminal(userId);
            mAppDatabase.homeTerminalDao().insertHomeTerminals(HomeTerminalConverter
                    .toEntityList(homeTerminals));
            mAppDatabase.userHomeTerminalDao().insertUserHomeTerminal(HomeTerminalConverter
                    .toUserRelation(homeTerminals, userId));
        }

        if (user.getConfigurations() != null) {
            mAppDatabase.configurationDao().deleteByUserId(userId);
            mAppDatabase.configurationDao().insertAll(ConfigurationConverter
                    .toEntityList(user.getConfigurations(), userId));
        }

        if (lastVehicles != null) {
            userDao.setUserLastVehicles(userId, lastVehicles);
        }
    }

    public boolean isSelectAssetScreenActive() {
        return mPreferencesManager.isShowSelectAssetScreenEnabled();
    }

    public void setShowSelectAssetScreenEnabled(boolean showSelectAssetScreen) {
        mPreferencesManager.setShowSelectAssetScreenEnabled(showSelectAssetScreen);
    }
}

