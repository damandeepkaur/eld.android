package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import java.io.IOException;

import io.reactivex.Observable;

/**
 * Created by osminin on 10.08.2017.
 */

public interface BlackBox {
    void connect() throws Exception;

    void disconnect() throws IOException;

    boolean isConnected();

    Observable<BlackBoxModel> getDataObservable();
}
