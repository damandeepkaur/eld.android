package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StorageCapacityJob extends BaseMalfunctionJob implements MalfunctionJob {

    private final SettingsManager mSettingsManager;

    @Inject
    public StorageCapacityJob(ELDEventsInteractor eldEventsInteractor,
                              DutyTypeManager dutyTypeManager,
                              SettingsManager settingsManager) {
        super(eldEventsInteractor, dutyTypeManager);
        mSettingsManager = settingsManager;
    }

    @Override
    public void start() {
        Disposable disposable = getIntervalObservable()
                .filter(unused -> isStateEndEventAreDifferent())
                .flatMap(unused -> Observable.fromCallable(() ->
                        getELDEventsInteractor().getEvent(getDutyTypeManager().getDutyType())))
                .flatMap(this::saveEvents)
                .subscribeOn(Schedulers.io())
                .subscribe();
        add(disposable);
    }

    @Override
    public void stop() {
        dispose();
    }

    @VisibleForTesting
    Observable<Long> getIntervalObservable(){
        return Observable.interval(1, TimeUnit.MILLISECONDS);
    }

    private boolean isStateEndEventAreDifferent(){
        return true;
    }

    private boolean isFreeSpaceEnough(){
        return true;
    }


}
