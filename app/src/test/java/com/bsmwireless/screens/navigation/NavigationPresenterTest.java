package com.bsmwireless.screens.navigation;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.SyncInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
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
    UserInteractor mUserInteractor;

    @Mock
    VehiclesInteractor mVehiclesInteractor;

    @Mock
    DutyTypeManager mDutyTypeManager;

    @Mock
    ELDEventsInteractor mEventsInteractor;

    @Mock
    AutoDutyTypeManager mAutoDutyTypeManager;

    @Mock
    SyncInteractor mSyncInteractor;

    @Mock
    AccountManager mAccountManager;


    private NavigationPresenter mNavigationPresenter;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mNavigationPresenter = new NavigationPresenter(mView, mUserInteractor, mVehiclesInteractor, mEventsInteractor, mDutyTypeManager, mAutoDutyTypeManager, mSyncInteractor, mAccountManager);
    }

    /**
     * Verify call to UserInteractor#logoutUser
     */
    @Test
    public void testOnLogoutInteractorCall() {
        // given
        when(mEventsInteractor.postLogoutEvent()).thenReturn(Observable.just(true));

        // when
        mNavigationPresenter.onLogoutItemSelected();

        // then
        verify(mEventsInteractor).postLogoutEvent();
        verify(mUserInteractor).deleteDriver();
    }

    /**
     * Verify no transition to login screen if logout API request fails
     */
    @Test
    public void testOnLogoutFailed() {
        // given
        when(mEventsInteractor.postLogoutEvent()).thenReturn(Observable.just(false));

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
        when(mEventsInteractor.postLogoutEvent()).thenReturn(Observable.error(new RuntimeException("it broke.")));

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
        final Flowable<Integer> coDriverCountFlowable = Flowable.just(3);

        final int coDriver = 3; // note: business logic is incorrect as can have multiple co-drivers
        // TODO: add refactor task/story to JIRA after server-side API refactors to match correct business logic

        final int boxId = 1111;
        final int assetNumber = 2222;

        when(mUserInteractor.getFullDriverName()).thenReturn(userFlowable);
        when(mUserInteractor.getCoDriversNumber()).thenReturn(coDriverCountFlowable);
        when(mVehiclesInteractor.getBoxId()).thenReturn(boxId);
        when(mVehiclesInteractor.getAssetsNumber()).thenReturn(assetNumber);

        when(mUserInteractor.isLoginActive()).thenReturn(true);

        // when
        mNavigationPresenter.onViewCreated();

        // then
        verify(mView).setDriverName(eq(name));
        verify(mView).setCoDriversNumber(eq(coDriver));
        verify(mView).setBoxId(eq(boxId));
        verify(mView).setAssetsNumber(eq(assetNumber));
    }
}
