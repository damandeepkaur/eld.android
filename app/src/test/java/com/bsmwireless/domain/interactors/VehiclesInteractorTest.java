package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.vehicles.VehicleDao;
import com.bsmwireless.data.storage.vehicles.VehicleEntity;
import com.bsmwireless.models.Vehicle;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for VehiclesInteractor
 */

@RunWith(MockitoJUnitRunner.class)
public class VehiclesInteractorTest {


    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    ServiceApi mServiceApi;

    @Mock
    AppDatabase mAppDatabase;

    @Mock
    PreferencesManager mPreferencesManager;

    @Mock
    LoginUserInteractor mUserInteractor;

    @Mock
    BlackBoxInteractor mBlackBoxInteractor;

    @Mock
    ELDEventsInteractor mEldEventsInteractor;

    @Mock
    private VehicleDao mVehicleDao;

    @Mock
    private UserDao mUserDao;


    private VehiclesInteractor mVehiclesInteractor;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mVehiclesInteractor = new VehiclesInteractor(mServiceApi, mPreferencesManager, mAppDatabase, mUserInteractor, mBlackBoxInteractor, mEldEventsInteractor);
    }


    /** Verify call to API layer */
    // TODO: might be too simple to break... ok to remove if that's the case
    @Test
    public void testSearchVehicles() {
        // given
        List<Vehicle> vehicles = new ArrayList<>();
        String searchText = "anything";
        when(mServiceApi.searchVehicles(anyString())).thenReturn(Observable.just(vehicles));

        // when
        mVehiclesInteractor.searchVehicles(searchText);

        // then
        verify(mServiceApi).searchVehicles(eq(searchText));
    }

    @Test
    public void testSaveVehicleSetPreferences() {
        // given
        final int vehicleId = 1111;
        final int boxId = 888;
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setBoxId(boxId);

        when(mAppDatabase.vehicleDao()).thenReturn(mVehicleDao);

        // when
        mVehiclesInteractor.saveVehicle(vehicle);

        // then

        // unsure why the following preferences are in saveVehicle(), but documenting in tests
        // nonetheless since these settings look important in other places in the app, and this is
        // the only place the setters appear
        verify(mPreferencesManager).setVehicleId(vehicleId);
        verify(mPreferencesManager).setBoxId(boxId);

        verify(mVehicleDao).insertVehicle(any(VehicleEntity.class));
    }

    @Test
    public void testSaveLastVehicleOnlyOneVehicle() {
        // given
        int driverId = 90210;
        int vehicleId = 112358;
        int prevListSize = 0;
        int expectedNewListSize = 1;

        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        // when
        mVehiclesInteractor.saveLastVehicle(driverId, vehicleId);

        // then
        verify(mUserDao).setUserLastVehicles(eq(driverId), argThat(new LastVehicleIsAtTopMatcher(vehicleId)));
        verify(mUserDao).setUserLastVehicles(eq(driverId), argThat(new LastVehicleListSizeMatcher(expectedNewListSize))); // expected last-vehicles list size is now 1
    }

    /**
     * Tests that all vehicles
     */
    @Test
    public void testSaveLastVehicleLessThanMax() {
        // given
        int driverId = 90210;
        int vehicleId = 112358;
        int prevListSize = Constants.MAX_LAST_VEHICLE - 1;
        int expectedNewListSize = Constants.MAX_LAST_VEHICLE;

        String lastVehicles = buildFakeVehicleList(prevListSize, 11111); // one less than max, so append is still ok without truncating a vehicle, starting id does not matter

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserLastVehiclesSync(any(Integer.class))).thenReturn(lastVehicles);

        // when
        mVehiclesInteractor.saveLastVehicle(driverId, vehicleId);

        // then
        verify(mUserDao).setUserLastVehicles(eq(driverId), argThat(new LastVehicleIsAtTopMatcher(vehicleId)));
        verify(mUserDao).setUserLastVehicles(eq(driverId), argThat(new LastVehicleListSizeMatcher(expectedNewListSize)));
    }

    @Test
    public void testSaveLastVehicleTooLongList() {
        // given
        int driverId = 90210;
        int vehicleId = 112358;
        int prevListSize = Constants.MAX_LAST_VEHICLE;
        int expectedNewListSize = Constants.MAX_LAST_VEHICLE;

        String lastVehicles = buildFakeVehicleList(prevListSize, 11111); // one less than max, so append is still ok without truncating a vehicle, starting id does not matter

        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserLastVehiclesSync(any(Integer.class))).thenReturn(lastVehicles);

        // when
        mVehiclesInteractor.saveLastVehicle(driverId, vehicleId);

        // then
        verify(mUserDao).setUserLastVehicles(eq(driverId), argThat(new LastVehicleIsAtTopMatcher(vehicleId)));
        verify(mUserDao).setUserLastVehicles(eq(driverId), argThat(new LastVehicleListSizeMatcher(expectedNewListSize)));
    }


    // TODO: cleanSelectedVehicle


    // TODO: pairVehicle
    // TODO: getLastVehicles
    // TODO: getBoxId()
    // TODO: getAssetsNumber


    /**
     * Builds a string compatible with last-vehicles list.
     *
     * @param numVehicles number of vehicles
     * @param startId start id for vehicles in list
     * @return
     */
    private String buildFakeVehicleList(int numVehicles, int startId) {
        if (numVehicles < 1) return "";

        StringBuilder sb = new StringBuilder();

        for(int i=0; i<numVehicles; i++) {

            if (i > 0) {
                sb.append(",");
            }

            sb.append(startId + i);
        }

        return sb.toString();
    }

    /**
     * Gets a list of vehicle id from a string of last-vehicles.
     *
     * @param lastVehicles
     * @return
     */
    private List<Integer> getVehiclesFromLastVehString(String lastVehicles) {
        return ListConverter.toIntegerList(lastVehicles);
    }


    /**
     * Matches last-vehicle-list strings with a specific vehicle id on top.
     */
    private class LastVehicleIsAtTopMatcher extends ArgumentMatcher<String> {

        private int mExpectedVehicleId;

        public LastVehicleIsAtTopMatcher(int vehicleId) {
            mExpectedVehicleId = vehicleId;
        }

        @Override
        public boolean matches(Object argument) {
            try {
                String argVehicles = (String) argument;

                return argVehicles.indexOf(Integer.toString(mExpectedVehicleId)) == 0;  // last vehicle is at top of list

            } catch (Exception e) {
                e.printStackTrace();
                fail(); // not expecting an exception, so fail
                return false;
            }
        }
    }

    /**
     * Matches last-vehicle-list strings with a specific number of vehicle ids.
     */
    private class LastVehicleListSizeMatcher extends ArgumentMatcher<String> {
        private int mExpectedSize;

        public LastVehicleListSizeMatcher(int expectedNewListSize) {
            mExpectedSize = expectedNewListSize;
        }

        @Override
        public boolean matches(Object argument) {
            try {
                String argVehicles = (String) argument;
                int resultNewListSize = getVehiclesFromLastVehString(argVehicles).size();

                return resultNewListSize == mExpectedSize;
            } catch (Exception e) {
                e.printStackTrace();
                fail(); // not expecting an exception, so fail
                return false;
            }
        }
    }



}
