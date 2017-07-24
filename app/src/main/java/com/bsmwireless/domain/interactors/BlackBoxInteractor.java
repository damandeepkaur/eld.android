package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.Connection.ConnectionManager;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.Vehicle;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

//TODO: return real data
public class BlackBoxInteractor {
    private ConnectionManager mConnectionManager;
    @Inject
    public BlackBoxInteractor(ConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    public Observable<BlackBoxModel> getData() {
        BlackBoxModel model = new BlackBoxModel();
        model.setOdometer(111222);
        model.setLat(70.333);
        model.setLon(-40.1128);

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
    public Subject<ConnectionManager.ConnectionState> getConnectionState() {

        return mConnectionManager.getConnectionStateObservable();
    }
}
