package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Matchers.any;
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

    private final String mfakeTimezone = "fake timezone";
    private final long MS_IN_DAY = 24*60*60*1000;


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
    private DutyTypeManager mDutyTypeManager;

    @Mock
    private ELDEventDao mEldEventDao;

    @Mock
    private AccountManager mAccountManager;

    @Mock
    private TokenManager mTokenManager;

    @Mock
    private LogSheetInteractor mLogSheetInteractor;


    private ELDEventsInteractor mEldEventsInteractor;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mAppDatabase.ELDEventDao()).thenReturn(mEldEventDao);
        when(mUserInteractor.getTimezone()).thenReturn(Flowable.just(mfakeTimezone));

        mEldEventsInteractor = new ELDEventsInteractor(mServiceApi, mPreferencesManager,
                mAppDatabase, mUserInteractor, mBlackBoxInteractor, mDutyTypeManager,
                mAccountManager, mTokenManager, mLogSheetInteractor);

    }

    @Test
    public void testGetEventsFromDBOnceSuccess() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        List<ELDEventEntity> eldEvents = new ArrayList<>();

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();
        when(mAccountManager.getCurrentUserId()).thenReturn(111111);
        when(mEldEventDao.getEventsFromStartToEndTimeOnce(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.just(eldEvents));

        // when
        mEldEventsInteractor.getEventsFromDBOnce(startTime, endTime).subscribe(testObserver);

        // then
        verify(mAccountManager).getCurrentUserId();
        verify(mEldEventDao).getEventsFromStartToEndTimeOnce(eq(startTime), eq(endTime), anyInt());
    }

    @Test
    public void testGetEventsFromDBOnceError() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        List<ELDEventEntity> eldEvents = new ArrayList<>();

        Throwable error = new RuntimeException("db broken");

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();
        when(mAccountManager.getCurrentUserId()).thenReturn(111111);
        when(mEldEventDao.getEventsFromStartToEndTimeOnce(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.error(error));

        // when
        mEldEventsInteractor.getEventsFromDBOnce(startTime, endTime).subscribe(testObserver);

        // then
        testObserver.assertError(error); // error is propagated
    }

    @Test
    public void testGetDutyEventsFromDbSuccess() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mEldEventDao.getDutyEventsFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.just(eldEventEntities));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        verify(mEldEventDao).getDutyEventsFromStartToEndTime(eq(startTime), eq(endTime), anyInt());
    }

    @Test
    public void testGetDutyEventsFromDbError() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        Exception fakeDbException = new RuntimeException("deadlock");

        when(mAccountManager.getCurrentUserId()).thenReturn(12345);
        when(mEldEventDao.getDutyEventsFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.error(fakeDbException));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mEldEventsInteractor.getDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        testSubscriber.assertError(Throwable.class);
    }

    @Test
    public void testGetLatestActiveDutyEventFromDbSync() {
        // given
        long latestTime = 1515151515;
        int userId = 1234;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mEldEventDao.getLatestActiveDutyEventSync(anyLong(), anyInt()))
                .thenReturn(eldEventEntities);

        // when
        mEldEventsInteractor.getLatestActiveDutyEventFromDBSync(latestTime, userId);

        // then
        verify(mEldEventDao).getLatestActiveDutyEventSync(eq(latestTime), anyInt());
    }

    @Test
    public void testGetLatestActiveDutyEventFromDBOnce() {
        // given
        long latestTime = 1515151515;
        int userId = 1234;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();
        when(mEldEventDao.getLatestActiveDutyEventOnce(anyLong(), anyInt()))
                .thenReturn(Single.just(eldEventEntities));

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mEldEventsInteractor.getLatestActiveDutyEventFromDBOnce(latestTime, userId)
                .subscribe(testObserver);

        // then
        verify(mEldEventDao).getLatestActiveDutyEventOnce(eq(latestTime), eq(userId));
    }

    @Test
    public void testGetLatestActiveDutyEventFromDBError() {
        // given
        long latestTime = 1515151515;
        int userId = 1234;

        Throwable error = new RuntimeException("db broken");
        when(mEldEventDao.getLatestActiveDutyEventOnce(anyLong(), anyInt()))
                .thenReturn(Single.error(error));

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mEldEventsInteractor.getLatestActiveDutyEventFromDBOnce(latestTime, userId)
                .subscribe(testObserver);

        // then
        verify(mEldEventDao).getLatestActiveDutyEventOnce(eq(latestTime), eq(userId));
        testObserver.assertError(error); // error propagated
    }

    @Test
    public void testGetActiveEventsFromDbSync() {
        // given
        long startTime = 10000L;
        long endTime = 20000L;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mEldEventDao.getActiveEventsFromStartToEndTimeSync(anyLong(), anyLong(), anyInt()))
                .thenReturn(eldEventEntities);

        // when
        mEldEventsInteractor.getActiveEventsFromDBSync(startTime, endTime);

        // then
        verify(mEldEventDao).getActiveEventsFromStartToEndTimeSync(eq(startTime), eq(endTime), anyInt());
    }

    @Test
    public void testGetDutyEventsForDay() {
        // given
        long startDayTime = 10000000L;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mEldEventDao.getDutyEventsFromStartToEndTimeSync(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.just(eldEventEntities));

        when(mAccountManager.getCurrentUserId()).thenReturn(1337);

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mEldEventsInteractor.getDutyEventsForDay(startDayTime).subscribe(testObserver);

        // then
        verify(mEldEventDao).getDutyEventsFromStartToEndTimeSync(eq(startDayTime),
                eq(startDayTime + MS_IN_DAY), anyInt());
    }

    @Test
    public void testGetDutyEventsForDayError() {
        // given
        long startDayTime = 10000000L;

        Throwable error = new RuntimeException("error from dao");

        when(mEldEventDao.getDutyEventsFromStartToEndTimeSync(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.error(error));

        when(mAccountManager.getCurrentUserId()).thenReturn(1337);

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mEldEventsInteractor.getDutyEventsForDay(startDayTime).subscribe(testObserver);

        // then
        testObserver.assertValue(new Predicate<List<ELDEvent>>() {
            @Override
            public boolean test(@NonNull List<ELDEvent> eldEvents) throws Exception {
                return eldEvents.size() == 0; // empty list is returned upon error, error is not propagated
            }
        });
    }

    @Test
    public void testGetLatestActiveDutyEventFromDBEmptyList() {
        // given
        long startDayTime = 111111111L;
        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mEldEventDao.getLatestActiveDutyEventSync(anyLong(), anyInt())).thenReturn(eldEventEntities);
        when(mAccountManager.getCurrentUserId()).thenReturn(123581220);

        // when
        ELDEvent eldEvent = mEldEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime);

        // then
        assertNull(eldEvent);
        verify(mEldEventDao).getLatestActiveDutyEventSync(eq(startDayTime), anyInt());
    }

    @Test
    public void testGetLatestActiveDutyEventFromDB() {
        // given
        long startDayTime = 111111111L;
        List<ELDEventEntity> eldEventEntities = new ArrayList<>();
        ELDEventEntity entity1 = new ELDEventEntity();

        eldEventEntities.add(entity1);

        when(mEldEventDao.getLatestActiveDutyEventSync(anyLong(), anyInt())).thenReturn(eldEventEntities);
        when(mAccountManager.getCurrentUserId()).thenReturn(123581220);

        // when
        ELDEvent eldEvent = mEldEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime);

        // then
        assertNotNull(eldEvent);
        verify(mEldEventDao).getLatestActiveDutyEventSync(eq(startDayTime), anyInt());
    }

    @Test
    public void testUpdateEldEvents() {
        // given
        long[] rowIdsInserted = {12345L};
        List<ELDEvent> events = new ArrayList<>();

        TestObserver<long[]> testObserver = TestObserver.create();
        when(mEldEventDao.insertAll(any())).thenReturn(rowIdsInserted);

        // when
        mEldEventsInteractor.updateELDEvents(events).subscribe(testObserver);

        // then
        verify(mEldEventDao).insertAll(Matchers.<ELDEventEntity>anyVararg());
        verify(mLogSheetInteractor).resetLogSheetHeaderSigning(eq(events));

        // TODO: verify SyncType.UPDATE_UNSYNC
    }

    @Test
    public void testPostNewEldEvent() {
        // given
        long fakeEventRow = 1234L;
        ELDEvent eldEvent = new ELDEvent();
        eldEvent.setId(2134124);

        when(mEldEventDao.insertEvent(any(ELDEventEntity.class))).thenReturn(fakeEventRow);

        TestObserver<Long> testObserver = TestObserver.create();

        // when
        mEldEventsInteractor.postNewELDEvent(eldEvent).subscribe(testObserver);

        // then
        verify(mEldEventDao).insertEvent(any(ELDEventEntity.class));
        verify(mLogSheetInteractor).resetLogSheetHeaderSigning(any(List.class));

        // TODO: verify SyncType.NEW_UNSYNC
    }

    @Test
    public void testPostNewEldEvents() {
        // given
        long[] rowIdsInserted = {12345L};
        List<ELDEvent> events = new ArrayList<>();

        when(mEldEventDao.insertAll(any())).thenReturn(rowIdsInserted);

        TestObserver<long[]> testObserver = TestObserver.create();

        // when
        mEldEventsInteractor.postNewELDEvents(events).subscribe(testObserver);

        // then
        verify(mEldEventDao).insertAll(Matchers.<ELDEventEntity>anyVararg());
        verify(mLogSheetInteractor).resetLogSheetHeaderSigning(eq(events));

        // TODO: verify SyncType.NEW_UNSYNC
    }

    @Test
    public void testStoreUnidentifiedEvents() {
        // given
        List<ELDEvent> events = new ArrayList<>();

        // when
        mEldEventsInteractor.storeUnidentifiedEvents(events);

        // then
        verify(mEldEventDao).insertAll(Matchers.<ELDEventEntity>anyVararg());

        // TODO: verify more once storeUnidentifiedEvents is complete
    }

    @Test
    public void testPostNewDutyTypeEvent() {
        // given
        DutyType dutyType = DutyType.DRIVING;
        long[] rowIds = {111L, 222L};
        String comment = "any comment";

        when(mBlackBoxInteractor.getLastData()).thenReturn(new BlackBoxModel());
        when(mEldEventDao.insertAll(any(ELDEventEntity.class))).thenReturn(rowIds);

        TestObserver<long[]> testObserver = TestObserver.create();
        ELDEventsInteractor eldEventsInteractorSpy = Mockito.spy(mEldEventsInteractor);

        // when
        eldEventsInteractorSpy.postNewDutyTypeEvent(dutyType, comment).subscribe(testObserver);

        // then
        verify(eldEventsInteractorSpy).postNewELDEvents(any(List.class));
        verify(mDutyTypeManager).setDutyType(eq(dutyType), eq(true));
    }

    @Test
    public void testPostLogoutEvent() {
        // given
        ResponseMessage response = new ResponseMessage();
        response.setMessage("ACK"); // hard-coded, from API documentation

        ELDEventsInteractor eldEventsInteractorSpy = Mockito.spy(mEldEventsInteractor);

        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.just(response));
        when(mBlackBoxInteractor.getLastData()).thenReturn(new BlackBoxModel());

        TestObserver<Boolean> testObserver = TestObserver.create();

        // when
        eldEventsInteractorSpy.postLogoutEvent().subscribe(testObserver);

        // then
        verify(eldEventsInteractorSpy).getEvent(eq(ELDEvent.LoginLogoutCode.LOGOUT));
        verify(mBlackBoxInteractor).shutdown(eq(true));
    }

    @Test
    public void testPostLogoutEventApiNack() {
        // given
        ResponseMessage response = new ResponseMessage();
        response.setMessage(""); // anything but "ACK"

        ELDEventsInteractor eldEventsInteractorSpy = Mockito.spy(mEldEventsInteractor);

        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.just(response));
        when(mBlackBoxInteractor.getLastData()).thenReturn(new BlackBoxModel());

        TestObserver<Boolean> testObserver = TestObserver.create();

        // when
        eldEventsInteractorSpy.postLogoutEvent().subscribe(testObserver);

        // then
        verify(eldEventsInteractorSpy).getEvent(eq(ELDEvent.LoginLogoutCode.LOGOUT));
        verify(mBlackBoxInteractor).shutdown(eq(false));
    }

    @Test
    public void testPostLogoutUnexpectedEventApiError() {
        // given
        Throwable error = new RuntimeException("anything but ACK");

        ELDEvent event = new ELDEvent();
        event.setEventType(ELDEvent.LoginLogoutCode.LOGOUT.getValue());

        BlackBoxModel blackBoxModel = new BlackBoxModel();
        blackBoxModel.setEngineHours(1000); // prevent null pointer exception in getBlackBoxState()

        TestObserver<Boolean> testObserver = TestObserver.create();
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.error(error));

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.DRIVING);

        when(mPreferencesManager.getBoxId()).thenReturn(12345); // for getEvents()
        when(mPreferencesManager.getVehicleId()).thenReturn(22222); // for getEvents()

        when(mBlackBoxInteractor.shutdown(anyBoolean())).thenReturn(Observable.just(false)); // return value ignored for now

        // when
        mEldEventsInteractor.postLogoutEvent().subscribe(testObserver);

        // then
        testObserver.assertNoErrors();
        verify(mBlackBoxInteractor).shutdown(eq(false));
    }

    @Test
    public void testPostLogoutApiRetrofitError() {
        // given
        Throwable error = RetrofitException.unexpectedError(new RuntimeException("unexpected retrofit"));

        ELDEvent event = new ELDEvent();
        event.setEventType(ELDEvent.LoginLogoutCode.LOGOUT.getValue());

        BlackBoxModel blackBoxModel = new BlackBoxModel();
        blackBoxModel.setEngineHours(1000); // prevent null pointer exception in getBlackBoxState()

        TestObserver<Boolean> testObserver = TestObserver.create();
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.error(error));

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.DRIVING);

        when(mPreferencesManager.getBoxId()).thenReturn(12345); // for getEvents()
        when(mPreferencesManager.getVehicleId()).thenReturn(22222); // for getEvents()

        when(mBlackBoxInteractor.shutdown(anyBoolean())).thenReturn(Observable.just(false)); // return value ignored for now

        // when
        mEldEventsInteractor.postLogoutEvent().subscribe(testObserver);

        // then
        testObserver.assertNoErrors();
        verify(mBlackBoxInteractor).shutdown(eq(true));
    }

    @Test
    public void testPostLogoutAoiIoException() {
        // given
        Throwable error = new IOException();

        ELDEvent event = new ELDEvent();
        event.setEventType(ELDEvent.LoginLogoutCode.LOGOUT.getValue());

        BlackBoxModel blackBoxModel = new BlackBoxModel();
        blackBoxModel.setEngineHours(1000); // prevent null pointer exception in getBlackBoxState()

        TestObserver<Boolean> testObserver = TestObserver.create();
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Observable.error(error));

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.DRIVING);

        when(mPreferencesManager.getBoxId()).thenReturn(12345); // for getEvents()
        when(mPreferencesManager.getVehicleId()).thenReturn(22222); // for getEvents()

        when(mBlackBoxInteractor.shutdown(anyBoolean())).thenReturn(Observable.just(false)); // return value ignored for now

        // when
        mEldEventsInteractor.postLogoutEvent().subscribe(testObserver);

        // then
        testObserver.assertNoErrors();
        verify(mBlackBoxInteractor).shutdown(eq(true));
    }

    @Test
    public void testSendReportApiSuccess() {
        // given
        mockGetLogSheetEventDependencies(DutyType.DRIVING, 12345, 11111);
        mockGetBlackBoxState();

        long start = 100000000L;
        long end = 1000002220L;
        int option = 0;
        String comment = "no comment";
        ResponseMessage success = new ResponseMessage();
        success.setMessage("ACK");

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.sendReport(anyLong(), anyLong(), anyInt(), any(ELDEvent.class)))
                .thenReturn(Single.just(success));

        // when
        mEldEventsInteractor.sendReport(start, end, option, comment).subscribe(testObserver);

        // then
        verify(mServiceApi).sendReport(anyLong(), anyLong(), anyInt(), any(ELDEvent.class));
        testObserver.assertValue(true);
    }

    @Test
    public void testSendReportApiError() {
        // given
        mockGetLogSheetEventDependencies(DutyType.DRIVING, 12345, 11111);
        mockGetBlackBoxState();

        long start = 100000000L;
        long end = 1000002220L;
        int option = 0;
        String comment = "no comment";
        Throwable error = new RuntimeException("api failed");

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.sendReport(anyLong(), anyLong(), anyInt(), any(ELDEvent.class)))
                .thenReturn(Single.error(error));

        // when
        mEldEventsInteractor.sendReport(start, end, option, comment).subscribe(testObserver);

        // then
        verify(mServiceApi).sendReport(anyLong(), anyLong(), anyInt(), any(ELDEvent.class));
        testObserver.assertError(error);
    }

    // TODO: test getDiagnosticEvents() when it is implemented
    // TODO: test getMalfunctionEvents() when it is implemented
    // TODO: test hasMalfunctionEvents() after modifying scope of getMalfunctionCount() for testing
    // TODO: test hasDiagnosticEvents() after modifying scope of getMalfunctionCount() for testing

    @Test
    public void testGetMalfunctionCountSync() {
        // given
        int driverId = 1111;
        long startTime = 2222222L;
        long endTime = 3333333L;
        when(mEldEventDao.getMalfunctionEventCountSync(anyInt(), anyLong(), anyLong())).thenReturn(10);

        // when
        mEldEventsInteractor.getMalfunctionCountSync(driverId, startTime, endTime);

        // then
        verify(mEldEventDao).getMalfunctionEventCountSync(eq(driverId), eq(startTime), eq(endTime));
    }

    @Test
    public void testGetDiagnosticCountSync() {
        // given
        int driverId = 1111;
        long startTime = 2222222L;
        long endTime = 3333333L;
        when(mEldEventDao.getDiagnosticEventCountSync(anyInt(), anyLong(), anyLong())).thenReturn(10);

        // when
        mEldEventsInteractor.getDiagnosticCountSync(driverId, startTime, endTime);

        // then
        verify(mEldEventDao).getDiagnosticEventCountSync(eq(driverId), eq(startTime), eq(endTime));
    }

    // TODO: test getEvents after changing scope for testing

    @Test
    public void testIsConnectedBoxSuccess() {
        // given
        BlackBoxModel blackBoxModel = new BlackBoxModel();
        blackBoxModel.setBoxId(2017);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        // when
        boolean result = mEldEventsInteractor.isConnected();

        // then
        verify(mBlackBoxInteractor).getLastData();
        assertEquals(true, result);
    }

    @Test
    public void testIsConnectedBoxFail() {
        // given
        BlackBoxModel blackBoxModel = new BlackBoxModel();
        blackBoxModel.setBoxId(0);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        // when
        boolean result = mEldEventsInteractor.isConnected();

        // then
        verify(mBlackBoxInteractor).getLastData();
        assertEquals(false, result);
    }

    @Test
    public void testGetEventLoginLogoutCode() {
        // given
        DutyType dutyType = DutyType.DRIVING;
        ELDEvent.LoginLogoutCode loginLogoutCode = ELDEvent.LoginLogoutCode.LOGIN;

        BlackBoxModel blackBoxModel = new BlackBoxModel();

        when(mDutyTypeManager.getDutyType()).thenReturn(dutyType);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        // when
        ELDEvent actualEldEvent = mEldEventsInteractor.getEvent(loginLogoutCode);

        // then
        assertEquals((Integer)ELDEvent.StatusCode.ACTIVE.getValue(), actualEldEvent.getStatus());
        assertEquals((Integer)ELDEvent.EventOrigin.DRIVER.getValue(), actualEldEvent.getOrigin());
        assertEquals((Integer)ELDEvent.EventType.LOGIN_LOGOUT.getValue(), actualEldEvent.getEventType());
        assertEquals((Integer)loginLogoutCode.getValue(), actualEldEvent.getEventCode());
    }

    // TODO: testGetEventLoginLogoutCode personal-use

    @Test
    public void testGetEventEnginePowerCode() {
        // given
        DutyType dutyType = DutyType.DRIVING;
        ELDEvent.EnginePowerCode enginePowerCode = ELDEvent.EnginePowerCode.POWER_UP;

        BlackBoxModel blackBoxModel = new BlackBoxModel();

        when(mDutyTypeManager.getDutyType()).thenReturn(dutyType);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        // when
        ELDEvent actualEldEvent = mEldEventsInteractor.getEvent(enginePowerCode);

        // then
        assertEquals((Integer)ELDEvent.StatusCode.ACTIVE.getValue(), actualEldEvent.getStatus());
        assertEquals((Integer)ELDEvent.EventOrigin.AUTOMATIC_RECORD.getValue(), actualEldEvent.getOrigin());
        assertEquals((Integer)ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue(), actualEldEvent.getEventType());
        assertEquals((Integer)enginePowerCode.getValue(), actualEldEvent.getEventCode());
    }

    // TODO: testGetEventEnginePowerCode personal-use


    @Test
    public void testGetEventWithComment() {
        // given
        DutyType dutyType = DutyType.DRIVING;

        String comment = "no comment";

        BlackBoxModel blackBoxModel = new BlackBoxModel();
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        // when
        ELDEvent result = mEldEventsInteractor.getEvent(dutyType, comment);

        // then
        assertEquals((Integer)ELDEvent.EventOrigin.DRIVER.getValue(), result.getOrigin());
        assertEquals((Integer)dutyType.getType(), result.getEventType());
        assertEquals((Integer)dutyType.getCode(), result.getEventCode());
        assertEquals(comment, result.getComment());
    }

    @Test
    public void testGetEvent() {
        // given
        DutyType dutyType = DutyType.DRIVING;

        BlackBoxModel blackBoxModel = new BlackBoxModel();
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        // when
        ELDEvent result = mEldEventsInteractor.getEvent(dutyType);

        // then
        assertEquals((Integer)ELDEvent.EventOrigin.DRIVER.getValue(), result.getOrigin());
        assertEquals((Integer)dutyType.getType(), result.getEventType());
        assertEquals((Integer)dutyType.getCode(), result.getEventCode());
    }

    @Test
    public void testGetEventWithMalfunction() {
        // given
        Malfunction malfunction = Malfunction.createByCode("P");
        ELDEvent.MalfunctionCode malfunctionCode = ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED;
        BlackBoxModel blackBoxModel = new BlackBoxModel();

        // when
        ELDEvent eldEvent = mEldEventsInteractor.getEvent(malfunction, malfunctionCode, blackBoxModel);

        // then
        assertEquals(malfunction, eldEvent.getMalCode());
        assertEquals((Integer)ELDEvent.StatusCode.ACTIVE.getValue(), eldEvent.getStatus());
        assertEquals((Integer)malfunctionCode.getCode(), eldEvent.getEventCode());
        assertEquals((Integer)ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(), eldEvent.getEventType());
    }

    @Test
    @Ignore("engine hours incorrectly defined in ELDEvent")
    public void testGetEventIsAutoTrue() {
        // given
        BlackBoxModel blackBoxModel = new BlackBoxModel();
        double engineHours = 1234.5; // TODO: ELDEvent.mEngineHours (and all related engine hours) needs to be updated to double (ELD 7.19)
        // TODO: complete the rest of this test

        // when

        // then
    }


    /**
     * Mocks dependencies of getLogSheetEvent to avoid null pointer exception.
     *
     * @param dutyType
     * @param driverId
     * @param vehicleId
     */
    private void mockGetLogSheetEventDependencies(DutyType dutyType, int driverId, int vehicleId) {
        when(mDutyTypeManager.getDutyType()).thenReturn(dutyType);
        when(mAccountManager.getCurrentUserId()).thenReturn(driverId);
        when(mPreferencesManager.getVehicleId()).thenReturn(vehicleId);
    }


    /**
     * Mocks dependencies of getBlackBoxState to avoid null pointer exception.
     */
    private void mockGetBlackBoxState() {
        BlackBoxModel blackBoxModel = new BlackBoxModel();
        blackBoxModel.setEngineHours(111);
        blackBoxModel.setOdometer(222);
        blackBoxModel.setLat(20.00);
        blackBoxModel.setLon(10.00);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);
    }




}
