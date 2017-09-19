package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import java.io.IOException;

import io.reactivex.Observable;

public interface BlackBox {

    void connect(int boxId) throws Exception;

    void disconnect() throws IOException;

    boolean isConnected();

    Observable<BlackBoxModel> getDataObservable();

    String getVinNumber();

    BlackBoxModel getBlackBoxState();
}
