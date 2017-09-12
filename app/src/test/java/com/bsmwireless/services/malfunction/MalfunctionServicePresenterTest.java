package com.bsmwireless.services.malfunction;

import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MalfunctionServicePresenterTest {

    @Mock
    BlackBoxConnectionManager mConnectionManager;
    @Mock
    ELDEventsInteractor mELDEventsInteractor;

    MalfunctionServicePresenter mMalfunctionServicePresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        mMalfunctionServicePresenter = new MalfunctionServicePresenter(mConnectionManager, mELDEventsInteractor);
    }

    @Test
    public void synchDiagnosticAppear() throws Exception {

        BlackBoxModel blackBoxModel = spy(new BlackBoxModel());
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_CABLE)).thenReturn(true);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.ECM_SYNC)).thenReturn(true);

        when(mConnectionManager.getDataObservable()).thenReturn(Observable.just(blackBoxModel));

        ELDEvent eldEvent = mock(ELDEvent.class);
        when(eldEvent.getEventCode()).thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(mELDEventsInteractor.getLatestMalfunctinoEvent(Malfunction.ENGINE_SYNCHRONIZATION))
                .thenReturn(Flowable.just(eldEvent));

        mMalfunctionServicePresenter.onCreate();

        verify(mELDEventsInteractor).postNewELDEvent(any());
    }
}