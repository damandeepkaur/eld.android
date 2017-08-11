package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.carriers.CarrierDao;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalDao;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.Auth;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.Carrier;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.PasswordModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.User;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LoginUserInteractor
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginUserInteractorTest {

    // defaults for testing //

    private final String mName = "name";
    private final String mPassword = "password";
    private final String mDomain = "domain";
    private final boolean mKeepToken = false;
    private final User.DriverType mDriverType = User.DriverType.DRIVER;

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


    private LoginUserInteractor mLoginUserInteractor;




    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);


        mLoginUserInteractor = new LoginUserInteractor(mServiceApi, mPreferencesManager, mAppDatabase, mTokenManager, mBlackBoxInteractor);
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
        verify(mPreferencesManager).setAccountName(anyString());
        verify(mPreferencesManager).setRememberUserEnabled(eq(mKeepToken));
        verify(mPreferencesManager).setShowHomeScreenEnabled(any(Boolean.class));
        verify(mUserDao).insertUser(any(UserEntity.class));
    }

    // TODO: test login failed, if possible (right now code will result in NullPointerException if the observable from mServiceApi.loginUser somehow emits a null)

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
        mLoginUserInteractor.getFullName().subscribe(testSubscriber);

        // then
        testSubscriber.assertResult(expected1);
    }


    // TODO: test co-drivers number once getCoDriversNumber is implemented


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
        verify(mPreferencesManager).setAccountName(anyString());

        verify(mPreferencesManager).setRememberUserEnabled(eq(mKeepToken));
        verify(mTokenManager).setToken(anyString(), eq(mName), eq(mDomain), any(Auth.class));
    }

    // TODO: add tests for logoutUser


    @Test
    public void testLogoutUserSuccessNoRemember() {
        // given
        final String accountName = "mock account name";
        final String driver = "90210"; // parsable to int
        final int driverInt = 90210; // int version of driver
        final String successResponse = "ACK";

        TestObserver<Boolean> isLogoutTestObserver = new TestObserver<>();
        BlackBoxModel blackBoxModel = new BlackBoxModel();

        when(mPreferencesManager.getAccountName()).thenReturn(accountName);
        when(mTokenManager.getDriver(anyString())).thenReturn(driver);
        when(mBlackBoxInteractor.getData()).thenReturn(Observable.just(blackBoxModel));
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        when(mPreferencesManager.isRememberUserEnabled()).thenReturn(false);
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.just(mResponseMessage));
        when(mResponseMessage.getMessage()).thenReturn(successResponse);

        // when
        Observable<Boolean> isLogout = mLoginUserInteractor.logoutUser();
        isLogout.subscribeWith(isLogoutTestObserver);

        // then
        verify(mServiceApi).logout(any(ELDEvent.class));
        verify(mUserDao).deleteUser(eq(driverInt));
        verify(mTokenManager).removeAccount(eq(accountName));
        verify(mPreferencesManager).clearValues();
        isLogoutTestObserver.assertResult(true);
    }

    @Test
    public void testLogoutUserSuccessRemember() {
        // given
        final String accountName = "mock account name";
        final String driver = "90210"; // parsable to int
        final String successResponse = "ACK";
        final String fakeToken = "314159265";

        TestObserver<Boolean> isLogoutTestObserver = new TestObserver<>();
        BlackBoxModel blackBoxModel = new BlackBoxModel();

        when(mPreferencesManager.getAccountName()).thenReturn(accountName);
        when(mTokenManager.getDriver(anyString())).thenReturn(driver);
        when(mBlackBoxInteractor.getData()).thenReturn(Observable.just(blackBoxModel));
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mTokenManager.getToken(anyString())).thenReturn(fakeToken);

        when(mPreferencesManager.isRememberUserEnabled()).thenReturn(true);
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.just(mResponseMessage));
        when(mResponseMessage.getMessage()).thenReturn(successResponse);

        // when
        Observable<Boolean> isLogout = mLoginUserInteractor.logoutUser();
        isLogout.subscribeWith(isLogoutTestObserver);

        // then
        verify(mServiceApi).logout(any(ELDEvent.class));
        verify(mTokenManager).clearToken(eq(fakeToken));
        isLogoutTestObserver.assertResult(true);
    }

    @Test
    public void testLogoutUserFailure() {

    }

    // TODO: add tests for updateDBUser
    // TODO: add tests for updateUserOnServer
    // TODO: add tests for getUserName
    // TODO: add tests for getFullName
    // TODO: add tests for getCoDriversNumber after implemented


    /**
     * Verify PreferencesManager and TokenManager workflow in getter.
     */
    @Test
    public void testGetDomainName() {
        // given
        // n/a

        // when
        mLoginUserInteractor.getDomainName();

        // then
        verify(mTokenManager).getDomain(anyString());
    }


    // TODO: add tests for getUser
    // TODO: add tests for isLoginActive
    // TODO: add tests for getDriverId
    // TODO: add tests for getTimezone
    // TODO: add tests for isRememberMeEnabled



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

}
