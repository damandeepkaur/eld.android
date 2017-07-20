package com.bsmwireless.screens.navigation;

import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for NavigationPresenter.
 */

@RunWith(MockitoJUnitRunner.class)
public class NavigationPresenterTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    NavigateView mView;

    @Mock
    LoginUserInteractor mLoginUserInteractor;

    @Mock
    VehiclesInteractor mVehiclesInteractor;


    private NavigationPresenter mNavigationPresenter;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        mNavigationPresenter = new NavigationPresenter(mView, mLoginUserInteractor, mVehiclesInteractor);
    }

    // TODO: onLogoutItemSelected

    /** Verify call to LoginUserInteractor#logoutUser */
    @Test
    public void testOnLogoutInteractorCall() {
        // given
        when(mLoginUserInteractor.logoutUser()).thenReturn(Observable.just(true));

        // when
        mNavigationPresenter.onLogoutItemSelected();

        // then
        verify(mLoginUserInteractor).logoutUser();
    }

    /** Verify no transition to login screen if logout API request fails */
    @Test
    public void testOnLogoutFailed() {
        // given
        when(mLoginUserInteractor.logoutUser()).thenReturn(Observable.just(false));

        // when
        mNavigationPresenter.onLogoutItemSelected();

        // then
        verify(mView, never()).goToLoginScreen();
    }

    /** Verify error shown on failed API call to logout */
    @Test
    public void testOnLogoutFailedShowError() {
        // given
        when(mLoginUserInteractor.logoutUser()).thenReturn(Observable.just(false));

        // when
        mNavigationPresenter.onLogoutItemSelected();

        // then
        verify(mView).showErrorMessage(anyString());
    }

    @Test
    public void testOnViewCreatedInactiveLogin() {
        // given
        when(mLoginUserInteractor.isLoginActive()).thenReturn(false);

        // when
        mNavigationPresenter.onViewCreated();

        // then
        verify(mView).goToLoginScreen(); // possibly stub behavior?
    }

    @Test
    public void testOnViewCreatedSuccess() {
        // given
        final String user = "testUser";
        final int coDriver = 1; // note: business logic is incorrect as can have multiple co-drivers
                                // TODO: add refactor task/story to JIRA after server-side API refactors to match correct business logic
        final int boxId = 1111;
        final int assetNumber = 2222;
        when(mLoginUserInteractor.getUserName()).thenReturn(user);
        when(mLoginUserInteractor.getCoDriversNumber()).thenReturn(coDriver);
        when(mVehiclesInteractor.getBoxId()).thenReturn(boxId);
        when(mVehiclesInteractor.getAssetsNumber()).thenReturn(assetNumber);

        when(mLoginUserInteractor.isLoginActive()).thenReturn(true);

        // when
        mNavigationPresenter.onViewCreated();

        // then
        verify(mView).setDriverName(eq(user));
        verify(mView).setCoDriversNumber(eq(coDriver));
        verify(mView).setBoxId(eq(boxId));
        verify(mView).setAssetsNumber(eq(assetNumber));
    }

}
