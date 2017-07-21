package com.bsmwireless.screens.selectasset;

import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.Vehicle;

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
import io.reactivex.Completable;
import io.reactivex.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SelectAssetPresenter.
 */

@RunWith(MockitoJUnitRunner.class)
public class SelectAssetPresenterTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    SelectAssetView mView;

    @Mock
    VehiclesInteractor mVehiclesInteractor;

    @Mock
    LoginUserInteractor mLoginUserInteractor;

    private SelectAssetPresenter mSelectAssetPresenter;

    private List<Vehicle> vehicles;

    /**
     * Builds a vehicle for unit test purposes. Add fields if required.
     */
    private static Vehicle buildVehicle(int id, String name) {
        Vehicle v = new Vehicle();
        v.setId(id);
        v.setName(name);

        return v;
    }

    /**
     * Build test vehicle list.
     */
    private static List<Vehicle> buildVehicleList() {
        List<Vehicle> result = new ArrayList<>();
        result.add(buildVehicle(1111, "abc"));
        result.add(buildVehicle(2222, "abcde"));
        return result;
    }


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        mSelectAssetPresenter = new SelectAssetPresenter(mView, mVehiclesInteractor, mLoginUserInteractor);

        vehicles = buildVehicleList();
    }


    // TODO: onSearchTextChanged
        // TODO: unsure if < 3 error message is expected or placeholder - pls. remove if needed

    @Test
    public void testOnSearchTextChangedLessThan3Chars() {
        // given
        String searchText = "ab";

        // when
        mSelectAssetPresenter.onSearchTextChanged(searchText);

        // then
        verify(mView).showErrorMessage();
    }

    @Test
    public void testOnSearchTextChangedEmpty() {
        // given
        String searchText = "";

        // when
        mSelectAssetPresenter.onSearchTextChanged(searchText);

        // then
        verify(mView).setEmptyList();
    }

    /**
     * Verify behavior of non-empty search response.
     */
    @Test
    public void testOnSearchTextChangedSuccess() {
        // given
        String searchText = "abc";
        when(mVehiclesInteractor.searchVehicles(eq("abc"))).thenReturn(Observable.just(vehicles));

        // when
        mSelectAssetPresenter.onSearchTextChanged(searchText);

        // then
        verify(mView).setVehicleList(eq(vehicles), eq(searchText));
    }

    /**
     * Verify behavior of empty search response.
     */
    @Test
    public void testOnSearchTextChangedUnsuccessful() {
        // given
        String searchText = "expect no results";
        final List<Vehicle> emptyList = new ArrayList<>();
        when(mVehiclesInteractor.searchVehicles(anyString())).thenReturn(Observable.just(emptyList));

        // when
        mSelectAssetPresenter.onSearchTextChanged(searchText);

        // then
        verify(mView).showEmptyListMessage();
    }


    // TODO: onCancelButtonPressed
    // TODO: skipping for now as Cancel button does not appear in interaction map - add tests or delete comment if this changes


    /**
     * Verify call to VehiclesInteractor#cleanSelectedVehicle.
     */
    @Test
    public void testOnNotInVehicleCleanSelectedVehicle() {
        // given
        when(mVehiclesInteractor.cleanSelectedVehicle()).thenReturn(Completable.complete());

        // when
        mSelectAssetPresenter.onNotInVehicleButtonClicked();

        // then
        verify(mVehiclesInteractor).cleanSelectedVehicle();
    }

    @Test
    public void testOnNotInVehicleGoToMainScreen() {
        // given
        when(mVehiclesInteractor.cleanSelectedVehicle()).thenReturn(Completable.complete());

        // when
        mSelectAssetPresenter.onNotInVehicleButtonClicked();

        // then
        verify(mView).goToHomeScreen();
    }

    /**
     * Verify pair vehicle.
     */
    @Test
    public void testOnVehicleListItemClickedPairVehicle() {
        // given
        Vehicle fakeVehicle = new Vehicle();
        when(mVehiclesInteractor.pairVehicle(any(Vehicle.class))).thenReturn(Observable.just(new ArrayList<>()));

        // when
        mSelectAssetPresenter.onVehicleListItemClicked(fakeVehicle);

        // then
        verify(mVehiclesInteractor).pairVehicle(eq(fakeVehicle));
    }

    /**
     * Verify go-to main screen.
     */
    // TODO: I cannot tell if this behavior is currently a stub - pls. remove test if it is
    @Test
    public void testOnVehicleClickedGoToMain() {
        // given
        Vehicle fakeVehicle = new Vehicle();
        when(mVehiclesInteractor.pairVehicle(any(Vehicle.class))).thenReturn(Observable.just(new ArrayList<>()));

        // when
        mSelectAssetPresenter.onVehicleListItemClicked(fakeVehicle);

        // then
        verify(mView).goToHomeScreen();
    }

}
