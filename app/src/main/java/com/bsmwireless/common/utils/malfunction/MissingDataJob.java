package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.VisibleForTesting;

import com.bsmwireless.common.utils.observers.DutyManagerObservable;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
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
    public MissingDataJob(ELDEventsInteractor eldEventsInteractor, DutyTypeManager dutyTypeManager) {
        super(eldEventsInteractor, dutyTypeManager);
    }

    @Override
    public final void start() {
        Disposable disposable = createDutyTypeObservable()
                .filter(dutyType -> DutyType.DRIVING != dutyType)
                .flatMap(unused -> getELDEventsInteractor().isLocationUpdateEventExists()
                        .toObservable())
                .zipWith(loadLatest(), Result::new)
                .filter(this::isStateAndEventAreDifferent)
                .map(result -> createEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS,
                        createCodeForDiagnostic(result.latestEvent)))
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
    Observable<DutyType> createDutyTypeObservable(){
        return DutyManagerObservable.create(getDutyTypeManager());
    }

    private boolean isStateAndEventAreDifferent(Result result) {


        ELDEvent latestEvent = result.latestEvent;
        if (result.isEventsForUpdateLocationExist) {
            return latestEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode();
        } else {
            return latestEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode();
        }
    }

    private Observable<ELDEvent> loadLatest() {
        return getELDEventsInteractor()
                .getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS)
                .toObservable()
                .switchIfEmpty(this::switchToDefaultDiagnosticCleared);
    }

    private final static class Result {
        private final boolean isEventsForUpdateLocationExist;
        private final ELDEvent latestEvent;

        private Result(boolean isEventsForUpdateLocationExist, ELDEvent latestEvent) {
            this.isEventsForUpdateLocationExist = isEventsForUpdateLocationExist;
            this.latestEvent = latestEvent;
        }
    }
}
