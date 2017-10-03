package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.BaseTest;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MissingDataJobTest extends BaseTest {

    @Mock
    ELDEventsInteractor mELDEventsInteractor;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    BlackBoxInteractor mBlackBoxInteractor;
    @Mock
    PreferencesManager mPreferencesManager;

    MissingDataJob mMissingDataJob;

    @Before
    public void setUp() throws Exception {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(mPreferencesManager.getBoxId()).thenReturn(0);

        mMissingDataJob = spy(new MissingDataJob(mELDEventsInteractor, mDutyTypeManager,
                mBlackBoxInteractor, mPreferencesManager));
    }

    @Test
    public void diagnosticDetectedFirstTime() throws Exception {

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.isLocationUpdateEventExists()).thenReturn(Single.just(true));
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS))
                .thenReturn(Maybe.empty());
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        ELDEvent eldEvent = mock(ELDEvent.class);
        when(eldEvent.getEventCode()).thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(DutyType.class)))
                .thenReturn(eldEvent);
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);

        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(new BlackBoxModel()));

        BehaviorSubject<DutyType> subject = BehaviorSubject.create();
        doReturn(subject).when(mMissingDataJob).createDutyTypeObservable();

        mMissingDataJob.start();
        subject.onNext(DutyType.ON_DUTY);
        mMissingDataJob.stop();
        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mELDEventsInteractor)
                .getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS);
        verify(mELDEventsInteractor, atLeastOnce()).getEvent(any(), any(), any());
    }

    @Test
    public void diagnosticNotDetected() throws Exception {

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.isLocationUpdateEventExists()).thenReturn(Single.just(false));
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS))
                .thenReturn(Maybe.empty());
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(new BlackBoxModel()));

        BehaviorSubject<DutyType> subject = BehaviorSubject.create();
        doReturn(subject).when(mMissingDataJob).createDutyTypeObservable();

        mMissingDataJob.start();
        subject.onNext(DutyType.ON_DUTY);
        mMissingDataJob.stop();
        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
        verify(mELDEventsInteractor)
                .getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS);
        verify(mELDEventsInteractor, atLeastOnce()).getEvent(any(), any(), any());
    }

    @Test
    public void diagnosticDetectedCurrentCleared() throws Exception {

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.isLocationUpdateEventExists()).thenReturn(Single.just(true));
        ELDEvent currentEvent = mock(ELDEvent.class);
        when(currentEvent.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS))
                .thenReturn(Maybe.just(currentEvent));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        ELDEvent eldEvent = mock(ELDEvent.class);
        when(mELDEventsInteractor.getEvent(any(DutyType.class)))
                .thenReturn(eldEvent);
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);

        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(new BlackBoxModel()));

        BehaviorSubject<DutyType> subject = BehaviorSubject.create();
        doReturn(subject).when(mMissingDataJob).createDutyTypeObservable();

        mMissingDataJob.start();
        subject.onNext(DutyType.ON_DUTY);
        mMissingDataJob.stop();
        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mELDEventsInteractor)
                .getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS);
        verify(mELDEventsInteractor, atLeastOnce()).getEvent(any(), any(), any());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void diagnosticDetectedAndClearedCurrentCleared() throws Exception {

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.isLocationUpdateEventExists())
                .thenReturn(Single.just(true), Single.just(false));
        ELDEvent cleared = mock(ELDEvent.class);
        when(cleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        ELDEvent logged = mock(ELDEvent.class);
        when(logged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS))
                .thenReturn(Maybe.just(cleared), Maybe.just(logged));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        ELDEvent eldEvent = mock(ELDEvent.class);
        when(mELDEventsInteractor.getEvent(any(DutyType.class)))
                .thenReturn(eldEvent);
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);

        BehaviorSubject<DutyType> subject = BehaviorSubject.create();
        doReturn(subject).when(mMissingDataJob).createDutyTypeObservable();

        mMissingDataJob.start();
        subject.onNext(DutyType.ON_DUTY);
        subject.onNext(DutyType.OFF_DUTY);
        mMissingDataJob.stop();
        verify(mELDEventsInteractor, times(2)).postNewELDEvent(any());
        verify(mELDEventsInteractor, atLeastOnce())
                .getLatestMalfunctionEvent(Malfunction.MISSING_REQUIRED_DATA_ELEMENTS);
        verify(mELDEventsInteractor, atLeastOnce()).getEvent(any(), any(), any());
    }
}