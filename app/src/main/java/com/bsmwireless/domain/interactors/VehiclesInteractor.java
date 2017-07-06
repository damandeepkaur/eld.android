package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.vehicle.VehicleConverter;
import com.bsmwireless.models.Vehicle;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class VehiclesInteractor {
    private static final int NOT_IN_VEHICLE_ID = -1;

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private PreferencesManager mPreferencesManager;

    public VehiclesInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
    }

    public Observable<List<Vehicle>> searchVehicles(int selectedProperty, String searchText, boolean isScan) {
        return mServiceApi.searchVehicles(selectedProperty, searchText, isScan ? 1 : 0).subscribeOn(Schedulers.io());
    }

    public Completable saveSelectedVehicle(Vehicle vehicle) {
        return Completable.fromAction(() -> {
            mAppDatabase.vehicleModel().insertVehicle(VehicleConverter.toEntity(vehicle));
            mPreferencesManager.setSelectedVehicleId(vehicle.getId());
            mPreferencesManager.setSelectedBoxId(vehicle.getBoxId());
        }).subscribeOn(Schedulers.io());
    }

    public Completable cleanSelectedVehicle() {
        return Completable.fromAction(
                () ->  {
                    mPreferencesManager.setSelectedVehicleId(NOT_IN_VEHICLE_ID);
                    mPreferencesManager.setSelectedBoxId(NOT_IN_VEHICLE_ID);
                })
                .subscribeOn(Schedulers.io());
    }
}
