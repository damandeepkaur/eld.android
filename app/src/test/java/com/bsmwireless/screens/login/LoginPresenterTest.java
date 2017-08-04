package com.bsmwireless.screens.login;


import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.models.User;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import app.bsmuniversal.com.R;
import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String DOMAIN = "domain";

    @Mock
    LoginView mView;

    @Mock
    LoginUserInteractor mLoginUserInteractor;

    private LoginPresenter mLoginPresenter;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        mLoginPresenter = new LoginPresenter(mView, mLoginUserInteractor);
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
        verify(mView).showErrorMessage(LoginView.Error.ERROR_DOMAIN);
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
        verify(mView).showErrorMessage(LoginView.Error.ERROR_PASSWORD);
    }

    @Test
    public void testLoginUserWithFalseResponse() {
        //given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(DOMAIN);
        when(mLoginUserInteractor.loginUser(anyString(), anyString(), anyString(), anyBoolean(),
                eq(User.DriverType.DRIVER))).thenReturn(Observable.just(false));

        //when
        mLoginPresenter.onLoginButtonClicked(false);

        //then
        verify(mView).showErrorMessage(LoginView.Error.ERROR_UNEXPECTED);
    }

    @Test
    public void testLoginUserWithSuccessResponse() {
        //given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(DOMAIN);
        when(mLoginUserInteractor.loginUser(anyString(), anyString(), anyString(), anyBoolean(),
                eq(User.DriverType.DRIVER))).thenReturn(Observable.just(true));

        //when
        mLoginPresenter.onLoginButtonClicked(false);

        //then
        verify(mView).goToSelectAssetScreen();
    }
}
