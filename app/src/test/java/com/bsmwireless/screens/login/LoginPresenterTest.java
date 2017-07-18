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

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LoginPresenter
 *
 * <p>
 *     This currently imitates existing app.bsmuniversal.com.presentation.LoginPresenterTest.
 * </p>
 *
 * <p>
 *     This is to help learn the existing style conventions the team, as well as to
 *     try tests via Jenkins without interfering with the existing tests.
 *
 *     TODO: remove if/when necessary
 * </p>
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DOMAIN = "domain";

    @Mock
    LoginView mView;

    @Mock
    LoginUserInteractor mLoginUserInteractor;

    LoginPresenter mLoginPresenter;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        mLoginPresenter = new LoginPresenter(mView, mLoginUserInteractor);


    }

    private void mockLoginFields() {
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(DOMAIN);
    }

    @Test
    public void testOnLoginButtonClickedNullDomain() {
        // given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(null);

        // when
        mLoginPresenter.onLoginButtonClicked(false);

        // then
        verify(mView).showErrorMessage(anyString());
    }

    @Test
    public void testOnLoginButtonClickedNullUsername() {
        // given
        when(mView.getUsername()).thenReturn(null);
        when(mView.getPassword()).thenReturn(PASSWORD);
        when(mView.getDomain()).thenReturn(DOMAIN);

        // when
        mLoginPresenter.onLoginButtonClicked(false);

        // then
        verify(mView).showErrorMessage(anyString());
    }

    @Test
    public void testOnLoginButtonClickedNullPassword() {
        // given
        when(mView.getUsername()).thenReturn(USERNAME);
        when(mView.getPassword()).thenReturn(null);
        when(mView.getDomain()).thenReturn(DOMAIN);

        // when
        mLoginPresenter.onLoginButtonClicked(false);

        // then
        verify(mView).showErrorMessage(anyString());
    }

    @Test
    public void testLoginUserWithFalseResponse() {
        // given
        mockLoginFields();
        when(mLoginUserInteractor.loginUser(anyString(), anyString(), anyString(), anyBoolean(),
                eq(User.DriverType.DRIVER))).thenReturn(Observable.just(false));

        // when
        mLoginPresenter.onLoginButtonClicked(false);;

        // then
        verify(mView).showErrorMessage(anyString());

        // TODO: when this is changed to a resource, then match to the resource
        verify(mView).showErrorMessage(matches("Login failed")); // hard-coded = bad
    }

    @Test
    public void testLoginUserWithSuccessfulResponse() {
        // given
        mockLoginFields();
        when(mLoginUserInteractor.loginUser(anyString(), anyString(), anyString(), anyBoolean(),
                eq(User.DriverType.DRIVER))).thenReturn(Observable.just(true));

        // when
        mLoginPresenter.onLoginButtonClicked(false);

        // then
        verify(mView).goToMainScreen();
    }










}
