package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by osminin on 10.08.2017.
 */

public class BlackBoxConnectionManagerImpl implements BlackBoxConnectionManager {

    private BlackBox mBlackBox;

    @Inject
    public BlackBoxConnectionManagerImpl(BlackBox blackBox) {
        mBlackBox = blackBox;
    }

    @Override
    public Observable<BlackBoxConnectionManager> connectBlackBox() {
        return Observable.just((BlackBoxConnectionManager) this)
                .subscribeOn(Schedulers.io())
                .doOnNext(manager -> manager.getBlackBox().connect());
    }

    @Override
    public Observable<BlackBoxConnectionManager> disconnectBlackBox() {
        return Observable.just((BlackBoxConnectionManager) this)
                .doOnNext(manager -> manager.getBlackBox().disconnect());
    }

    @Override
    public boolean isConnected() {
        return mBlackBox.isConnected();
    }

    @Override
    public Observable<BlackBoxModel> getDataObservable() {
        return mBlackBox.getDataObservable();
    }

    @Override
    public BlackBox getBlackBox() {
        return mBlackBox;
    }
}
