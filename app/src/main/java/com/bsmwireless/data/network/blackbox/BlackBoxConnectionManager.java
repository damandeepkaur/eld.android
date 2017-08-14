package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import io.reactivex.Observable;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by osminin on 10.08.2017.
 */

public interface BlackBoxConnectionManager {

    Observable<BlackBoxConnectionManager> connectBlackBox();

    Observable<BlackBoxConnectionManager> disconnectBlackBox();

    boolean isConnected();

    Observable<BlackBoxModel> getDataObservable();

    BlackBox getBlackBox();
}
