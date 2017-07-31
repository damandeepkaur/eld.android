package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.connection.ConnectionManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.Vehicle;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

//TODO: return real data
public class BlackBoxInteractor {
    private ConnectionManager mConnectionManager;

    private PreferencesManager mPreferencesManager;

    @Inject
    public BlackBoxInteractor(ConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }
    public BlackBoxInteractor(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
    }

    public Observable<BlackBoxModel> getData() {
        BlackBoxModel model = new BlackBoxModel();

        if (mPreferencesManager.getBoxId() == PreferencesManager.NOT_FOUND_VALUE) {
            model.setOdometer(-1);
            model.setEngineHours(-1);
            model.setLat(0);
            model.setLon(0);
        } else {
            model.setOdometer(111222);
            model.setEngineHours(50);
            model.setLat(70.333);
            model.setLon(-40.1128);
        }

        return Observable.just(model);
    }

    public void connectVehicle(Vehicle vehicle)
    {
        mConnectionManager.connect(vehicle);
    }
    public void disconnectVehicle()
    {
        mConnectionManager.disconnect();
    }
    public Subject<ConnectionManager.ConnectionStatus> getConnectionState() {

        return mConnectionManager.getConnectionStateObservable();
    }
}
