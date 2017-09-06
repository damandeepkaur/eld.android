package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class BlackBoxConnectionManagerImpl implements BlackBoxConnectionManager {

    private BlackBox mBlackBox;

    @Inject
    public BlackBoxConnectionManagerImpl(BlackBox blackBox) {
        mBlackBox = blackBox;
    }

    @Override
    public Observable<BlackBoxConnectionManager> connectBlackBox(int boxId) {
        return Observable.just((BlackBoxConnectionManager) this)
                .observeOn(Schedulers.single())
                .doOnNext(manager -> manager.getBlackBox().connect(boxId));
    }

    @Override
    public Observable<BlackBoxConnectionManager> disconnectBlackBox() {
        return Observable.just((BlackBoxConnectionManager) this)
                .observeOn(Schedulers.single())
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
