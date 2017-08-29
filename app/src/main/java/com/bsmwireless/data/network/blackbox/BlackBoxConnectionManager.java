package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import io.reactivex.Observable;

public interface BlackBoxConnectionManager {

    Observable<BlackBoxConnectionManager> connectBlackBox(int boxId);

    Observable<BlackBoxConnectionManager> disconnectBlackBox();

    boolean isConnected();

    Observable<BlackBoxModel> getDataObservable();

    BlackBox getBlackBox();
}
