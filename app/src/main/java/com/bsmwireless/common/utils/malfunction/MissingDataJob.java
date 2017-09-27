package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.utils.observers.DutyManagerObservable;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.widgets.alerts.DutyType;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Job for detecting data diagnostic events according to paragraph 4.6.1.4 (d) of ELD Requirements
 */
public final class MissingDataJob extends BaseMalfunctionJob implements MalfunctionJob {

    @Inject
    public MissingDataJob(ELDEventsInteractor eldEventsInteractor,
                          DutyTypeManager dutyTypeManager,
                          BlackBoxInteractor blackBoxInteractor,
                          PreferencesManager preferencesManager) {
        super(eldEventsInteractor, dutyTypeManager, blackBoxInteractor, preferencesManager);
    }

    @Override
    public final void start() {
        Disposable disposable = createDutyTypeObservable()
                .observeOn(Schedulers.io())
                .filter(dutyType -> DutyType.DRIVING != dutyType)
                .flatMap(unused -> getELDEventsInteractor()
                        .isLocationUpdateEventExists()
                        .toObservable())
                .flatMap(isEventsExist -> getBlackboxData()
                        .flatMap(blackBoxModel -> loadLatest(blackBoxModel)
                                .map(eldEvent -> new BaseMalfunctionJob.Result(eldEvent, blackBoxModel)))
                        .map(result -> new Result(isEventsExist, result.mELDEvent, result.mBlackBoxModel)))
                .filter(this::isStateAndEventAreDifferent)
                .map(result -> createEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS,
                        createCodeForDiagnostic(result.mELDEvent),
                        result.mBlackBoxModel))
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
    public final void stop() {
        dispose();
    }

    @VisibleForTesting
    Observable<DutyType> createDutyTypeObservable() {
        return DutyManagerObservable.create(getDutyTypeManager());
    }

    private boolean isStateAndEventAreDifferent(Result result) {
        ELDEvent latestEvent = result.mELDEvent;
        if (result.isEventsForUpdateLocationExist) {
            return latestEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode();
        } else {
            return latestEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode();
        }
    }

    private Observable<ELDEvent> loadLatest(BlackBoxModel blackBoxModel) {
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS)
                .toObservable()
                .switchIfEmpty(observer -> switchToDefaultMalfunction(observer,
                        Malfunction.MISSING_REQUIRED_DATA_ELEMENTS,
                        ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED,
                        blackBoxModel));
    }

    private final static class Result {
        private final boolean isEventsForUpdateLocationExist;
        private final ELDEvent mELDEvent;
        private final BlackBoxModel mBlackBoxModel;

        private Result(boolean isEventsForUpdateLocationExist, ELDEvent eldEvent, BlackBoxModel blackBoxModel) {
            this.isEventsForUpdateLocationExist = isEventsForUpdateLocationExist;
            mELDEvent = eldEvent;
            mBlackBoxModel = blackBoxModel;
        }
    }
}
