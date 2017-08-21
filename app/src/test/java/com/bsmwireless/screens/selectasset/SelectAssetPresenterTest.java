package com.bsmwireless.screens.selectasset;

import com.bsmwireless.domain.interactors.UserInteractor;
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
import io.reactivex.Flowable;
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
    UserInteractor mUserInteractor;

    private SelectAssetPresenter mSelectAssetPresenter;

    private List<Vehicle> mVehicles;

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
        mSelectAssetPresenter = new SelectAssetPresenter(mView, mVehiclesInteractor, mUserInteractor);

        mVehicles = buildVehicleList();
    }

    /** Verify rx setting of last vehicle list */
    @Test
    public void testOnViewCreatedSetLastVehicleList() {
        // given
        List<Vehicle> testVehicleList = buildVehicleList();
        when(mVehiclesInteractor.getLastVehicles()).thenReturn(Flowable.just(testVehicleList));
        when(mVehiclesInteractor.cleanSelectedVehicle()).thenReturn(Completable.complete());

        // when
        mSelectAssetPresenter.onViewCreated();

        // then
        verify(mView).setLastVehicleList(eq(testVehicleList));
    }

    @Test
    public void testOnViewCreatedSetEmptyLastList() {
        // given
        List<Vehicle> testVehicleList = new ArrayList<>();
        when(mVehiclesInteractor.getLastVehicles()).thenReturn(Flowable.just(testVehicleList));
        when(mVehiclesInteractor.cleanSelectedVehicle()).thenReturn(Completable.complete());

        // when
        mSelectAssetPresenter.onViewCreated();

        // then
        verify(mView).showEmptyLastListMessage();
    }

    @Test
    public void testOnViewCreatedEmptyLastList() {
        // given
        List<Vehicle> emptyVehicleList = new ArrayList<>();
        when(mVehiclesInteractor.getLastVehicles()).thenReturn(Flowable.just(emptyVehicleList));
        when(mVehiclesInteractor.cleanSelectedVehicle()).thenReturn(Completable.complete());

        // when
        mSelectAssetPresenter.onViewCreated();

        // then
        verify(mView).showEmptyLastListMessage();
    }

    @Test
    public void testOnSearchTextChangedLessThan3Chars() {
        // given
        String searchText = "ab";

        // when
        mSelectAssetPresenter.onSearchTextChanged(searchText);

        // then
        verify(mView).showSearchErrorMessage();
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
        when(mVehiclesInteractor.searchVehicles(eq("abc"))).thenReturn(Observable.just(mVehicles));

        // when
        mSelectAssetPresenter.onSearchTextChanged(searchText);

        // then
        verify(mView).setVehicleList(eq(mVehicles), eq(searchText));
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

    /**
     * Verify call to VehiclesInteractor#cleanSelectedVehicle.
     */
    @Test
    public void testOnNotInVehicleButtonClicked() {
        // given
        when(mVehiclesInteractor.cleanSelectedVehicle()).thenReturn(Completable.complete());

        // when
        mSelectAssetPresenter.onNotInVehicleButtonClicked();

        // then
        verify(mVehiclesInteractor).cleanSelectedVehicle();
        verify(mView).goToHomeScreen();
    }

    @Test
    public void testOnVehicleListItemClicked() {
        // given
        Vehicle fakeVehicle = new Vehicle();
        when(mVehiclesInteractor.pairVehicle(any(Vehicle.class))).thenReturn(Observable.just(new ArrayList<>()));

        // when
        mSelectAssetPresenter.onVehicleListItemClicked(fakeVehicle);

        // then
        verify(mVehiclesInteractor).pairVehicle(eq(fakeVehicle));
        verify(mView).goToHomeScreen();
    }

}
