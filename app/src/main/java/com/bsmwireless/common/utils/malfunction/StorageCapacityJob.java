package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.common.utils.StorageUtil;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public final class StorageCapacityJob extends BaseMalfunctionJob implements MalfunctionJob {

    private final SettingsManager mSettingsManager;
    private final StorageUtil mStorageUtil;

    @Inject
    public StorageCapacityJob(ELDEventsInteractor eldEventsInteractor,
                              DutyTypeManager dutyTypeManager,
                              SettingsManager settingsManager, StorageUtil storageUtil) {
        super(eldEventsInteractor, dutyTypeManager);
        mSettingsManager = settingsManager;
        mStorageUtil = storageUtil;
    }

    @Override
    public void start() {
        Disposable disposable = getIntervalObservable()
                .flatMap(unused -> loadLatest())
                .filter(this::isStateEndEventAreDifferent)
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

    Observable<ELDEvent> loadLatest(){
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.DATA_RECORDING_COMPLIANCE)
                .toObservable()
                .switchIfEmpty(this::switchToDefaultMalfunctionCleared);
    }

    private boolean isStateEndEventAreDifferent(ELDEvent eldEvent){

        int eventCode = eldEvent.getEventCode();

        if (isFreeSpaceEnough()) {
            if (eventCode == ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode()) return true;
        } else {
            if (eventCode == ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode()) return true;
        }

        return false;
    }

    private boolean isFreeSpaceEnough(){
        long storageFreeSpace = mStorageUtil.getAvailableSpace();
        long totalSpace = mStorageUtil.getTotalSpace();
        return ((double)storageFreeSpace / totalSpace) > mSettingsManager.getFreeSpaceThreshold();
    }
}
