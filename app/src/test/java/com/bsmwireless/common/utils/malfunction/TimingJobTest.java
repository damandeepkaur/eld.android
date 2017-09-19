package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.BaseTest;
import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
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
    SettingsManager mSettingsManager;

    TimingJob mTimingJob;

    @Before
    public void setUp() throws Exception {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(mSettingsManager.getIntervalForCheckTime()).thenReturn(0L);

        mTimingJob = spy(new TimingJob(mELDEventsInteractor, mDutyTypeManager, mNtpClientManager,
                mSettingsManager));
    }

    @Test
    public void complianceNotDetectedNoEventsInDb() throws Exception {


        ELDEvent eldEvent = spy(ELDEvent.class);

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.getEvent(any(DutyType.class))).thenReturn(eldEvent);
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.empty());

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(5);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered - 1);
        when(mSettingsManager.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);
        when(mTimingJob.getIntervalObservable()).thenReturn(Observable.just(1L));


        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
    }

    @Test
    public void complianceDetectedNoEventsInDb() throws Exception {

        ELDEvent eldEvent = spy(ELDEvent.class);

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.getEvent(any(DutyType.class))).thenReturn(eldEvent);
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.empty());
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(1);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered + 1);
        when(mSettingsManager.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        when(mTimingJob.getIntervalObservable()).thenReturn(Observable.just(1L));

        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor).postNewELDEvent(any());
    }

    @Test
    public void complianceDetectedEventLogged() throws Exception {

        ELDEvent eldEventFromDb = spy(ELDEvent.class);
        when(eldEventFromDb.getMalCode()).thenReturn(Malfunction.TIMING_COMPLIANCE);
        when(eldEventFromDb.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode());

        ELDEvent newEldEvent = spy(ELDEvent.class);

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.getEvent(any(DutyType.class))).thenReturn(newEldEvent);
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.just(eldEventFromDb));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(1);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered + 1);
        when(mSettingsManager.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        when(mTimingJob.getIntervalObservable()).thenReturn(Observable.just(1L));

        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
    }

    @Test
    public void complianceDetectedEventCleared() throws Exception {

        ELDEvent eldEventFromDb = spy(ELDEvent.class);
        when(eldEventFromDb.getMalCode()).thenReturn(Malfunction.TIMING_COMPLIANCE);
        when(eldEventFromDb.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());

        ELDEvent newEldEvent = spy(ELDEvent.class);

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        when(mELDEventsInteractor.getEvent(any(DutyType.class))).thenReturn(newEldEvent);
        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.TIMING_COMPLIANCE))
                .thenReturn(Maybe.just(eldEventFromDb));
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(1);
        when(mNtpClientManager.getRealTimeInMillisDiff()).thenReturn(timeDiffForTriggered + 1);
        when(mSettingsManager.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        when(mTimingJob.getIntervalObservable()).thenReturn(Observable.just(1L));

        mTimingJob.start();
        mTimingJob.stop();

        verify(mELDEventsInteractor).postNewELDEvent(any());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void complianceAppearedAndDisappeared() throws Exception {

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

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);

        final long timeDiffForTriggered = TimeUnit.MILLISECONDS.toMillis(5);
        when(mNtpClientManager.getRealTimeInMillisDiff())
                .thenReturn(timeDiffForTriggered + 1, timeDiffForTriggered - 1);
        when(mSettingsManager.getTimingMalfunctionDiff()).thenReturn(timeDiffForTriggered);

        Subject<Long> subject = BehaviorSubject.create();
        when(mTimingJob.getIntervalObservable()).thenReturn(subject);

        mTimingJob.start();
        subject.onNext(1L);
        subject.onNext(2L);
        subject.onComplete();
        mTimingJob.stop();

        verify(mELDEventsInteractor, times(2)).postNewELDEvent(any());
    }
}