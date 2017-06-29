package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.vehicle.VehicleConverter;
import com.bsmwireless.models.Vehicle;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class VehiclesInteractor {
    private static final int NOT_IN_VEHICLE_ID = -1;

    private ServiceApi mServiceApi;

    private Scheduler mIoThread;

    private AppDatabase mAppDatabase;

    private HttpClientManager mClientManager;

    private TokenManager mTokenManager;

    private PreferencesManager mPreferencesManager;

    public VehiclesInteractor(ServiceApi serviceApi, Scheduler ioThread, AppDatabase appDatabase, HttpClientManager clientManager, TokenManager tokenManager, PreferencesManager preferencesManager) {
        this.mServiceApi = serviceApi;
        this.mIoThread = ioThread;
        this.mAppDatabase = appDatabase;
        this.mClientManager = clientManager;
        this.mTokenManager = tokenManager;
        this.mPreferencesManager = preferencesManager;
    }

    public Observable<List<Vehicle>> searchVehicles(int selectedProperty, String searchText, boolean isScan) {
        return mServiceApi.searchVehicles(selectedProperty, searchText, isScan ? 1 : 0).subscribeOn(mIoThread);
    }

    public Completable saveSelectedVehicle(Vehicle vehicle) {
        return Completable.fromAction(() -> {
            mAppDatabase.vehicleModel().insertVehicle(VehicleConverter.toEntity(vehicle));
            mPreferencesManager.setSelectedVehicleId(vehicle.getId());
        }).subscribeOn(mIoThread);
    }

    public Completable cleanSelectedVehicle() {
        return Completable.fromAction(
                () -> mPreferencesManager.setSelectedVehicleId(NOT_IN_VEHICLE_ID))
                .subscribeOn(mIoThread);
    }
}
