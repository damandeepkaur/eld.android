package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.NonNull;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

abstract class BaseMalfunctionJob {

    private final ELDEventsInteractor mELDEventsInteractor;
    private final CompositeDisposable mCompositeDisposable;
    private final DutyTypeManager mDutyTypeManager;
    private final BlackBoxInteractor mBlackBoxInteractor;
    private final PreferencesManager mPreferencesManager;

    BaseMalfunctionJob(ELDEventsInteractor eldEventsInteractor,
                       DutyTypeManager dutyTypeManager,
                       BlackBoxInteractor blackBoxInteractor,
                       PreferencesManager preferencesManager) {
        mELDEventsInteractor = eldEventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mBlackBoxInteractor = blackBoxInteractor;
        mPreferencesManager = preferencesManager;
        mCompositeDisposable = new CompositeDisposable();
    }

    protected final void add(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    protected final void dispose() {
        mCompositeDisposable.dispose();
    }

    protected final Observable<Long> saveEvents(ELDEvent eldEvent) {
        return mELDEventsInteractor.postNewELDEvent(eldEvent).toObservable();
    }

    protected final ELDEventsInteractor getELDEventsInteractor() {
        return mELDEventsInteractor;
    }

    protected final DutyTypeManager getDutyTypeManager() {
        return mDutyTypeManager;
    }

    /**
     * Creates event with an opposite malfunction code
     *
     * @param eldEvent
     * @return
     */
    protected final ELDEvent.MalfunctionCode createCodeForDiagnostic(ELDEvent eldEvent) {
        return eldEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode() ?
                ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED :
                ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED;
    }

    /**
     * Creates event with an opposite malfunction code
     *
     * @param eldEvent
     * @return
     */
    protected final ELDEvent.MalfunctionCode createCodeForMalfunction(ELDEvent eldEvent) {
        return eldEvent.getEventCode() == ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode() ?
                ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED :
                ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED;
    }

    /**
     * Creates a default {@link ELDEvent} and put it into observable
     * @param observer observer
     * @param malfunction malfunction type for this event
     * @param malfunctionCode malfunction code for this event
     * @param blackBoxModel data from blackbox
     */
    protected final void switchToDefaultMalfunction(Observer<? super ELDEvent> observer,
                                                    Malfunction malfunction,
                                                    ELDEvent.MalfunctionCode malfunctionCode,
                                                    BlackBoxModel blackBoxModel) {
        ELDEvent eldEvent = createEvent(malfunction, malfunctionCode, blackBoxModel);
        observer.onNext(eldEvent);
    }

    protected void switchToDefaultDiagnosticCleared(Observer<? super ELDEvent> observer){
        ELDEvent event = getELDEventsInteractor().getEvent(getDutyTypeManager().getDutyType());
        event.setEventCode(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        observer.onNext(event);
    }

    /**
     * Creates an {@link ELDEvent}
     * @param malfunction malfunction type for this event
     * @param malfunctionCode malfunction code for this event
     * @param blackBoxModel data from blackbox
     * @return corret event
     */
    @NonNull
    protected final ELDEvent createEvent(Malfunction malfunction,
                                         ELDEvent.MalfunctionCode malfunctionCode,
                                         BlackBoxModel blackBoxModel) {
        return mELDEventsInteractor.getEvent(malfunction, malfunctionCode, blackBoxModel);
    }

    /**
     * Load data from BlackBox
     * @return
     */
    protected final Observable<BlackBoxModel> getBlackboxData() {
        return mBlackBoxInteractor
                .getData(mPreferencesManager.getBoxId())
                .onErrorReturn(throwable -> new BlackBoxModel())
                .switchIfEmpty(observer -> observer.onNext(new BlackBoxModel()));
    }

    static final class Result {
        final ELDEvent mELDEvent;
        final BlackBoxModel mBlackBoxModel;

        Result(ELDEvent eldEvent, BlackBoxModel blackBoxModel) {
            mELDEvent = eldEvent;
            mBlackBoxModel = blackBoxModel;
        }
    }
}
