package com.bsmwireless.screens.navigation;

import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
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
import io.reactivex.Flowable;
import io.reactivex.Observable;

import static org.mockito.Matchers.any;
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

    /**
     * Verify call to LoginUserInteractor#logoutUser
     */
    @Test
    public void testOnLogoutInteractorCall() {
        // given
        when(mLoginUserInteractor.logoutUser()).thenReturn(Observable.just(true));

        // when
        mNavigationPresenter.onLogoutItemSelected();

        // then
        verify(mLoginUserInteractor).logoutUser();
    }

    /**
     * Verify no transition to login screen if logout API request fails
     */
    @Test
    public void testOnLogoutFailed() {
        // given
        when(mLoginUserInteractor.logoutUser()).thenReturn(Observable.just(false));

        // when
        mNavigationPresenter.onLogoutItemSelected();

        // then

        // TODO: update expected logout logic when offline-mode is implemented
        verify(mView).showErrorMessage(anyString());    // TODO: change to check string resource when no longer hard-coded

        verify(mView, never()).goToLoginScreen();   // redundant for now, but want to enforce no
                                                    // future transition to login on failure (online mode)

    }

    @Test
    public void testOnLogoutError() {
        // given
        when(mLoginUserInteractor.logoutUser()).thenReturn(Observable.error(new RuntimeException("it broke.")));

        // when
        mNavigationPresenter.onLogoutItemSelected();

        // then
        verify(mView).showErrorMessage(anyString());    // TODO: change to check string resource when PO finalizes error message
        verify(mView, never()).goToLoginScreen();   // check no transition when logout fails
    }

    @Test
    public void testOnViewCreatedSuccess() {
        // given
        final String name = "userName";
        final Flowable<String> userFlowable = Flowable.just(name);

        final int coDriver = 1; // note: business logic is incorrect as can have multiple co-drivers
        // TODO: add refactor task/story to JIRA after server-side API refactors to match correct business logic

        final int boxId = 1111;
        final int assetNumber = 2222;

        when(mLoginUserInteractor.getFullName()).thenReturn(userFlowable);
        when(mLoginUserInteractor.getCoDriversNumber()).thenReturn(coDriver);
        when(mVehiclesInteractor.getBoxId()).thenReturn(boxId);
        when(mVehiclesInteractor.getAssetsNumber()).thenReturn(assetNumber);

        when(mLoginUserInteractor.isLoginActive()).thenReturn(true);

        // when
        mNavigationPresenter.onViewCreated();

        // then
        verify(mView).setDriverName(eq(name));
        verify(mView).setCoDriversNumber(eq(coDriver));
        verify(mView).setBoxId(eq(boxId));
        verify(mView).setAssetsNumber(eq(assetNumber));
    }

    /**
     * Test API call to update user.
     */
    @Test
    public void testOnUserUpdated() {
        // given
        User user = new User();
        ResponseMessage fakeSuccessfulResponseMessage = new ResponseMessage();
        when(mLoginUserInteractor.updateUser(any(User.class))).thenReturn(Observable.just(true)); // prevent null pointer exception

        // when
        mNavigationPresenter.onUserUpdated(user);

        // then
        verify(mLoginUserInteractor).updateUser(eq(user));
    }

    @Test
    public void testOnUserUpdatedNullUser() {
        // given
        // n/a

        // when
        mNavigationPresenter.onUserUpdated(null);

        // then
        verify(mLoginUserInteractor, never()).updateUser(any(User.class));
    }

    @Test
    public void testOnUserUpdatedError() {
        // given
        User user = new User();
        String error = "sorry, it didn't work";
        when(mLoginUserInteractor.updateUser(any(User.class))).thenReturn(Observable.error(new RuntimeException(error)));

        // when
        mNavigationPresenter.onUserUpdated(user);

        // then
        verify(mView).showErrorMessage(eq(error));
    }
}
