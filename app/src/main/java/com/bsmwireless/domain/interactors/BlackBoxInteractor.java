package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.BlackBoxModel;

import javax.inject.Inject;

import io.reactivex.Observable;

//TODO: return real data
public class BlackBoxInteractor {

    private PreferencesManager mPreferencesManager;
    private BlackBoxConnectionManager mConnectionManager;

    @Inject
    public BlackBoxInteractor(PreferencesManager preferencesManager, BlackBoxConnectionManager connectionManager) {
        mPreferencesManager = preferencesManager;
        mConnectionManager = connectionManager;
    }

    public Observable<BlackBoxModel> getData(int boxId) {
        if (!mConnectionManager.isConnected()) {
            return mConnectionManager.connectBlackBox(boxId)
                    .switchMap(connectionManager ->  connectionManager.getDataObservable());
        }

        return mConnectionManager.getDataObservable();
    }

    public <T> Observable<T> shutdown(T item) {
        return Observable.zip(Observable.just(item), mConnectionManager.disconnectBlackBox(),
                (tItem, manager) -> tItem);
    }
}
