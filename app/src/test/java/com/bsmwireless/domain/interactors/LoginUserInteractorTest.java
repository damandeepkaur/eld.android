package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.Auth;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LoginModel;
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

    private User mFakeUser;

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

    LoginUserInteractor mLoginUserInteractor;


    /**
     * Make a fake User for testing purposes.
     *
     * @return a fake User
     */
    private User makeFakeUser() {
        Auth auth = mAuth;

        User user = new User();
        user.setAuth(mAuth);

        return user;
    }

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);


        mLoginUserInteractor = new LoginUserInteractor(mServiceApi, mPreferencesManager, mAppDatabase, mTokenManager);
    }

    /** Verify call to ServiceApi layer. */
    @Test
    public void testLoginUserCallToServiceApi() {
        // given
        when(mServiceApi.loginUser(any(LoginModel.class))).thenReturn(Observable.just(makeFakeUser()));

        // when
        mLoginUserInteractor.loginUser(mName, mPassword, mDomain, mKeepToken, mDriverType);

        // then
        verify(mServiceApi).loginUser(any(LoginModel.class));
    }

    /** Verify call to ServiceApi layer. */
    @Test
    public void testLogoutCallToServiceApi() {
        // given
        ELDEvent event = new ELDEvent();

        // when
        mLoginUserInteractor.logoutUser(event);

        // then
        verify(mServiceApi).logout(event);
    }

    /** Verify call to ServiceApi layer. */
    @Test
    public void testUpdateUserCallToServiceApi() {
        // given
        User user = makeFakeUser();

        // when
        mLoginUserInteractor.updateUser(user);

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
        String name = mName;
        String password = mPassword;
        String domain = mDomain;
        boolean keepToken = mKeepToken;
        User.DriverType driverType = mDriverType;
        User user = makeFakeUser();

        TestObserver<Boolean> testObserver = new TestObserver<>();
        when(mServiceApi.loginUser(any(LoginModel.class))).thenReturn(Observable.just(user));

        // when
        mLoginUserInteractor.loginUser(name, password, domain, keepToken, driverType)
                .subscribe(testObserver);

        // then
        verify(mPreferencesManager).setAccountName(anyString());
        verify(mTokenManager).setToken(anyString(), eq(name), eq(domain), any(Auth.class));
    }


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



}
