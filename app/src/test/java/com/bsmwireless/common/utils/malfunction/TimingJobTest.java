package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.BaseTest;
import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimingJobTest extends BaseTest {
    @Mock
    ELDEventsInteractor mELDEventsInteractor;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    NtpClientManager mNtpClientManager;
    @Mock
    AppSettings mAppSettings;
    @Mock
    BlackBoxInteractor mBlackBoxInteractor;
    @Mock
    PreferencesManager mPreferencesManager;

    TimingJob mTimingJob;

    @Before
    public void setUp() throws Exception {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(mPreferencesManager.getBoxId()).thenReturn(0);
        when(mAppSettings.getIntervalForCheckTime()).thenReturn(0L);

        mTimingJob = spy(new TimingJob(mELDEventsInteractor, mDutyTypeManager, mNtpClientManager,
                mAppSettings, mBlackBoxInteractor, mPreferencesManager));
    }

    @Test
    public void complianceNotDetectedNoEventsInDb() throws Exception {

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.empty());

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(5);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered - 1);
        when(mAppSettings.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(new BlackBoxModel()));

        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);

        doReturn(Observable.just(1L)).when(mTimingJob).getIntervalObservable();

        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
    }

    @Test
    public void complianceDetectedNoEventsInDb() throws Exception {

        BlackBoxModel model = spy(new BlackBoxModel());
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(model));

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.empty());
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(1);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered + 1);
        when(mAppSettings.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        doReturn(Observable.just(1L)).when(mTimingJob).getIntervalObservable();

        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mELDEventsInteractor).getEvent(Malfunction.TIMING_COMPLIANCE,
                ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED, model);
    }

    @Test
    public void complianceDetectedEventLogged() throws Exception {

        ELDEvent eldEventFromDb = spy(ELDEvent.class);
        when(eldEventFromDb.getMalCode()).thenReturn(Malfunction.TIMING_COMPLIANCE);
        when(eldEventFromDb.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode());

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.just(eldEventFromDb));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(new BlackBoxModel()));

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(1);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered + 1);
        when(mAppSettings.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        doReturn(Observable.just(1L)).when(mTimingJob).getIntervalObservable();

        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
    }

    @Test
    public void complianceDetectedEventCleared() throws Exception {
        BlackBoxModel model = spy(new BlackBoxModel());
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(model));

        ELDEvent eldEventFromDb = spy(ELDEvent.class);
        when(eldEventFromDb.getMalCode()).thenReturn(Malfunction.TIMING_COMPLIANCE);
        when(eldEventFromDb.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.just(eldEventFromDb));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(new ELDEvent());

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(1);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered + 1);
        when(mAppSettings.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        doReturn(Observable.just(1L)).when(mTimingJob).getIntervalObservable();

        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mELDEventsInteractor).getEvent(Malfunction.TIMING_COMPLIANCE,
                ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED, model);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void complianceAppearedAndDisappeared() throws Exception {
        BlackBoxModel model = spy(new BlackBoxModel());

        when(mBlackBoxInteractor.getData(anyInt()))
                .thenReturn(Observable.just(model),
                        Observable.just(model));

        ELDEvent clearedEvent = spy(ELDEvent.class);
        when(clearedEvent.getMalCode()).thenReturn(Malfunction.TIMING_COMPLIANCE);
        when(clearedEvent.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());

        ELDEvent loggedEvent = spy(ELDEvent.class);
        when(loggedEvent.getMalCode()).thenReturn(Malfunction.TIMING_COMPLIANCE);
        when(loggedEvent.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode());

        ELDEvent newEldEvent = spy(ELDEvent.class);
        when(mELDEventsInteractor.getEvent(any(DutyType.class))).thenReturn(newEldEvent);
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.just(clearedEvent), Maybe.just(loggedEvent));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(new ELDEvent());

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(5);
        when(mNtpClientManager.getRealTimeInMillisDiff())
                .thenReturn(timeDiffForTriggered + 1, timeDiffForTriggered - 1);
        when(mAppSettings.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        Subject<Long> subject = BehaviorSubject.create();
        doReturn(subject).when(mTimingJob).getIntervalObservable();

        mTimingJob.start();
        subject.onNext(1L);
        subject.onNext(2L);
        subject.onComplete();
        mTimingJob.stop();

        verify(mELDEventsInteractor, times(2)).postNewELDEvent(any());
        verify(mELDEventsInteractor).getEvent(Malfunction.TIMING_COMPLIANCE,
                ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED, model);
        verify(mELDEventsInteractor).getEvent(Malfunction.TIMING_COMPLIANCE,
                ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED, model);
    }
}