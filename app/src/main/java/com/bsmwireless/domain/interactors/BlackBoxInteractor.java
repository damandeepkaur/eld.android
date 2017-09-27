package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.models.BlackBoxModel;

import javax.inject.Inject;

import io.reactivex.Observable;

public final class BlackBoxInteractor {

    private BlackBoxConnectionManager mConnectionManager;

    @Inject
    public BlackBoxInteractor(BlackBoxConnectionManager connectionManager) {
        mConnectionManager = connectionManager;
    }

    public Observable<BlackBoxModel> getData(int boxId) {
        if (!mConnectionManager.isConnected()) {
            return mConnectionManager.connectBlackBox(boxId)
                    .flatMapObservable(BlackBoxConnectionManager::getDataObservable)
                    .doOnError(error -> mConnectionManager.disconnectBlackBox());
        }

        return mConnectionManager.getDataObservable();
    }

    public BlackBoxModel getLastData() {
        BlackBox blackBox = mConnectionManager.getBlackBox();
        return blackBox == null ? new BlackBoxModel() : blackBox.getBlackBoxState();
    }

    public <T> Observable<T> shutdown(T item) {
        return mConnectionManager.disconnectBlackBox()
                .flatMapObservable(blackBoxConnectionManager -> Observable.just(item));
    }

    public String getVinNumber() {
        BlackBox blackBox = mConnectionManager.getBlackBox();
        return blackBox == null ? "" : blackBox.getVinNumber();
    }
}
