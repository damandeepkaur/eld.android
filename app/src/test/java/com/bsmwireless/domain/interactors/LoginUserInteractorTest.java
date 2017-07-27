package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.models.Auth;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LoginModel;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.User;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

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
    ResponseMessage mResponseMessage;


    private LoginUserInteractor mLoginUserInteractor;



    /**
     * Make a fake User for testing purposes.
     *
     * @return a fake User
     */
    private User makeFakeUser() {
        Auth auth = mAuth;

        User user = new User();

        user.setAuth(auth);


        return user;
    }

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);


        mLoginUserInteractor = new LoginUserInteractor(mServiceApi, mPreferencesManager, mAppDatabase, mTokenManager, mBlackBoxInteractor);
    }

    /**
     * Verify call to ServiceApi layer.
     */
    @Test
    public void testLoginUserCallToServiceApi() {
        // given
        when(mServiceApi.loginUser(any(LoginModel.class))).thenReturn(Observable.just(makeFakeUser()));

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType);

        // then
        verify(mServiceApi).loginUser(any(LoginModel.class));
    }



    // TODO: Verify call to ServiceApi layer of logout - re-implement test when stable



    /**
     * Verify call to ServiceApi layer.
     */
    @Test
    public void testUpdateUserCallToServiceApi() {
        // given
        User user = makeFakeUser();

        // when
        mLoginUserInteractor.updateUserOnServer(user);

        // then
        verify(mServiceApi).updateProfile(user);
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

}
