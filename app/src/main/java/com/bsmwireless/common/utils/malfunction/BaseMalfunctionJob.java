package com.bsmwireless.common.utils.malfunction;

import android.support.annotation.NonNull;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

abstract class BaseMalfunctionJob {

    private final ELDEventsInteractor mELDEventsInteractor;
    private final CompositeDisposable mCompositeDisposable;
    private final DutyTypeManager mDutyTypeManager;

    BaseMalfunctionJob(ELDEventsInteractor eldEventsInteractor, DutyTypeManager dutyTypeManager) {
        mELDEventsInteractor = eldEventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mCompositeDisposable = new CompositeDisposable();
    }

    protected final void add(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    protected final void dispose(){
        mCompositeDisposable.dispose();
    }

    protected final Observable<Long> saveEvents(ELDEvent eldEvent) {
        return mELDEventsInteractor.postNewELDEvent(eldEvent).toObservable();
    }

    protected final ELDEventsInteractor getELDEventsInteractor(){
        return mELDEventsInteractor;
    }

    protected final DutyTypeManager getDutyTypeManager(){
        return mDutyTypeManager;
    }

    protected final ELDEvent.MalfunctionCode createCodeForDiagnostic(ELDEvent eldEvent) {
        return eldEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode() ?
                ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED :
                ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED;
    }

    protected final ELDEvent.MalfunctionCode createCodeForMalfunction(ELDEvent eldEvent) {
        return eldEvent.getEventCode() == ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode() ?
                ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED :
                ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED;
    }

    @NonNull
    protected final ELDEvent createEvent(Malfunction malfunction, ELDEvent.MalfunctionCode malfunctionCode) {
        ELDEvent eldEvent = mELDEventsInteractor.getEvent(mDutyTypeManager.getDutyType());
        eldEvent.setMalCode(malfunction);
        eldEvent.setEventCode(malfunctionCode.getCode());
        eldEvent.setEventType(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue());
        return eldEvent;
    }
}
