package com.bsmwireless.screens.navigation;

import com.bsmwireless.data.storage.users.UserEntity;
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
        verify(mView).showErrorMessage(anyString());    // TODO: change to check string resource when no longer hard-coded

        verify(mView, never()).goToLoginScreen();   // redundant for now, but want to enforce no
                                                    // transition in future on failure
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
        UserEntity fakeUserEntity = new UserEntity();
        ResponseMessage fakeSuccessfulResponseMessage = new ResponseMessage();

        when(mLoginUserInteractor.getUser()).thenReturn(Flowable.just(fakeUserEntity));
        when(mLoginUserInteractor.updateUserOnServer(any(User.class))).thenReturn(Observable.just(fakeSuccessfulResponseMessage));

        // when
        mNavigationPresenter.onUserUpdated();

        // then
        verify(mLoginUserInteractor).getUser();
        verify(mLoginUserInteractor).updateUserOnServer(any(User.class)); // TODO: constrain test further once NavigationPresenter#getUpdatedUser is no longer a stub
    }

    // TODO: add test for LoginUserInteractor#updateUserOnServer failure when control path implemented
    // TODO: add test for LoginUserInteractor#updateUserOnServer error when user-observable workflow complete
    // TODO: add tests for getUpdatedUser if/when it becomes testable

}
