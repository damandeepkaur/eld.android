package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.carriers.CarrierDao;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalDao;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.Auth;
import com.bsmwireless.models.Carrier;
import com.bsmwireless.models.DriverHomeTerminal;
import com.bsmwireless.models.DriverProfileModel;
import com.bsmwireless.models.DriverSignature;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.PasswordModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.RuleSelectionModel;
import com.bsmwireless.models.User;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserInteractor
 */

@RunWith(MockitoJUnitRunner.class)
public class UserInteractorTest {

    // defaults for testing //

    private final String mName = "name";
    private final String mPassword = "password";
    private final String mDomain = "domain";
    private final boolean mKeepToken = false;
    private final User.DriverType mDriverType = User.DriverType.DRIVER;
    private final String mSuccessResponse = "ACK";
    private final String mShortValidSignature = "1,2;2,3;3,4;5,-1;";

    /**
     * Matches an ELD event that has a valid logout event type and valid logout event code.
     *<p>
     *     Checks event code and event type against ELD 7.20 and 7.25.
     *</p>
     */
    private final ArgumentMatcher<ELDEvent> mEldEventLogoutCodeMatcher = new ArgumentMatcher<ELDEvent>(){

        @Override
        public boolean matches(Object argument) {
            return ((ELDEvent) argument).getEventType() == 5 // ELD 7.20, Table 6 (5 = login/logout) & ELD 7.25, Table 9
                    && ((ELDEvent) argument).getEventCode() == 2; // ELD 7.20, Table 6 (2 = Authenticated driver's ELD logout activity)
        }
    };

    /**
     * Matches an ELD event that has an active event record status.
     * <p>
     *     Checks event record status against ELD 7.23.
     * </p>
     */
    private final ArgumentMatcher<ELDEvent> mEldEventActiveStatusCodeMatcher = new ArgumentMatcher<ELDEvent>() {
        @Override
        public boolean matches(Object argument) {
            ELDEvent arg = (ELDEvent) argument;
            return arg.getStatus() == 1; // ELD 7.23 (1 = active)
        }
    };

    /**
     * Matches an ELD event that has an edited or entered by the Driver origin.
     * <p>
     *     Checks event record status against ELD 7.22.
     * </p>
     */
    private final ArgumentMatcher<ELDEvent> mEldEventDriverEditOriginCodeMatcher = new ArgumentMatcher<ELDEvent>() {
        @Override
        public boolean matches(Object argument) {
            ELDEvent arg = (ELDEvent) argument;
            return arg.getOrigin() == 2; // ELD 7.22 (2 = edited or entered by the driver)
        }
    };


    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    ServiceApi mServiceApi;

    @Mock
    AppDatabase mAppDatabase;

    @Mock
    TokenManager mTokenManager;

    @Mock
    PreferencesManager mPreferencesManager;

    @Mock
    Auth mAuth;

    @Mock
    BlackBoxInteractor mBlackBoxInteractor;

    @Mock
    UserDao mUserDao;

    @Mock
    CarrierDao mCarrierDao;

    @Mock
    HomeTerminalDao mHomeTerminalDao;

    @Mock
    ResponseMessage mResponseMessage;

    @Mock
    AccountManager mAccountManager;


    private UserInteractor mLoginUserInteractor;




    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);


        mLoginUserInteractor = new UserInteractor(mServiceApi, mPreferencesManager, mAppDatabase, mTokenManager, mAccountManager);
    }

    /**
     * Verify call to ServiceApi layer.
     */
    @Test
    public void testLoginUserApiCall() {
        // given
        User user = makeFakeUser();
        when(mServiceApi.loginUser(any(LoginModel.class))).thenReturn(Observable.just(user));

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType);

        // then
        verify(mServiceApi).loginUser(any(LoginModel.class));

    }

    @Test
    public void testLoginUserSuccess() {
        // given
        User user = makeFakeUser();
        TestObserver<Boolean> testObserver = TestObserver.create();
        String fakeAccountName = "fake account name";

        when(mServiceApi.loginUser((any(LoginModel.class)))).thenReturn(Observable.just(user));
        when(mTokenManager.getAccountName(anyString(), anyString())).thenReturn(fakeAccountName);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType)
                .subscribe(testObserver);

        // then
        verify(mPreferencesManager).setRememberUserEnabled(eq(mKeepToken));
        verify(mPreferencesManager).setShowHomeScreenEnabled(eq(true));

        verify(mAccountManager).setCurrentDriver(anyInt(), anyString());
        verify(mAccountManager).setCurrentUser(anyInt(), anyString());

        verify(mTokenManager).setToken(anyString(), eq(mName), eq(mDomain), any(Auth.class));

        verify(mUserDao).insertUser(any(UserEntity.class));
    }

    // TODO: test login-failed, if possible (right now code will result in NullPointerException if the observable from mServiceApi.loginUser somehow emits a null)

    /**
     * Verifies login actions when carrier list is not null
     */
    @Test
    public void testLoginUserCarriers() {
        // given
        User user = makeFakeUser();
        user.setId(123456);

        TestObserver<Boolean> testObserver = TestObserver.create();
        String fakeAccountName = "fake account name";

        when(mServiceApi.loginUser((any(LoginModel.class)))).thenReturn(Observable.just(user));
        when(mTokenManager.getAccountName(anyString(), anyString())).thenReturn(fakeAccountName);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        when(mAppDatabase.carrierDao()).thenReturn(mCarrierDao);

        List<Carrier> listOfCarriers = new ArrayList<>();

        Carrier carrier1 = new Carrier();
        carrier1.setName("carrier 1");
        listOfCarriers.add(carrier1);

        Carrier carrier2 = new Carrier();
        carrier2.setName("carrier 2");
        listOfCarriers.add(carrier2);

        user.setCarriers(listOfCarriers);

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType)
                .subscribe(testObserver);

        // then
        verify(mCarrierDao).insertCarriers(any(List.class));

    }

    /**
     * Verifies login actions when home terminals list is not null
     */
    @Test
    public void testLoginUserHomeTerminals() {
        // given
        User user = makeFakeUser();
        user.setId(123456);

        List<HomeTerminal> homeTerminals = new ArrayList<>();

        HomeTerminal ht1 = new HomeTerminal();
        HomeTerminal ht2 = new HomeTerminal();
        HomeTerminal ht3 = new HomeTerminal();

        homeTerminals.add(ht1);
        homeTerminals.add(ht2);
        homeTerminals.add(ht3);

        user.setHomeTerminals(homeTerminals);

        TestObserver<Boolean> testObserver = TestObserver.create();
        String fakeAccountName = "fake account name";

        when(mServiceApi.loginUser((any(LoginModel.class)))).thenReturn(Observable.just(user));
        when(mTokenManager.getAccountName(anyString(), anyString())).thenReturn(fakeAccountName);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        when(mAppDatabase.homeTerminalDao()).thenReturn(mHomeTerminalDao);

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType)
                .subscribe(testObserver);

        // then
        verify(mHomeTerminalDao).insertHomeTerminals(any(List.class));
    }

    /**
     * Verifies login actions when last-vehicles list is not null
     *
     * TODO: check that this behavior is intended, as we fetch the last vehicles from the db only to then persist it again, it seems?
     */
    @Test
    public void testLoginUserLastVehicles() {
        // given
        User user = makeFakeUser();
        user.setId(123456);

        TestObserver<Boolean> testObserver = TestObserver.create();
        String fakeAccountName = "fake account name";

        // TODO: test string format, and get from utility function when it is written + delete these comments
        String lastVehicles = "101,102,105"; // <-- format is currently coded in VehiclesInteractor#saveLastVehicles, and possibly needs to be moved + enforced/tested

        when(mServiceApi.loginUser((any(LoginModel.class)))).thenReturn(Observable.just(user));
        when(mTokenManager.getAccountName(anyString(), anyString())).thenReturn(fakeAccountName);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        when(mUserDao.getUserLastVehiclesSync(any(Integer.class))).thenReturn(lastVehicles);

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType)
                .subscribe(testObserver);

        // then
        verify(mUserDao).setUserLastVehicles(any(Integer.class), eq(lastVehicles));
    }

    /**
     * Verify PreferencesManager and TokenManager workflow when login.
     */
    @Test
    public void testLoginUser() {
        // given
        User user = makeFakeUser();

        TestObserver<Boolean> testObserver = new TestObserver<>();
        when(mServiceApi.loginUser(any(LoginModel.class))).thenReturn(Observable.just(user));

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType)
                .subscribe(testObserver);

        // then
        verify(mPreferencesManager).setRememberUserEnabled(eq(mKeepToken));
        verify(mPreferencesManager).setShowHomeScreenEnabled(eq(true));

        verify(mAccountManager).setCurrentDriver(anyInt(), anyString());
        verify(mAccountManager).setCurrentUser(anyInt(), anyString());

        verify(mTokenManager).setToken(anyString(), eq(mName), eq(mDomain), any(Auth.class));
    }


    @Test
    public void testDeleteUserSuccessNoRemember() {
        // given
        final String accountName = "mock account name";
        final String driver = "90210"; // parsable to int
        final int driverInt = 90210; // int version of driver

        when(mPreferencesManager.getDriverAccountName()).thenReturn(accountName);
        when(mTokenManager.getDriver(anyString())).thenReturn(driver);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        when(mPreferencesManager.isRememberUserEnabled()).thenReturn(false);
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.just(mResponseMessage));
        when(mResponseMessage.getMessage()).thenReturn(mSuccessResponse);

        when(mAccountManager.getCurrentDriverAccountName()).thenReturn(accountName);

        // when
        mLoginUserInteractor.deleteDriver();

        // then
        verify(mUserDao).deleteUser(eq(driverInt));
        verify(mTokenManager).removeAccount(eq(accountName));
        verify(mPreferencesManager).clearValues();
    }

    @Test
    public void testDeleteUserSuccessRemember() {
        // given
        final String accountName = "mock account name";
        final String driver = "90210"; // parsable to int
        final String fakeToken = "314159265";

        when(mPreferencesManager.getDriverAccountName()).thenReturn(accountName);
        when(mTokenManager.getDriver(anyString())).thenReturn(driver);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mTokenManager.getToken(anyString())).thenReturn(fakeToken);

        when(mPreferencesManager.isRememberUserEnabled()).thenReturn(true);
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.just(mResponseMessage));
        when(mResponseMessage.getMessage()).thenReturn(mSuccessResponse);

        // when
        mLoginUserInteractor.deleteDriver();

        // then
        verify(mTokenManager).clearToken(eq(fakeToken));
    }

    @Test
    public void testSyncDriverProfileInvalidUserId() {
        // given
        UserEntity user1 = new UserEntity();
        user1.setId(-1); // negative

        UserEntity user2 = new UserEntity();
        user2.setId(0); // boundary

        TestObserver<Boolean> testObserver1 = TestObserver.create();
        TestObserver<Boolean> testObserver2 = TestObserver.create();

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.insertUser(any(UserEntity.class))).thenAnswer(new Answer<Long>() {

            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return Long.valueOf(((UserEntity) args[0]).getId());
            }
        });

        // when
        mLoginUserInteractor.syncDriverProfile(user1).subscribe(testObserver1);
        mLoginUserInteractor.syncDriverProfile(user2).subscribe(testObserver2);

        // then
        testObserver1.assertResult(false);
        testObserver2.assertResult(false);
    }

    @Test
    public void testSyncDriverProfileApiFailed() {
        // given
        UserEntity user = new UserEntity();
        user.setId(12345);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(""); // not "ACK" = fail

        TestObserver<Boolean> testObserver = TestObserver.create();
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.insertUser(any(UserEntity.class))).thenAnswer(new Answer<Long>() {

            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return Long.valueOf(((UserEntity) args[0]).getId());
            }
        });

        when(mServiceApi.updateDriverProfile(any(DriverProfileModel.class))).thenReturn(Observable.just(responseMessage));

        // when
        mLoginUserInteractor.syncDriverProfile(user).subscribe(testObserver);

        // then
        testObserver.assertResult(false);
    }

    @Test
    public void testSyncDriverProfileApiError() {
        // given
        UserEntity user = new UserEntity();
        user.setId(12345);

        String fakeErrorMessage = "sorry.";

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(""); // not "ACK" = fail

        TestObserver<Boolean> testObserver = TestObserver.create();
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.insertUser(any(UserEntity.class))).thenAnswer(new Answer<Long>() {

            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return Long.valueOf(((UserEntity) args[0]).getId());
            }
        });

        when(mServiceApi.updateDriverProfile(any(DriverProfileModel.class))).thenReturn(Observable.error(new Exception(fakeErrorMessage)));

        // when
        mLoginUserInteractor.syncDriverProfile(user).subscribe(testObserver);

        // then
        testObserver.assertErrorMessage(fakeErrorMessage);
    }

    @Test
    public void testSyncDriverProfileSuccess() {
        // given
        UserEntity user = new UserEntity();
        user.setId(12345);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("ACK"); // "ACK" = success

        TestObserver<Boolean> testObserver = TestObserver.create();
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.insertUser(any(UserEntity.class))).thenAnswer(new Answer<Long>() {

            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return Long.valueOf(((UserEntity) args[0]).getId());
            }
        });

        when(mServiceApi.updateDriverProfile(any(DriverProfileModel.class))).thenReturn(Observable.just(responseMessage));

        // when
        mLoginUserInteractor.syncDriverProfile(user).subscribe(testObserver);

        // then
        testObserver.assertResult(true);
    }

    @Test
    public void testUpdateDriverPasswordSuccess() {
        // given
        ResponseMessage successResponse = new ResponseMessage();
        successResponse.setMessage("ACK");  // from API

        String passOld = "oldPassword";
        String passNew = "newPassword";

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.updateDriverPassword(any(PasswordModel.class))).thenReturn(Observable.just(successResponse));

        // when
        mLoginUserInteractor.updateDriverPassword(passOld, passNew)
                .subscribe(testObserver);

        // then
        verify(mServiceApi).updateDriverPassword(any(PasswordModel.class));
        testObserver.assertResult(true);
    }

    @Test
    public void testUpdateDriverPasswordFailed() {
        // given
        ResponseMessage notSuccessResponse = new ResponseMessage();
        notSuccessResponse.setMessage("");  // not "ACK"

        String passOld = "oldPassword";
        String passNew = "newPassword";

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.updateDriverPassword(any(PasswordModel.class))).thenReturn(Observable.just(notSuccessResponse));

        // when
        mLoginUserInteractor.updateDriverPassword(passOld, passNew)
                .subscribe(testObserver);

        // then
        verify(mServiceApi).updateDriverPassword(any(PasswordModel.class));
        testObserver.assertResult(false);
    }

    /**
     * Test error propagation from ServiceApi
     */
    @Test
    public void testUpdateDriverPasswordError() {
        // given
        ResponseMessage notSuccessResponse = new ResponseMessage();
        notSuccessResponse.setMessage("");  // not "ACK"

        Exception fakeError = new Exception("nope.");

        String passOld = "oldPassword";
        String passNew = "newPassword";

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.updateDriverPassword(any(PasswordModel.class)))
                .thenReturn(Observable.error(fakeError));

        // when
        mLoginUserInteractor.updateDriverPassword(passOld, passNew)
                .subscribe(testObserver);

        // then
        verify(mServiceApi).updateDriverPassword(any(PasswordModel.class));
        testObserver.assertError(fakeError);
    }

    @Test
    public void testUpdateDriverSignatureSuccess() {
        // given
        ResponseMessage successMessage = new ResponseMessage();
        successMessage.setMessage(mSuccessResponse);

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverSignature(any(DriverSignature.class))).thenReturn(Observable.just(successMessage));

        // when
        mLoginUserInteractor.updateDriverSignature(mShortValidSignature).subscribe(testObserver);

        // then
        testObserver.assertResult(true);
    }

    @Test
    public void testUpdateDriverSignatureApiFail() {
        // given
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("");

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverSignature(any(DriverSignature.class))).thenReturn(Observable.just(responseMessage));

        // when
        mLoginUserInteractor.updateDriverSignature(mShortValidSignature).subscribe(testObserver);

        // then
        testObserver.assertResult(false);
    }

    /** Tests for propagation of error message from API. */
    @Test
    public void testUpdateDriverSignatureApiError() {
        // given
        TestObserver<Boolean> testObserver = new TestObserver<>();
        String errorMessage = "failed";

        when(mServiceApi.updateDriverSignature(any(DriverSignature.class))).thenReturn(Observable.error(new Exception(errorMessage)));

        // when
        mLoginUserInteractor.updateDriverSignature(mShortValidSignature).subscribe(testObserver);

        // then
        testObserver.assertErrorMessage(errorMessage);
    }

    @Test
    public void testUpdateDriverRuleSuccess() {
        // given
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(mSuccessResponse);

        String fakeRule = "fake rule";

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverRule(any(RuleSelectionModel.class))).thenReturn(Observable.just(responseMessage));

        // when
        mLoginUserInteractor.updateDriverRule(fakeRule).subscribe(testObserver);

        // then
        testObserver.assertResult(true);
    }

    @Test
    public void testUpdateDriverRuleApiFail() {
        // given
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("");

        String fakeRule = "fake rule";

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverRule(any(RuleSelectionModel.class))).thenReturn(Observable.just(responseMessage));

        // when
        mLoginUserInteractor.updateDriverRule(fakeRule).subscribe(testObserver);

        // then
        testObserver.assertResult(false);
    }

    /** Tests for propagation of error message from API. */
    @Test
    public void testUpdateDriverRuleApiError() {
        // given
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("");

        String fakeRule = "fake rule";
        String fakeError = "not this time.";

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverRule(any(RuleSelectionModel.class))).thenReturn(Observable.error(new Exception(fakeError)));

        // when
        mLoginUserInteractor.updateDriverRule(fakeRule).subscribe(testObserver);

        // then
        testObserver.assertErrorMessage(fakeError);
    }

    @Test
    public void testUpdateHomeTerminalSuccess() {
        // given
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(mSuccessResponse);

        Integer fakeTerminalId = 31415926;

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverHomeTerminal(any(DriverHomeTerminal.class))).thenReturn(Observable.just(responseMessage));

        // when
        mLoginUserInteractor.updateDriverHomeTerminal(fakeTerminalId).subscribe(testObserver);

        // then
        testObserver.assertResult(true);
    }

    @Test
    public void testUpdateDriverHomeTerminalApiFail() {
        // given
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("");

        Integer fakeTerminalId = 31415926;

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverHomeTerminal(any(DriverHomeTerminal.class))).thenReturn(Observable.just(responseMessage));

        // when
        mLoginUserInteractor.updateDriverHomeTerminal(fakeTerminalId).subscribe(testObserver);

        // then
        testObserver.assertResult(false);
    }

    /** Tests for propagation of error message from API. */
    @Test
    public void testUpdateDriverHomeTerminalApiError() {
        // given
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("");

        Integer fakeTerminalId = 31415926;
        String fakeErrorMessage = "fake error";

        TestObserver<Boolean> testObserver = new TestObserver<>();

        when(mServiceApi.updateDriverHomeTerminal(any(DriverHomeTerminal.class))).thenReturn(Observable.error(new Exception(fakeErrorMessage)));

        // when
        mLoginUserInteractor.updateDriverHomeTerminal(fakeTerminalId).subscribe(testObserver);

        // then
        testObserver.assertErrorMessage(fakeErrorMessage);
    }

    /**
     * Verify PreferencesManager and TokenManager workflow in getter.
     */
    @Test
    public void testGetUserName() {
        // given
        // n/a

        // when
        mLoginUserInteractor.getUserName();

        // then
        verify(mTokenManager).getName(anyString());
    }

    @Test
    public void testGetFullName() {
        // given
        UserEntity user1 = new UserEntity();
        user1.setFirstName("First");
        user1.setLastName("Last");
        user1.setMidName("Middle");

        String expected1 = "First Last"; // TODO: verify with PO if ignore middle name, or include it?

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUser(any(Integer.class))).thenReturn(Flowable.just(user1));

        TestSubscriber<String> testSubscriber = TestSubscriber.create();

        // when
        mLoginUserInteractor.getFullDriverName().subscribe(testSubscriber);

        // then
        testSubscriber.assertResult(expected1);
    }


    // TODO: test co-drivers number once getCoDriversNumber is implemented


    /**
     * Verify PreferencesManager and TokenManager workflow in getter.
     */
    @Test
    public void testGetDomainName() {
        // given
        // n/a

        // when
        mLoginUserInteractor.getDriverDomainName();

        // then
        verify(mTokenManager).getDomain(anyString());
    }

    @Test
    public void testGetUser() {
        // given
        mockGetDriverId();

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUser(any(Integer.class))).thenReturn(Flowable.just(new UserEntity()));

        TestSubscriber<UserEntity> testSubscriber = TestSubscriber.create();

        // when
        mLoginUserInteractor.getUser().subscribe(testSubscriber);

        // then
        verify(mUserDao).getUser(any(Integer.class));
        verify(mAppDatabase).userDao();
    }

    @Test
    public void testGetFullUser() {
        // given
        mockGetDriverId();

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getFullUser(any(Integer.class))).thenReturn(Flowable.just(new FullUserEntity()));

        TestSubscriber<FullUserEntity> testSubscriber = TestSubscriber.create();

        // when
        mLoginUserInteractor.getFullDriver().subscribe(testSubscriber);

        // then
        verify(mUserDao).getFullUser(any(Integer.class));
        verify(mAppDatabase).userDao();
    }

    // TODO: add tests for isLoginActive
    // seems better to test as instrumented

    // TODO: add tests for getDriverId
    // seems better to test as instrumented

    @Test
    public void testGetTimezoneSync() {
        // given
        int fakeDriverId = 123456;

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserTimezoneSync(any(Integer.class))).thenReturn("fake timezone");

        // when
        mLoginUserInteractor.getTimezoneSync(fakeDriverId);

        // then
        verify(mUserDao).getUserTimezoneSync(any(Integer.class));
        verify(mAppDatabase).userDao();
    }

    @Test
    public void testGetTimezone() {
        // given
        mockGetDriverId();

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserTimezone(any(Integer.class))).thenReturn(Flowable.just("fake timezone"));

        // when
        mLoginUserInteractor.getTimezone();

        // then
        verify(mUserDao).getUserTimezone(any(Integer.class));
        verify(mAppDatabase).userDao();
    }

    // verifying call to preferences manager
    // note: seems better to test as instrumented
    @Test
    public void testIsRememberMeEnabled() {
        // given
        // n/a

        // when
        mLoginUserInteractor.isRememberMeEnabled();

        // then
        verify(mPreferencesManager).isRememberUserEnabled();
    }

    /**
     * Make a fake User for testing purposes.
     *
     * @return a fake User
     */
    private User makeFakeUser() {
        Auth auth = mAuth;

        User user = new User();
        user.setId(1991);

        user.setAuth(auth);


        return user;
    }

    /**
     * Mocks LoginUserInteractor#getDriverId.
     *
     * Used in tests to prevent exceptions, and only when return value does not matter.
     */
    private void mockGetDriverId() {
        when(mPreferencesManager.getDriverAccountName()).thenReturn("fake account");
        when(mTokenManager.getDriver(anyString())).thenReturn("12222"); // fake driver id
    }

}
