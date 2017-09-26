package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
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

    private final BlackBoxInteractor mBlackBoxInteractor;
    private final PreferencesManager mPreferencesManager;

    @Inject
    public SynchronizationJob(ELDEventsInteractor eldEventsInteractor,
                              DutyTypeManager dutyTypeManager,
                              BlackBoxInteractor blackBoxInteractor, PreferencesManager preferencesManager) {
        super(eldEventsInteractor, dutyTypeManager, blackBoxInteractor, preferencesManager);
        mBlackBoxInteractor = blackBoxInteractor;
        mPreferencesManager = preferencesManager;
    }

    @Override
    public void start() {
        Timber.d("Start synchronization compliance detection");
        Disposable disposable = mBlackBoxInteractor.getData(mPreferencesManager.getBoxId())
                .filter(blackBoxModel -> BlackBoxResponseModel.ResponseType.STATUS_UPDATE
                        == blackBoxModel.getResponseType())
                .flatMap(this::loadLatestSynchronizationEvent, SynchResult::new)
                .filter(this::isStateAndEventAreDifferent)
                // create event with opposite mal code
                .map(result -> createEvent(Malfunction.ENGINE_SYNCHRONIZATION,
                        createCodeForDiagnostic(result.mELDEvent), result.mBlackBoxModel))
                .flatMap(this::saveEvents)
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
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

    private Observable<ELDEvent> loadLatestSynchronizationEvent(BlackBoxModel blackBoxModel) {
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION)
                .toObservable()
                .switchIfEmpty(observer -> {
                    // create default event with cleared status
                    ELDEvent eldEvent = getELDEventsInteractor()
                            .getEvent(Malfunction.ENGINE_SYNCHRONIZATION,
                                    ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED,
                                    blackBoxModel);
                    observer.onNext(eldEvent);
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
