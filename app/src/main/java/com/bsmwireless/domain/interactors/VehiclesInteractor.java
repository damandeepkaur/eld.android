package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.vehicles.VehicleConverter;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Vehicle;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class VehiclesInteractor {
    private static final int NOT_IN_VEHICLE_ID = -1;

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private TokenManager mTokenManager;
    private PreferencesManager mPreferencesManager;
    private BlackBoxInteractor mBlackBoxInteractor;
    private ELDEventsInteractor mELDEventsInteractor;

    @Inject
    public VehiclesInteractor(ServiceApi serviceApi,
                              PreferencesManager preferencesManager,
                              AppDatabase appDatabase,
                              TokenManager tokenManager,
                              BlackBoxInteractor blackBoxInteractor,
                              ELDEventsInteractor eventsInteractor) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mTokenManager = tokenManager;
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

    public Completable cleanSelectedVehicle() {
        return Completable.fromAction(
                () -> {
                    mPreferencesManager.setVehicleId(NOT_IN_VEHICLE_ID);
                    mPreferencesManager.setBoxId(NOT_IN_VEHICLE_ID);
                });
    }

    public Observable<List<ELDEvent>> pairVehicle(Vehicle vehicle) {
        ELDEvent event = new ELDEvent();
        int id = getDriverId();

        //TODO: get real data for hos
        event.setEngineHours(50);

        event.setMobileTime(System.currentTimeMillis());
        event.setDriverId(id);
        event.setVehicleId(vehicle.getId());
        event.setBoxId(vehicle.getBoxId());

        return mBlackBoxInteractor.getData()
                .flatMap(blackBox -> {
                    event.setTimezone(getTimezone(id));
                    event.setOdometer(blackBox.getOdometer());
                    event.setLat(blackBox.getLat());
                    event.setLng(blackBox.getLon());

                    return mServiceApi.pairVehicle(event, vehicle.getBoxId());
                })
                .doOnNext(events -> {
                    saveVehicle(vehicle);
                    mELDEventsInteractor.storeEvents(events, true);
                });
    }

    public Integer getDriverId() {
        String id = mTokenManager.getDriver(mPreferencesManager.getAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public String getTimezone(int driverId) {
        return mAppDatabase.userDao().getTimezoneById(driverId).getTimezone();
    }

    public int getBoxId() {
        return mPreferencesManager.getBoxId();
    }

    public int getAssetsNumber() {
        //TODO: implement getting assets number
        return 1;
    }
}
