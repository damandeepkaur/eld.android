package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.vehicles.VehicleDao;
import com.bsmwireless.data.storage.vehicles.VehicleEntity;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Vehicle;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
    UserInteractor mUserInteractor;

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
        when(mServiceApi.searchVehicles(anyString())).thenReturn(Single.just(vehicles));

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

    @Test
    public void testGetVehiclesFromDB() {
        // given
        Integer[] arrFakeVehicleIds = {111, 222, 333, 444, 555};
        List<Integer> vehicleIds = Arrays.asList(arrFakeVehicleIds);
        List<VehicleEntity> vehicleEntities = new ArrayList<>();

        when(mAppDatabase.vehicleDao()).thenReturn(mVehicleDao);
        when(mVehicleDao.getVehicles(any(List.class))).thenReturn(Flowable.just(vehicleEntities));

        TestSubscriber<List<Vehicle>> testSubscriber = TestSubscriber.create();

        // when
        mVehiclesInteractor.getVehiclesFromDB(vehicleIds).subscribe(testSubscriber);

        // then
        verify(mVehicleDao).getVehicles(eq(vehicleIds)); // verify call to dao
    }

    @Test
    public void testCleanSelectedVehicle() {
        // given
        final int NOT_IN_VEHICLE_ID = -1; // if needed, extract from class
        TestObserver testObserver = TestObserver.create();

        // when
        mVehiclesInteractor.cleanSelectedVehicle().subscribe(testObserver);

        // then
        verify(mPreferencesManager).setVehicleId(eq(NOT_IN_VEHICLE_ID));
        verify(mPreferencesManager).setBoxId(eq(NOT_IN_VEHICLE_ID));
    }

    @Test
    public void testPairVehicleSuccess() {
        // given
        Vehicle vehicle = new Vehicle();
        vehicle.setId(11111);
        vehicle.setBoxId(22222);

        BlackBoxModel fakeData = new BlackBoxModel();

        List<ELDEvent> events = new ArrayList<>();
        events.add(new ELDEvent());
        events.add(new ELDEvent());

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(fakeData));
        when(mUserInteractor.getTimezoneSync(any(Integer.class))).thenReturn("fake timezone");
        when(mServiceApi.pairVehicle(any(ELDEvent.class))).thenReturn(Single.just(events));

        when(mAppDatabase.vehicleDao()).thenReturn(mVehicleDao); // for saveVehicle
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserLastVehiclesSync(any(Integer.class))).thenReturn("1,2");

        // when
        mVehiclesInteractor.pairVehicle(vehicle).subscribe(testObserver);

        // then

        verify(mBlackBoxInteractor).getData(anyInt());
        verify(mServiceApi).pairVehicle(any(ELDEvent.class));
        verify(mEldEventsInteractor).storeUnidentifiedEvents(any(List.class));
        testObserver.assertNoErrors();
    }


    // TODO: pairVehicle: black box fail - add when BlackBoxInteractor behavior is finalized


    @Test
    public void testGetLastVehicles() {
        // given
        String[] arrValidLastVehStrings = {"111,2222,333,4,5"}; // currently user dao returns one string of last vehicles in an array
        Integer[] arrExpectedVehicles = {111,2222,333,4,5};
        final int numVehicles = 5;


        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserLastVehicles(anyInt())).thenReturn(Flowable.just(arrValidLastVehStrings));
        when(mUserInteractor.getDriverId()).thenReturn(11111);
        when(mAppDatabase.vehicleDao()).thenReturn(mVehicleDao);
        when(mVehicleDao.getVehicles(any(List.class))).thenReturn(Flowable.just(new ArrayList<VehicleEntity>())); // doesn't matter for now what we return... change if needed

        TestSubscriber<List<Vehicle>> testSubscriber = TestSubscriber.create();

        // when
        mVehiclesInteractor.getLastVehicles().subscribe(testSubscriber);

        // then
        verify(mUserDao).getUserLastVehicles(anyInt());

        verify(mVehicleDao).getVehicles(argThat(new ArgumentMatcher<List<Integer>>() {

            @Override
            public boolean matches(List<Integer> argument) {
                boolean result = true;

                List<Integer> lastVehiclesList = (List<Integer>) argument;

                for(int i=0; i<numVehicles; i++) {
                    // all expected ids from the saved string end up getting fetched from vehicle dao?
                    result = result && lastVehiclesList.contains(arrExpectedVehicles[i]);
                }

                return result;
            }
        }));
    }

    @Test
    public void testGetBoxId() {
        // given
        // n/a

        // when
        mVehiclesInteractor.getBoxId();

        // then
        verify(mPreferencesManager).getBoxId();
    }


    // TODO: getAssetsNumber - test when implemented


    @Test
    public void testGetVehicleId() {
        // given
        // n/a

        // when
        mVehiclesInteractor.getVehicleId();

        // then
        verify(mPreferencesManager).getVehicleId();
    }


    /**
     * Builds a string compatible with last-vehicles list.
     *
     * @param numVehicles number of vehicles
     * @param startId start id for vehicles in list
     * @return fake last-vehicles list string
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
     * @param lastVehicles list of vehicle ids compatible with output of ListConverter#toString
     * @return list of last vehicles from a last-vehicles-list string
     */
    private List<Integer> getVehiclesFromLastVehString(String lastVehicles) {
        return ListConverter.toIntegerList(lastVehicles);
    }


    /**
     * Matches last-vehicle-list strings with a specific vehicle id on top.
     */
    private class LastVehicleIsAtTopMatcher implements ArgumentMatcher<String> {

        private int mExpectedVehicleId;

        public LastVehicleIsAtTopMatcher(int vehicleId) {
            mExpectedVehicleId = vehicleId;
        }

        @Override
        public boolean matches(String argument) {
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
    private class LastVehicleListSizeMatcher implements ArgumentMatcher<String> {
        private int mExpectedSize;

        public LastVehicleListSizeMatcher(int expectedNewListSize) {
            mExpectedSize = expectedNewListSize;
        }

        @Override
        public boolean matches(String argument) {
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
