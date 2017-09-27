package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface BlackBoxConnectionManager {

    Single<BlackBoxConnectionManager> connectBlackBox(int boxId);

    Single<BlackBoxConnectionManager> disconnectBlackBox();

    boolean isConnected();

    Observable<BlackBoxModel> getDataObservable();

    BlackBox getBlackBox();
}
