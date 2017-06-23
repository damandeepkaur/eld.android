package app.bsmuniversal.com.presentation;


import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.screens.login.LoginPresenter;
import com.bsmwireless.screens.login.LoginView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String DOMAIN = "domain";

    @Mock
    LoginView mView;

    @Mock
    LoginUserInteractor mLoginUserInteractor;

    LoginPresenter mLoginPresenter;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        mLoginPresenter = new LoginPresenter(mView, mLoginUserInteractor, Schedulers.trampoline());
    }


    @Test
    public void testTryLoginUserWithNullData() {
        //given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(null);

        //when
        mLoginPresenter.onLoginButtonClicked(false);

        //then
        verify(mView).showErrorMessage(anyString());
    }

    @Test
    public void testTryLoginUserWithEmptyData() {
        //given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn("");
        when(mView.getDomain()).thenReturn(DOMAIN);

        //when
        mLoginPresenter.onLoginButtonClicked(false);

        //then
        verify(mView).showErrorMessage(anyString());
    }

    @Test
    public void testLoginUserWithFalseResponse() {
        //given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(DOMAIN);
        when(mLoginUserInteractor.loginUser(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(Observable.just(false));

        //when
        mLoginPresenter.onLoginButtonClicked(false);

        //then
        verify(mView).showErrorMessage(matches("Login failed"));
    }

    @Test
    public void testLoginUserWithSuccessResponse() {
        //given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(DOMAIN);
        when(mLoginUserInteractor.loginUser(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(Observable.just(true));

        //when
        mLoginPresenter.onLoginButtonClicked(false);

        //then
        verify(mView).goToMainScreen();
    }

    @Test
    public void testForgotPasswordButtonPressed() {
        //given
        //nothing to do here

        //when
        mLoginPresenter.onForgotPasswordButtonClicked();

        //then
        verify(mView).goToForgotPasswordScreen();
    }
}
