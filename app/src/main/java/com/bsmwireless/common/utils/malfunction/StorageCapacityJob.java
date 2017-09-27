package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.StorageUtil;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class StorageCapacityJob extends BaseMalfunctionJob implements MalfunctionJob {

    private final AppSettings mAppSettings;
    private final StorageUtil mStorageUtil;

    @Inject
    public StorageCapacityJob(ELDEventsInteractor eldEventsInteractor,
                              DutyTypeManager dutyTypeManager,
                              BlackBoxInteractor blackBoxInteractor,
                              PreferencesManager settingsManager,
                              AppSettings appSettings,
                              StorageUtil storageUtil) {
        super(eldEventsInteractor, dutyTypeManager, blackBoxInteractor, settingsManager);
        mAppSettings = appSettings;
        mStorageUtil = storageUtil;
    }

    @Override
    public void start() {
        Disposable disposable = getIntervalObservable()
                .flatMap(unused -> getBlackboxData())
                .flatMap(this::loadLatest)
                .filter(result -> isStateEndEventAreDifferent(result.mELDEvent))
                .flatMap(result -> Observable.fromCallable(() -> createEvent(Malfunction.DATA_RECORDING_COMPLIANCE,
                        createCodeForMalfunction(result.mELDEvent), result.mBlackBoxModel)))
                .doOnNext(eldEvent -> Timber.d("Save new Data Recording Compliance event: " + eldEvent))
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
    Observable<Long> getIntervalObservable() {
        return Observable.interval(mAppSettings.getIntervalForCheckStorageCapacity(),
                TimeUnit.MILLISECONDS);
    }

    Observable<Result> loadLatest(BlackBoxModel blackBoxModel) {
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.DATA_RECORDING_COMPLIANCE)
                .toObservable()
                .switchIfEmpty(observer -> switchToDefaultMalfunctionCleared(observer,
                        Malfunction.DATA_RECORDING_COMPLIANCE,
                        ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED,
                        blackBoxModel))
                .map(eldEvent -> new Result(eldEvent, blackBoxModel));
    }

    private boolean isStateEndEventAreDifferent(ELDEvent eldEvent) {

        int eventCode = eldEvent.getEventCode();

        if (isFreeSpaceEnough()) {
            if (eventCode == ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode()) return true;
        } else {
            if (eventCode == ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode()) return true;
        }

        return false;
    }

    private boolean isFreeSpaceEnough() {
        long storageFreeSpace = mStorageUtil.getAvailableSpace();
        long totalSpace = mStorageUtil.getTotalSpace();
        return ((double) storageFreeSpace / totalSpace) > mAppSettings.getFreeSpaceThreshold();
    }
}
