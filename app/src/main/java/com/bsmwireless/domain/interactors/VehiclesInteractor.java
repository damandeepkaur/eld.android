package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.vehicle.VehicleConverter;
import com.bsmwireless.models.Vehicle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class VehiclesInteractor {
    private static final int NOT_IN_VEHICLE_ID = -1;

    @Inject
    ServiceApi mServiceApi;

    @Inject
    @Named(Constants.IO_THREAD)
    Scheduler mIoThread;

    @Inject
    AppDatabase mAppDatabase;

    @Inject
    HttpClientManager mClientManager;

    @Inject
    TokenManager mTokenManager;

    @Inject
    PreferencesManager mPreferencesManager;

    public VehiclesInteractor() {
        App.getComponent().inject(this);
    }

    public Observable<List<Vehicle>> searchVehicles(int selectedProperty, String searchText, boolean isScan) {
        return mServiceApi.searchVehicles(selectedProperty, searchText, isScan ? 1 : 0).subscribeOn(mIoThread);
    }

    public Completable saveSelectedVehicle(Vehicle vehicle) {
        return Completable.fromAction(() -> {
            mAppDatabase.vehicleModel().insertVehicle(VehicleConverter.toEntity(vehicle));
            mPreferencesManager.setSelectedVehicleId(vehicle.getId());
            mPreferencesManager.setSelectedBoxId(vehicle.getBoxId());
        }).subscribeOn(mIoThread);
    }

    public Completable cleanSelectedVehicle() {
        return Completable.fromAction(
                () ->  {
                    mPreferencesManager.setSelectedVehicleId(NOT_IN_VEHICLE_ID);
                    mPreferencesManager.setSelectedBoxId(NOT_IN_VEHICLE_ID);
                })
                .subscribeOn(mIoThread);
    }
}
