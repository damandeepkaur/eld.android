package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.data.storage.vehicles.VehicleConverter;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Vehicle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;

public class VehiclesInteractor {
    private static final int NOT_IN_VEHICLE_ID = -1;

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private PreferencesManager mPreferencesManager;
    private LoginUserInteractor mUserInteractor;
    private BlackBoxInteractor mBlackBoxInteractor;
    private ELDEventsInteractor mELDEventsInteractor;

    @Inject
    public VehiclesInteractor(ServiceApi serviceApi,
                              PreferencesManager preferencesManager,
                              AppDatabase appDatabase,
                              LoginUserInteractor userInteractor,
                              BlackBoxInteractor blackBoxInteractor,
                              ELDEventsInteractor eventsInteractor) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mUserInteractor = userInteractor;
        mBlackBoxInteractor = blackBoxInteractor;
        mELDEventsInteractor = eventsInteractor;
    }

    public Observable<List<Vehicle>> searchVehicles(String searchText) {
        return mServiceApi.searchVehicles(searchText);
    }

    public void saveVehicle(Vehicle vehicle) {
        mAppDatabase.vehicleDao().insertVehicle(VehicleConverter.toEntity(vehicle));
        mPreferencesManager.setVehicleId(vehicle.getId());
        mPreferencesManager.setBoxId(vehicle.getBoxId());
    }

    public void saveLastVehicle(int driverId, Integer vehicleId) {
        String lastVehicles = mAppDatabase.userDao().getUserLastVehiclesSync(driverId);
        Iterator<Integer> iterator = ListConverter.toIntegerList(lastVehicles).iterator();

        StringBuilder builder = new StringBuilder();
        builder.append(vehicleId);

        int count = 1;
        while (count < Constants.MAX_LAST_VEHICLE && iterator.hasNext()) {
            Integer next = iterator.next();

            if (!next.equals(vehicleId)) {
                builder.append(",");
                builder.append(next);
                count ++;
            }
        }

        mAppDatabase.userDao().setUserLastVehicles(driverId, builder.toString());
    }

    public Completable cleanSelectedVehicle() {
        return Completable.fromAction(
                () -> {
                    mPreferencesManager.setVehicleId(NOT_IN_VEHICLE_ID);
                    mPreferencesManager.setBoxId(NOT_IN_VEHICLE_ID);
                });
    }

    public Observable<List<ELDEvent>> pairVehicle(Vehicle vehicle) {
        ELDEvent event = new ELDEvent();
        int id = mUserInteractor.getDriverId();

        //TODO: get real data for hos
        event.setEngineHours(50);

        event.setMobileTime(System.currentTimeMillis());
        event.setDriverId(id);
        event.setVehicleId(vehicle.getId());
        event.setBoxId(vehicle.getBoxId());

        return mBlackBoxInteractor.getData()
                .doOnNext(blackBox -> saveVehicle(vehicle))
                .flatMap(blackBox -> {
                    event.setTimezone(mUserInteractor.getTimezone(id));
                    event.setOdometer(blackBox.getOdometer());
                    event.setLat(blackBox.getLat());
                    event.setLng(blackBox.getLon());

                    return mServiceApi.pairVehicle(event);
                })
                .doOnNext(events -> {
                    saveLastVehicle(id, vehicle.getId());
                    mELDEventsInteractor.storeEvents(events, true);
                })
                .doOnError(error -> cleanSelectedVehicle().blockingAwait());
    }

    public Flowable<List<Vehicle>> getLastVehicles() {
        return mAppDatabase.userDao().getUserLastVehicles(mUserInteractor.getDriverId())
                .flatMap(userLastVehicles -> mAppDatabase.vehicleDao().getVehicles(ListConverter.toIntegerList(userLastVehicles.length == 0 ? "" : userLastVehicles[0])))
                .flatMap(vehicleEntities -> Flowable.just(VehicleConverter.toVehicle(vehicleEntities)));
    }

    public int getBoxId() {
        return mPreferencesManager.getBoxId();
    }

    public int getAssetsNumber() {
        //TODO: implement getting assets number
        return mPreferencesManager.getBoxId() > 0 ? 1 : 0;
    }
}
