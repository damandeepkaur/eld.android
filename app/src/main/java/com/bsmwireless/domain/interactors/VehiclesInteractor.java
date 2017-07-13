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
import io.reactivex.schedulers.Schedulers;

public class VehiclesInteractor {
    private static final int NOT_IN_VEHICLE_ID = -1;

    private ServiceApi mServiceApi;
    private AppDatabase mAppDatabase;
    private TokenManager mTokenManager;
    private PreferencesManager mPreferencesManager;

    @Inject
    public VehiclesInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase, TokenManager tokenManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mTokenManager = tokenManager;
    }

    public Observable<List<Vehicle>> searchVehicles(String searchText) {
        return mServiceApi.searchVehicles(searchText).subscribeOn(Schedulers.io());
    }

    public Completable saveSelectedVehicle(Vehicle vehicle) {
        return Completable.fromAction(() -> {
            mAppDatabase.vehicleDao().insertVehicle(VehicleConverter.toEntity(vehicle));
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

    public Observable<List<ELDEvent>> pairVehicle(ELDEvent event) {
        int boxId = mPreferencesManager.getSelectedBoxId();
        if (boxId == PreferencesManager.NOT_FOUND_VALUE) {
            return Observable.error(new Throwable("Not found selected boxId"));
        } else {
            return mServiceApi.pairVehicle(event, boxId).subscribeOn(Schedulers.io());
        }
    }

    public Integer getDriverId() {
        String id = mTokenManager.getDriver(mPreferencesManager.getAccountName());
        return id == null || id.isEmpty() ? -1 : Integer.valueOf(id);
    }

    public Observable<String> getTimezone(int driverId) {
        //TODO: get time zone from database
        return Observable.just("America/Puerto_Rico");
    }
}
