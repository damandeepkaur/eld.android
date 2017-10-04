package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.BaseTest;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SynchronizationJobTest extends BaseTest {

    @Mock
    BlackBoxInteractor mBlackBoxInteractor;
    @Mock
    ELDEventsInteractor mELDEventsInteractor;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    PreferencesManager mPreferencesManager;

    SynchronizationJob mSynchronizationJob;

    @Before
    public void setUp() throws Exception {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        mSynchronizationJob = new SynchronizationJob(mELDEventsInteractor, mDutyTypeManager,
                mBlackBoxInteractor, mPreferencesManager);
    }

    @Test
    public void synchDiagnosticAppear() throws Exception {

        BlackBoxModel blackBoxModel = spy(new BlackBoxModel());
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)).thenReturn(true);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC)).thenReturn(true);
        when(blackBoxModel.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.STATUS_UPDATE);

        when(mPreferencesManager.getBoxId()).thenReturn(1);
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(blackBoxModel));

        ELDEvent eldEvent = mock(ELDEvent.class);
        when(eldEvent.getEventCode()).thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(eldEvent.getMalCode()).thenReturn(Malfunction.ENGINE_SYNCHRONIZATION);

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION))
                .thenReturn(Single.just(eldEvent).toFlowable());
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(eldEvent);

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        mSynchronizationJob.start();

        mSynchronizationJob.stop();
        verify(mELDEventsInteractor).getEvent(Malfunction.ENGINE_SYNCHRONIZATION,
                ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED,
                blackBoxModel);
        verify(mELDEventsInteractor).postNewELDEvent(any(ELDEvent.class));
    }

    @Test
    public void synchDiagnosticAppearWithAlreadyExist() throws Exception {

        BlackBoxModel blackBoxModel = spy(new BlackBoxModel());
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)).thenReturn(false);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC)).thenReturn(false);

        when(mPreferencesManager.getBoxId()).thenReturn(1);
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(blackBoxModel));

        ELDEvent eldEvent = mock(ELDEvent.class);
        when(eldEvent.getEventCode()).thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION))
                .thenReturn(Flowable.just(eldEvent));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        mSynchronizationJob.start();

        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
    }

    @Test
    public void synchDiagnosticAppearAndDisappear() throws Exception {
        BlackBoxModel diagnosticAppear = spy(new BlackBoxModel());
        when(diagnosticAppear.getSensorState(BlackBoxSensorState.ECM_CABLE)).thenReturn(false);
        when(diagnosticAppear.getSensorState(BlackBoxSensorState.ECM_SYNC)).thenReturn(false);
        when(diagnosticAppear.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.STATUS_UPDATE);

        BlackBoxModel diagnosticDisappear = spy(new BlackBoxModel());
        when(diagnosticDisappear.getSensorState(BlackBoxSensorState.ECM_CABLE)).thenReturn(true);
        when(diagnosticDisappear.getSensorState(BlackBoxSensorState.ECM_SYNC)).thenReturn(true);
        when(diagnosticDisappear.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.STATUS_UPDATE);

        Subject<BlackBoxModel> blackBoxObservable = PublishSubject.create();
        when(mPreferencesManager.getBoxId()).thenReturn(1);
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(blackBoxObservable);

        ELDEvent diagnosticLogged = mock(ELDEvent.class);
        when(diagnosticLogged.getEventCode()).thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        ELDEvent diagnosticCleared = mock(ELDEvent.class);
        when(diagnosticCleared.getEventCode()).thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());

        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(new ELDEvent());

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        mSynchronizationJob.start();

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION))
                .thenReturn(Flowable.just(diagnosticCleared));
        blackBoxObservable.onNext(diagnosticAppear);

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION))
                .thenReturn(Flowable.just(diagnosticLogged));
        blackBoxObservable.onNext(diagnosticDisappear);

        blackBoxObservable.onComplete();
        mSynchronizationJob.stop();

        verify(mELDEventsInteractor, times(2)).postNewELDEvent(any());
    }

    @Test
    public void synchDiagnosticAppearNoEventInDb() throws Exception {

        BlackBoxModel blackBoxModel = spy(new BlackBoxModel());
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)).thenReturn(false);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC)).thenReturn(false);
        when(blackBoxModel.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.STATUS_UPDATE);

        when(mPreferencesManager.getBoxId()).thenReturn(1);
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(blackBoxModel));

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION))
                .thenReturn(Flowable.empty());

        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        mSynchronizationJob.start();
        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mELDEventsInteractor).getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION);
    }

    @Test
    public void synchDiagnosticDisappearNoEventInDb() throws Exception {

        BlackBoxModel blackBoxModel = spy(new BlackBoxModel());
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)).thenReturn(true);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC)).thenReturn(true);
        when(blackBoxModel.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.STATUS_UPDATE);

        when(mPreferencesManager.getBoxId()).thenReturn(1);
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(blackBoxModel));

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION))
                .thenReturn(Flowable.empty());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(new ELDEvent());

        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        mSynchronizationJob.start();
        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
        verify(mELDEventsInteractor).getLatestMalfunctionEvent(Malfunction.ENGINE_SYNCHRONIZATION);
    }

}