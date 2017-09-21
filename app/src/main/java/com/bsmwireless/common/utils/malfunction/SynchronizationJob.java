package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class SynchronizationJob extends BaseMalfunctionJob implements MalfunctionJob {

    private final BlackBoxConnectionManager mBoxConnectionManager;

    @Inject
    public SynchronizationJob(ELDEventsInteractor eldEventsInteractor, DutyTypeManager dutyTypeManager, BlackBoxConnectionManager boxConnectionManager) {
        super(eldEventsInteractor, dutyTypeManager);
        mBoxConnectionManager = boxConnectionManager;
    }

    @Override
    public void start() {
        Timber.d("Start synchronization compliance detection");
        Disposable disposable = mBoxConnectionManager.getDataObservable()
                .filter(blackBoxModel -> BlackBoxResponseModel.ResponseType.STATUS_UPDATE
                        == blackBoxModel.getResponseType())
                .flatMap(blackBoxModel -> loadLatestSynchronizationEvent(), SynchResult::new)
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
        add(disposable);
    }

    @Override
    public void stop() {
        Timber.d("Start synchronization compliance detection");
        dispose();
    }

    private Observable<ELDEvent> loadLatestSynchronizationEvent() {
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION)
                .toObservable()
                .switchIfEmpty(observer -> {
                    // create default event with cleared status
                    ELDEvent event = getELDEventsInteractor().getEvent(getDutyTypeManager().getDutyType());
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

        boolean isEcmOk = synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)
                && synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC);
        if (isEcmOk) {
            return ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode() == synchResult.mELDEvent.getEventCode();
        } else {
            return ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode() == synchResult.mELDEvent.getEventCode();
        }
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
