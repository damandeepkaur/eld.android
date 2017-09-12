package com.bsmwireless.services.malfunction;

import android.support.annotation.NonNull;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class MalfunctionServicePresenter {

    final BlackBoxConnectionManager mBlackBoxConnectionManager;
    final ELDEventsInteractor mELDEventsInteractor;
    private final CompositeDisposable mCompositeDisposable;

    @Inject
    public MalfunctionServicePresenter(BlackBoxConnectionManager blackBoxConnectionManager,
                                       ELDEventsInteractor eldEventsInteractor) {
        mBlackBoxConnectionManager = blackBoxConnectionManager;
        mELDEventsInteractor = eldEventsInteractor;
        mCompositeDisposable = new CompositeDisposable();
    }

    public void onCreate() {
        startSynchronizationMonitoring();
    }

    public void onDestroy() {
        mCompositeDisposable.dispose();
    }

    private Observable<BlackBoxModel> getBaseObservable() {
        return mBlackBoxConnectionManager.getDataObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    private void startSynchronizationMonitoring() {
        Disposable disposable = Flowable
                .combineLatest(getBaseObservable().toFlowable(BackpressureStrategy.LATEST),
                        mELDEventsInteractor.getLatestMalfunctinoEvent(Malfunction.ENGINE_SYNCHRONIZATION),
                        SynchResult::new)
                .filter(this::isStateAndEventAreDifferent)
                .map(synchResult -> createSynchDiagnosticEvent(synchResult.mBlackBoxModel))
                .flatMap(eldEvent -> mELDEventsInteractor.postNewELDEvent(eldEvent).toFlowable())
                .onErrorReturn(throwable -> {
                    Timber.e(throwable, "Error handle synchronization event");
                    return -1L;
                })
                .subscribe();
        mCompositeDisposable.add(disposable);
    }

    /**
     * Checks current black box state and latest event for Synchronization monitoring in DB and
     * return true if they are different.
     * For example, if current sensor state is in ECM CABLE CONNECTED state and current event
     * in a database for this state is in DISCONNECTED state {@code true} will be returned
     * @param synchResult
     * @return
     */
    private boolean isStateAndEventAreDifferent(SynchResult synchResult){
        return (synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)
                || synchResult.mBlackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC))
                && synchResult.mELDEvent.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode();
    }

    @NonNull
    private ELDEvent createSynchDiagnosticEvent(BlackBoxModel blackBoxModel) {
        ELDEvent eldEvent = new ELDEvent();
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
