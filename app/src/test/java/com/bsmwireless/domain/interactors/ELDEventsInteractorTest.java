package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.models.ELDEvent;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for ELDEventsInteractor.
 */
@RunWith(MockitoJUnitRunner.class)
public class ELDEventsInteractorTest {


    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    private ServiceApi mServiceApi;

    @Mock
    private PreferencesManager mPreferencesManager;

    @Mock
    private AppDatabase mAppDatabase;

    @Mock
    private UserInteractor mUserInteractor;

    @Mock
    private BlackBoxInteractor mBlackBoxInteractor;

    @Mock
    private DutyManager mDutyManager;

    @Mock
    private ELDEventDao mEldEventDao;


    private ELDEventsInteractor mEldEventsInteractor;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mAppDatabase.ELDEventDao()).thenReturn(mEldEventDao);

        mEldEventsInteractor = new ELDEventsInteractor(mServiceApi, mPreferencesManager,
                mAppDatabase, mUserInteractor, mBlackBoxInteractor, mDutyManager);

    }

    // TODO: getELDEvents

    @Test
    public void testGetDutyEventsFromDbSuccess() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mPreferencesManager.getDriverId()).thenReturn(12345);
        when(mEldEventDao.getDutyEventsFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.just(eldEventEntities));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        verify(mPreferencesManager).getDriverId();
        verify(mEldEventDao).getDutyEventsFromStartToEndTime(eq(startTime), eq(endTime), anyInt());
    }

    @Test
    public void testGetDutyEventsFromDbError() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        Exception fakeDbException = new RuntimeException("deadlock");

        when(mPreferencesManager.getDriverId()).thenReturn(12345);
        when(mEldEventDao.getDutyEventsFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.error(fakeDbException));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        testSubscriber.assertError(Throwable.class);
    }

    @Test
    public void testGetLatestActiveDutyEventFromDb() {
        // given
        long latestTime = 1503963474;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mEldEventDao.getLatestActiveDutyEvent(anyLong(), anyInt()))
                .thenReturn(Flowable.just(eldEventEntities));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getLatestActiveDutyEventFromDB(latestTime).subscribe(testSubscriber);

        // then
        verify(mPreferencesManager).getDriverId();
        verify(mEldEventDao).getLatestActiveDutyEvent(eq(latestTime), anyInt());
    }

    @Test
    public void testGetLatestActiveDutyEventFromDbError() {
        // given
        long latestTime = 1503963474;
        Exception fakeDbException = new RuntimeException("fake db exception");

        when(mEldEventDao.getLatestActiveDutyEvent(anyLong(), anyInt()))
                .thenReturn(Flowable.error(fakeDbException));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getLatestActiveDutyEventFromDB(latestTime).subscribe(testSubscriber);

        // then
        testSubscriber.assertError(Throwable.class);
    }

    @Test
    public void testGetActiveDutyEventsFromDb() {
        // given
        long startTime = 10000;
        long endTime = 20000;

        List<ELDEventEntity> eldEvents = new ArrayList<>();

        when(mPreferencesManager.getDriverId()).thenReturn(1234);
        when(mEldEventDao.getActiveDutyEventsAndFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.just(eldEvents));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getActiveDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        verify(mPreferencesManager).getDriverId();
        verify(mEldEventDao).getActiveDutyEventsAndFromStartToEndTime(eq(startTime), eq(endTime), anyInt());
    }

    @Test
    public void testGetActiveDutyEventsFromDbError() {
        // given
        long startTime = 10000;
        long endTime = 20000;

        Exception fakeDbException = new RuntimeException("no.");

        when(mPreferencesManager.getDriverId()).thenReturn(1234);
        when(mEldEventDao.getActiveDutyEventsAndFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.error(fakeDbException));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getActiveDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        testSubscriber.assertError(fakeDbException);
    }

    // TODO: getActiveEventsFromDBSync success
    // TODO: getActiveEventsFromDBSync error

}
