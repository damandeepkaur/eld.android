package com.bsmwireless.data.network.blackbox;

import com.bsmwireless.models.BlackBoxModel;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class BlackBoxConnectionManagerImpl implements BlackBoxConnectionManager {

    private BlackBox mBlackBox;

    @Inject
    public BlackBoxConnectionManagerImpl(BlackBox blackBox) {
        mBlackBox = blackBox;
    }

    @Override
    public Single<BlackBoxConnectionManager> connectBlackBox(int boxId) {
        return Completable.fromAction(() -> mBlackBox.connect(boxId))
                .andThen(Single.just(this));
    }

    @Override
    public Single<BlackBoxConnectionManager> disconnectBlackBox() {
        return Completable.fromAction(() -> mBlackBox.disconnect())
                .andThen(Single.just(this));
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
