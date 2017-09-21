package com.bsmwireless.common.utils.malfunction;

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

public final class MissingDataJob extends BaseMalfunctionJob implements MalfunctionJob {

    @Inject
    public MissingDataJob(ELDEventsInteractor eldEventsInteractor, DutyTypeManager dutyTypeManager) {
        super(eldEventsInteractor, dutyTypeManager);
    }

    @Override
    public final void start() {
        Disposable disposable = DutyManagerObservable.create(getDutyTypeManager())
                .filter(dutyType -> DutyType.DRIVING == dutyType)
                .flatMap(unused -> getELDEventsInteractor().isLocationUpdateEventExists().toObservable())
                .zipWith(loadLatest(), Result::new)
                .filter(this::isStateAndEventAreDifferent)
                .subscribeOn(Schedulers.io())
                .subscribe();
        add(disposable);
    }

    @Override
    public final void stop() {
        dispose();
    }

    private boolean isStateAndEventAreDifferent(Result result) {

        if (result.isEventsForUpdateLocationExist) {

        } else {

        }
        return true;
    }

    private Observable<ELDEvent> loadLatest() {
        return getELDEventsInteractor().getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS)
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

/*    private Observable<Long> createDiagnosticLoggedEvent() {

                .flatMap(eldEvent -> {
                    if (eldEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode()) {
                        ELDEvent event = createEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS,
                                ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED);
                        return saveEvents(event);
                    }
                    //already logged, just return a stub
                    return Observable.just(0L);
                });
    }*/
}
