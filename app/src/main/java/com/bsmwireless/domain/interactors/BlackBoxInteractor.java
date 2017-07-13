package com.bsmwireless.domain.interactors;

import com.bsmwireless.models.BlackBoxModel;

import javax.inject.Inject;

import io.reactivex.Observable;

//TODO: return real data
public class BlackBoxInteractor {

    @Inject
    public BlackBoxInteractor() {}

    public Observable<BlackBoxModel> getData() {
        BlackBoxModel model = new BlackBoxModel();
        model.setOdometer(111222);
        model.setLat(70.333);
        model.setLon(-40.1128);

        return Observable.just(model);
    }
}
