package com.bsmwireless.services.malfunction;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


@ActivityScope
public final class MalfunctionServicePresenter {

    final BlackBoxConnectionManager mBlackBoxConnectionManager;
    final ELDEventsInteractor mELDEventsInteractor;
    private final DutyTypeManager mDutyTypeManager;
    final NtpClientManager mNtpClientManager;
    final SettingsManager mSettingsManager;
    private final CompositeDisposable mCompositeDisposable;

    @Inject
    public MalfunctionServicePresenter(BlackBoxConnectionManager blackBoxConnectionManager,
                                       ELDEventsInteractor eldEventsInteractor,
                                       DutyTypeManager dutyTypeManager,
                                       NtpClientManager ntpClientManager,
                                       SettingsManager settingsManager) {
        mBlackBoxConnectionManager = blackBoxConnectionManager;
        mELDEventsInteractor = eldEventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mNtpClientManager = ntpClientManager;
        mSettingsManager = settingsManager;
        mCompositeDisposable = new CompositeDisposable();
    }

    public void startMonitoring() {

        Timber.d("Start malfunction monitoring");

        startSynchronizationMonitoring();
        startTimeMonitoring();
    }

    public void stopMonitoring() {
        Timber.d("Stop malfunction monitoring");

        mCompositeDisposable.dispose();
    }

    private Observable<BlackBoxModel> getBlackBoxDataObservable() {
        return mBlackBoxConnectionManager.getDataObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    private void startSynchronizationMonitoring() {

        Disposable disposable = getBlackBoxDataObservable()
                .filter(blackBoxModel -> BlackBoxResponseModel.ResponseType.STATUS_UPDATE
                        == blackBoxModel.getResponseType())
                .flatMap(blackBoxModel -> loadLatestSynchronizationEvent(), SynchResult::new
                )
                .filter(this::isStateAndEventAreDifferent)
                .map(result -> createEvent(Malfunction.ENGINE_SYNCHRONIZATION,
                        createCodeForDiagnostic(result.mELDEvent)))
                .flatMap(this::saveEvents)
                .onErrorReturn(throwable -> {
                    Timber.e(throwable, "Error handle synchronization event");
                    return -1L;
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
        mCompositeDisposable.add(disposable);
    }

    private Observable<ELDEvent> loadLatestSynchronizationEvent() {
        return mELDEventsInteractor
                .getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION)
                .toObservable()
                .switchIfEmpty(observer -> {
                    // create default event with cleared status
                    ELDEvent event = mELDEventsInteractor.getEvent(mDutyTypeManager.getDutyType());
                    event.setEventCode(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
                    observer.onNext(event);
                });
    }

    /**
     * Checks current black box state and latest event for Synchronization monitoring in DB and
     * return true if they are different.
     * For example, if current sensor state is in ECM CABLE CONNECTED state and current event
     * in a database for this state is in DISCONNECTED state {@code true} will be returned
     *
     * @param synchResult
     * @return
     */
    private boolean isStateAndEventAreDifferent(SynchResult synchResult) {

        if (ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode() == synchResult.mELDEvent.getEventCode()) {
            return synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)
                    && synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC);
        } else if (ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode() == synchResult.mELDEvent.getEventCode()) {
            return !synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)
                    || !synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC);
        }
        return true;
    }

    private void startTimeMonitoring() {
        Disposable disposable = Observable.interval(1, TimeUnit.MINUTES)
                .zipWith(loadLatestTimingEvent(), (unused, eldEvent) -> eldEvent)
                .filter(this::isCurrentTimingEventAndStateDifferent)
                .map(eldEvent -> createEvent(Malfunction.TIMING_COMPLIANCE,
                        createCodeForMalfunction(eldEvent)))
                .subscribe();
        mCompositeDisposable.add(disposable);
    }

    private Observable<ELDEvent> loadLatestTimingEvent() {
        return mELDEventsInteractor
                .getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE)
                .toObservable()
                .switchIfEmpty(observer -> {
                    // create default event with cleared status
                    ELDEvent event = mELDEventsInteractor.getEvent(mDutyTypeManager.getDutyType());
                    event.setEventCode(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());
                    observer.onNext(event);
                });
    }

    private Observable<Long> saveEvents(ELDEvent eldEvent) {
        return mELDEventsInteractor.postNewELDEvent(eldEvent).toObservable();
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

    private ELDEvent.MalfunctionCode createCodeForDiagnostic(ELDEvent eldEvent) {
        return eldEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode() ?
                ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED :
                ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED;
    }

    private ELDEvent.MalfunctionCode createCodeForMalfunction(ELDEvent eldEvent) {
        return eldEvent.getEventCode() == ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode() ?
                ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED :
                ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED;
    }

    @NonNull
    private ELDEvent createEvent(Malfunction malfunction, ELDEvent.MalfunctionCode malfunctionCode) {
        ELDEvent eldEvent = mELDEventsInteractor.getEvent(mDutyTypeManager.getDutyType());
        eldEvent.setMalCode(malfunction);
        eldEvent.setEventCode(malfunctionCode.getCode());
        eldEvent.setEventType(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue());
        return eldEvent;
    }

    private final static class SynchResult {
        final BlackBoxModel mBlackBoxModel;
        final ELDEvent mELDEvent;

        private SynchResult(BlackBoxModel blackBoxModel, ELDEvent eldEvent) {
            mBlackBoxModel = blackBoxModel;
            mELDEvent = eldEvent;
        }
    }
}
