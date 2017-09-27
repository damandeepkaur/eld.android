package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.data.network.NtpClientManager;
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

public final class TimingJob extends BaseMalfunctionJob implements MalfunctionJob {

    private final NtpClientManager mNtpClientManager;
    private final AppSettings mAppSettings;

    @Inject
    public TimingJob(ELDEventsInteractor eldEventsInteractor,
                     DutyTypeManager dutyTypeManager,
                     NtpClientManager ntpClientManager,
                     AppSettings appSettings,
                     BlackBoxInteractor blackBoxInteractor,
                     PreferencesManager preferencesManager) {
        super(eldEventsInteractor, dutyTypeManager, blackBoxInteractor, preferencesManager);
        mNtpClientManager = ntpClientManager;
        mAppSettings = appSettings;
    }

    @Override
    public void start() {
        Timber.d("Start timing compliance detection");
        Disposable disposable = getIntervalObservable()
                .doOnNext(unused -> Timber.d("Check the time compliance"))
                .flatMap(unsed -> getBlackboxData())
                .flatMap(this::loadLatestTimingEvent)
                .filter(result -> isCurrentTimingEventAndStateDifferent(result.mELDEvent))
                .map(result -> createEvent(Malfunction.TIMING_COMPLIANCE,
                        createCodeForMalfunction(result.mELDEvent),
                        result.mBlackBoxModel))
                .doOnNext(eldEvent -> Timber.d("Save new timing compliance event: " + eldEvent))
                .flatMap(this::saveEvents)
                .onErrorReturn(throwable -> {
                    Timber.e(throwable, "Error save the eld event");
                    return -1L;
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
        add(disposable);
    }

    @Override
    public void stop() {
        Timber.d("Stop timing compliance detection");
        dispose();
    }

    @VisibleForTesting
    Observable<Long> getIntervalObservable() {
        return Observable.interval(mAppSettings.getIntervalForCheckTime(), TimeUnit.MILLISECONDS);
    }

    private Observable<Result> loadLatestTimingEvent(BlackBoxModel blackBoxModel) {
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE)
                .toObservable()
                .switchIfEmpty(observer -> {
                    // create default event with cleared status
                    ELDEvent eldEvent = getELDEventsInteractor()
                            .getEvent(Malfunction.TIMING_COMPLIANCE,
                                    ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED,
                                    blackBoxModel);
                    observer.onNext(eldEvent);
                })
                .map(eldEvent -> new Result(eldEvent, blackBoxModel));
    }

    private boolean isCurrentTimingEventAndStateDifferent(ELDEvent eldEvent) {
        long realTimeInMillisDiff = mNtpClientManager.getRealTimeInMillisDiff();
        long timingMalfunctionDiff = mAppSettings.getTimingMalfunctionDiff();
        int eventCode = eldEvent.getEventCode();

        if (realTimeInMillisDiff > timingMalfunctionDiff) {
            //Compliance detected, check current event
            if (eventCode == ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode()) return true;
        } else {
            if (eventCode == ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode()) return true;
        }

        return false;
    }


}
