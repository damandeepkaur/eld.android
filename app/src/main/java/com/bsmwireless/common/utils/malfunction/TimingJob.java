package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class TimingJob extends BaseMalfunctionJob implements MalfunctionJob {

    private final NtpClientManager mNtpClientManager;
    private final SettingsManager mSettingsManager;

    @Inject
    public TimingJob(ELDEventsInteractor eldEventsInteractor,
                     DutyTypeManager dutyTypeManager,
                     NtpClientManager ntpClientManager,
                     SettingsManager settingsManager) {
        super(eldEventsInteractor, dutyTypeManager);
        mNtpClientManager = ntpClientManager;
        mSettingsManager = settingsManager;
    }

    @Override
    public void start() {
        Timber.d("Start timing compliance detection");
        Disposable disposable = Observable.interval(1, TimeUnit.MINUTES)
                .zipWith(loadLatestTimingEvent(), (unused, eldEvent) -> eldEvent)
                .filter(this::isCurrentTimingEventAndStateDifferent)
                .map(eldEvent -> createEvent(Malfunction.TIMING_COMPLIANCE,
                        createCodeForMalfunction(eldEvent)))
                .subscribe();
        add(disposable);
    }

    @Override
    public void stop() {
        Timber.d("Stop timing compliance detection");
        dispose();
    }

    private Observable<ELDEvent> loadLatestTimingEvent() {
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE)
                .toObservable()
                .switchIfEmpty(observer -> {
                    // create default event with cleared status
                    ELDEvent event = getELDEventsInteractor().getEvent(getDutyTypeManager().getDutyType());
                    event.setEventCode(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());
                    observer.onNext(event);
                });
    }

    private boolean isCurrentTimingEventAndStateDifferent(ELDEvent eldEvent) {

        long realTimeInMillisDiff = mNtpClientManager.getRealTimeInMillisDiff();
        long timingMalfunctionDiff = mSettingsManager.getTimingMalfunctionDiff();
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
