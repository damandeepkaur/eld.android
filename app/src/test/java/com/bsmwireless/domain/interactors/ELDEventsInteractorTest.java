package com.bsmwireless.domain.interactors;

import com.bsmwireless.BaseTest;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ELDEventsInteractorTest extends BaseTest {

    private final String mfakeTimezone = "fake timezone";
    private final long MS_IN_DAY = 24*60*60*1000;

    @Mock
    ServiceApi mServiceApi;
    @Mock
    BlackBoxInteractor mBlackBoxInteractor;
    @Mock
    UserInteractor mUserInteractor;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    ELDEventDao mELDEventDao;
    @Mock
    AppDatabase mAppDatabase;
    @Mock
    UserDao mUserDao;
    @Mock
    PreferencesManager mPreferencesManager;
    @Mock
    AccountManager mAccountManager;
    @Mock
    TokenManager mTokenManager;
    @Mock
    LogSheetInteractor mLogSheetInteractor;

    ELDEventsInteractor mELDEventsInteractor;


    @Before
    public void setUp() throws Exception {

        when(mAppDatabase.ELDEventDao()).thenReturn(mELDEventDao);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);

        when(mUserInteractor.getTimezone()).thenReturn(Flowable.empty());

        mELDEventsInteractor = new ELDEventsInteractor(mServiceApi,
                mPreferencesManager,
                mAppDatabase,
                mUserInteractor,
                mBlackBoxInteractor,
                mDutyTypeManager,
                mAccountManager,
                mTokenManager,
                mLogSheetInteractor);
    }

    @Test
    public void getEventPositionCompliance() throws Exception {
        BlackBoxModel blackBoxModel = spy(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(true);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        ELDEventEntity eldEventEntity = mock(ELDEventEntity.class);
        when(eldEventEntity.getEventType())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());

        final int currentUserId = 1;
        when(mAccountManager.getCurrentUserId()).thenReturn(currentUserId);
        when(mELDEventDao
                .getLatestEventSync(
                        currentUserId,
                        ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                        Malfunction.POSITIONING_COMPLIANCE.getCode(),
                        ELDEvent.StatusCode.ACTIVE.getValue()))
                .thenReturn(eldEventEntity);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);
        assertEquals(ELDEvent.LatLngFlag.FLAG_E, event.getLatLngFlag());

    }

    @Test
    public void getEventPositionComplianceCleared() throws Exception {

        BlackBoxModel blackBoxModel = spy(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(true);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        ELDEventEntity eldEventEntity = mock(ELDEventEntity.class);
        when(eldEventEntity.getEventType())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());

        final int currentUserId = 1;
        when(mAccountManager.getCurrentUserId()).thenReturn(currentUserId);
        when(mELDEventDao
                .getLatestEventSync(
                        currentUserId,
                        ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                        Malfunction.POSITIONING_COMPLIANCE.getCode(),
                        ELDEvent.StatusCode.ACTIVE.getValue()))
                .thenReturn(eldEventEntity);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);
        assertEquals(ELDEvent.LatLngFlag.FLAG_NONE, event.getLatLngFlag());
    }

    @Test
    public void getEventNoCompliance() throws Exception {

        BlackBoxModel blackBoxModel = spy(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(true);

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        final int currentUserId = 1;
        when(mAccountManager.getCurrentUserId()).thenReturn(currentUserId);
        when(mELDEventDao
                .getLatestEventSync(
                        currentUserId,
                        ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                        Malfunction.POSITIONING_COMPLIANCE.getCode(),
                        ELDEvent.StatusCode.ACTIVE.getValue()))
                .thenReturn(null);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);

        assertEquals(ELDEvent.LatLngFlag.FLAG_NONE, event.getLatLngFlag());

    }

    @Test
    public void getEventGpsNoFixNoCompliance() throws Exception {

        BlackBoxModel blackBoxModel = mock(BlackBoxModel.class);
        when(blackBoxModel.getSensorState(BlackBoxSensorState.GPS)).thenReturn(false);

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        final int currentUserId = 1;
        when(mAccountManager.getCurrentUserId()).thenReturn(currentUserId);
        when(mELDEventDao
                .getLatestEventSync(
                        currentUserId,
                        ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                        Malfunction.POSITIONING_COMPLIANCE.getCode(),
                        ELDEvent.StatusCode.ACTIVE.getValue()))
                .thenReturn(null);

        ELDEvent event = mELDEventsInteractor.getEvent(DutyType.ON_DUTY);

        assertEquals(ELDEvent.LatLngFlag.FLAG_X, event.getLatLngFlag());

    }

    /**
     * Test for getting a correct list of diagnostic events
     *
     * @throws Exception
     */
    @Test
    public void getDiagnosticEvents() throws Exception {

        final int currentUserId = 1;

        when(mAccountManager.getCurrentUserId()).thenReturn(currentUserId);

        // logged events

        ELDEventEntity powerDiagnosticLogged = mock(ELDEventEntity.class);
        when(powerDiagnosticLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(powerDiagnosticLogged.getMalCode())
                .thenReturn(Malfunction.POWER_DATA_DIAGNOSTIC.getCode());

        ELDEventEntity engineSynchLogged = mock(ELDEventEntity.class);
        when(engineSynchLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(engineSynchLogged.getMalCode())
                .thenReturn(Malfunction.ENGINE_SYNCHRONIZATION.getCode());

        ELDEventEntity dataTransferLogged = mock(ELDEventEntity.class);
        when(dataTransferLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(dataTransferLogged.getMalCode())
                .thenReturn(Malfunction.DATA_TRANSFER.getCode());

        ELDEventEntity secondPowerDiagnosticLogged = mock(ELDEventEntity.class);
        when(secondPowerDiagnosticLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(secondPowerDiagnosticLogged.getMalCode())
                .thenReturn(Malfunction.POWER_DATA_DIAGNOSTIC.getCode());

        ELDEventEntity unidentifiedLogged = mock(ELDEventEntity.class);
        when(unidentifiedLogged.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode());
        when(unidentifiedLogged.getMalCode())
                .thenReturn(Malfunction.UNIDENTIFIED_DRIVING.getCode());

        // cleared events
        ELDEventEntity powerDiagnosticCleared = mock(ELDEventEntity.class);
        when(powerDiagnosticCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(powerDiagnosticCleared.getMalCode())
                .thenReturn(Malfunction.POWER_DATA_DIAGNOSTIC.getCode());

        ELDEventEntity dataTransferCleared = mock(ELDEventEntity.class);
        when(dataTransferCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(dataTransferCleared.getMalCode())
                .thenReturn(Malfunction.DATA_TRANSFER.getCode());

        ELDEventEntity unidentifiedCleared = mock(ELDEventEntity.class);
        when(unidentifiedCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(unidentifiedCleared.getMalCode())
                .thenReturn(Malfunction.UNIDENTIFIED_DRIVING.getCode());

        ELDEventEntity engineSynchCleared = mock(ELDEventEntity.class);
        when(engineSynchCleared.getEventCode())
                .thenReturn(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode());
        when(engineSynchCleared.getMalCode())
                .thenReturn(Malfunction.ENGINE_SYNCHRONIZATION.getCode());

        List<ELDEventEntity> entities = Arrays.asList(powerDiagnosticLogged, engineSynchLogged,
                powerDiagnosticCleared, engineSynchCleared, secondPowerDiagnosticLogged,
                dataTransferLogged, unidentifiedLogged, unidentifiedCleared, dataTransferCleared);

        when(mELDEventDao.loadMalfunctions(anyInt(), anyInt(), any(String[].class), anyInt()))
                .thenReturn(Single.just(entities));

        mELDEventsInteractor.getDiagnosticEvents()
                .test()
                .assertValue(events -> {

                    if (events.size() != 1) return false;
                    ELDEvent event = events.get(0);

                    return event.getMalCode() == Malfunction.POWER_DATA_DIAGNOSTIC
                            && event.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode();
                });
        verify(mELDEventDao).loadMalfunctions(currentUserId,
                ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                Constants.DIAGNOSTIC_CODES,
                ELDEvent.StatusCode.ACTIVE.getValue());
    }


    @Test
    public void testGetEventsFromDBOnceSuccess() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        List<ELDEventEntity> eldEvents = new ArrayList<>();

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();
        when(mAccountManager.getCurrentUserId()).thenReturn(111111);
        when(mELDEventDao.getEventsFromStartToEndTimeOnce(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.just(eldEvents));

        // when
        mELDEventsInteractor.getEventsFromDBOnce(startTime, endTime).subscribe(testObserver);

        // then
        verify(mAccountManager).getCurrentUserId();
        verify(mELDEventDao).getEventsFromStartToEndTimeOnce(eq(startTime), eq(endTime), anyInt());
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
        when(mELDEventDao.getEventsFromStartToEndTimeOnce(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.error(error));

        // when
        mELDEventsInteractor.getEventsFromDBOnce(startTime, endTime).subscribe(testObserver);

        // then
        testObserver.assertError(error); // error is propagated
    }

    @Test
    public void testGetDutyEventsFromDbSuccess() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mELDEventDao.getDutyEventsFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.just(eldEventEntities));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mELDEventsInteractor.getDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        verify(mELDEventDao).getDutyEventsFromStartToEndTime(eq(startTime), eq(endTime), anyInt());
    }

    @Test
    public void testGetDutyEventsFromDbError() {
        // given
        long startTime = 1234567890;
        long endTime = 1235555555;

        Exception fakeDbException = new RuntimeException("deadlock");

        when(mAccountManager.getCurrentUserId()).thenReturn(12345);
        when(mELDEventDao.getDutyEventsFromStartToEndTime(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.error(fakeDbException));

        TestSubscriber<List<ELDEvent>> testSubscriber = TestSubscriber.create();

        // when
        mELDEventsInteractor.getDutyEventsFromDB(startTime, endTime).subscribe(testSubscriber);

        // then
        testSubscriber.assertError(Throwable.class);
    }

    @Test
    public void testGetLatestActiveDutyEventFromDbSync() {
        // given
        long latestTime = 1515151515;
        int userId = 1234;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mELDEventDao.getLatestActiveDutyEventSync(anyLong(), anyInt()))
                .thenReturn(eldEventEntities);

        // when
        mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(latestTime, userId);

        // then
        verify(mELDEventDao).getLatestActiveDutyEventSync(eq(latestTime), anyInt());
    }

    @Test
    public void testGetLatestActiveDutyEventFromDBOnce() {
        // given
        long latestTime = 1515151515;
        int userId = 1234;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();
        when(mELDEventDao.getLatestActiveDutyEventOnce(anyLong(), anyInt()))
                .thenReturn(Single.just(eldEventEntities));

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mELDEventsInteractor.getLatestActiveDutyEventFromDBOnce(latestTime, userId)
                .subscribe(testObserver);

        // then
        verify(mELDEventDao).getLatestActiveDutyEventOnce(eq(latestTime), eq(userId));
    }

    @Test
    public void testGetLatestActiveDutyEventFromDBError() {
        // given
        long latestTime = 1515151515;
        int userId = 1234;

        Throwable error = new RuntimeException("db broken");
        when(mELDEventDao.getLatestActiveDutyEventOnce(anyLong(), anyInt()))
                .thenReturn(Single.error(error));

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mELDEventsInteractor.getLatestActiveDutyEventFromDBOnce(latestTime, userId)
                .subscribe(testObserver);

        // then
        verify(mELDEventDao).getLatestActiveDutyEventOnce(eq(latestTime), eq(userId));
        testObserver.assertError(error); // error propagated
    }

    @Test
    public void testGetActiveEventsFromDbSync() {
        // given
        long startTime = 10000L;
        long endTime = 20000L;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mELDEventDao.getActiveEventsFromStartToEndTimeSync(anyLong(), anyLong(), anyInt()))
                .thenReturn(eldEventEntities);

        // when
        mELDEventsInteractor.getActiveEventsFromDBSync(startTime, endTime);

        // then
        verify(mELDEventDao).getActiveEventsFromStartToEndTimeSync(eq(startTime), eq(endTime), anyInt());
    }

    @Test
    public void testGetDutyEventsForDay() {
        // given
        long startDayTime = 10000000L;

        List<ELDEventEntity> eldEventEntities = new ArrayList<>();

        when(mELDEventDao.getDutyEventsFromStartToEndTimeSync(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.just(eldEventEntities));

        when(mAccountManager.getCurrentUserId()).thenReturn(1337);

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mELDEventsInteractor.getDutyEventsForDay(startDayTime).subscribe(testObserver);

        // then
        verify(mELDEventDao).getDutyEventsFromStartToEndTimeSync(eq(startDayTime),
                eq(startDayTime + MS_IN_DAY), anyInt());
    }

    @Test
    public void testGetDutyEventsForDayError() {
        // given
        long startDayTime = 10000000L;

        Throwable error = new RuntimeException("error from dao");

        when(mELDEventDao.getDutyEventsFromStartToEndTimeSync(anyLong(), anyLong(), anyInt()))
                .thenReturn(Single.error(error));

        when(mAccountManager.getCurrentUserId()).thenReturn(1337);

        TestObserver<List<ELDEvent>> testObserver = TestObserver.create();

        // when
        mELDEventsInteractor.getDutyEventsForDay(startDayTime).subscribe(testObserver);

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

        when(mELDEventDao.getLatestActiveDutyEventSync(anyLong(), anyInt())).thenReturn(eldEventEntities);
        when(mAccountManager.getCurrentUserId()).thenReturn(123581220);

        // when
        ELDEvent eldEvent = mELDEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime);

        // then
        assertNull(eldEvent);
        verify(mELDEventDao).getLatestActiveDutyEventSync(eq(startDayTime), anyInt());
    }

    @Test
    public void testGetLatestActiveDutyEventFromDB() {
        // given
        long startDayTime = 111111111L;
        List<ELDEventEntity> eldEventEntities = new ArrayList<>();
        ELDEventEntity entity1 = new ELDEventEntity();

        eldEventEntities.add(entity1);

        when(mELDEventDao.getLatestActiveDutyEventSync(anyLong(), anyInt())).thenReturn(eldEventEntities);
        when(mAccountManager.getCurrentUserId()).thenReturn(123581220);

        // when
        ELDEvent eldEvent = mELDEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime);

        // then
        assertNotNull(eldEvent);
        verify(mELDEventDao).getLatestActiveDutyEventSync(eq(startDayTime), anyInt());
    }

    @Test
    public void testUpdateEldEvents() {
        // given
        long[] rowIdsInserted = {12345L};
        List<ELDEvent> events = new ArrayList<>();

        TestObserver<long[]> testObserver = TestObserver.create();
        when(mELDEventDao.insertAll(any())).thenReturn(rowIdsInserted);

        // when
        mELDEventsInteractor.updateELDEvents(events).subscribe(testObserver);

        // then
        verify(mELDEventDao).insertAll(Matchers.<ELDEventEntity>anyVararg());
        verify(mLogSheetInteractor).resetLogSheetHeaderSigning(eq(events));

        // TODO: verify SyncType.UPDATE_UNSYNC
    }

    @Test
    public void testPostNewEldEvent() {
        // given
        long fakeEventRow = 1234L;
        ELDEvent eldEvent = new ELDEvent();
        eldEvent.setId(2134124);

        when(mELDEventDao.insertEvent(any(ELDEventEntity.class))).thenReturn(fakeEventRow);

        TestObserver<Long> testObserver = TestObserver.create();

        // when
        mELDEventsInteractor.postNewELDEvent(eldEvent).subscribe(testObserver);

        // then
        verify(mELDEventDao).insertEvent(any(ELDEventEntity.class));
        verify(mLogSheetInteractor).resetLogSheetHeaderSigning(any(List.class));

        // TODO: verify SyncType.NEW_UNSYNC
    }

    @Test
    public void testPostNewEldEvents() {
        // given
        long[] rowIdsInserted = {12345L};
        List<ELDEvent> events = new ArrayList<>();

        when(mELDEventDao.insertAll(any())).thenReturn(rowIdsInserted);

        TestObserver<long[]> testObserver = TestObserver.create();

        // when
        mELDEventsInteractor.postNewELDEvents(events).subscribe(testObserver);

        // then
        verify(mELDEventDao).insertAll(Matchers.<ELDEventEntity>anyVararg());
        verify(mLogSheetInteractor).resetLogSheetHeaderSigning(eq(events));

        // TODO: verify SyncType.NEW_UNSYNC
    }

    @Test
    public void testStoreUnidentifiedEvents() {
        // given
        List<ELDEvent> events = new ArrayList<>();

        // when
        mELDEventsInteractor.storeUnidentifiedEvents(events);

        // then
        verify(mELDEventDao).insertAll(Matchers.<ELDEventEntity>anyVararg());

        // TODO: verify more once storeUnidentifiedEvents is complete
    }

    @Test
    public void testPostNewDutyTypeEvent() {
        // given
        DutyType dutyType = DutyType.DRIVING;
        long[] rowIds = {111L, 222L};
        String comment = "any comment";

        when(mBlackBoxInteractor.getLastData()).thenReturn(new BlackBoxModel());
        when(mELDEventDao.insertAll(any(ELDEventEntity.class))).thenReturn(rowIds);

        TestObserver<long[]> testObserver = TestObserver.create();
        ELDEventsInteractor eldEventsInteractorSpy = Mockito.spy(mELDEventsInteractor);

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

        ELDEventsInteractor eldEventsInteractorSpy = Mockito.spy(mELDEventsInteractor);

        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Single.just(response));
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

        ELDEventsInteractor eldEventsInteractorSpy = Mockito.spy(mELDEventsInteractor);

        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Single.just(response));
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
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Single.error(error));

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.DRIVING);

        when(mPreferencesManager.getBoxId()).thenReturn(12345); // for getEvents()
        when(mPreferencesManager.getVehicleId()).thenReturn(22222); // for getEvents()

        when(mBlackBoxInteractor.shutdown(anyBoolean())).thenReturn(Observable.just(false)); // return value ignored for now

        // when
        mELDEventsInteractor.postLogoutEvent().subscribe(testObserver);

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
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Single.error(error));

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.DRIVING);

        when(mPreferencesManager.getBoxId()).thenReturn(12345); // for getEvents()
        when(mPreferencesManager.getVehicleId()).thenReturn(22222); // for getEvents()

        when(mBlackBoxInteractor.shutdown(anyBoolean())).thenReturn(Observable.just(false)); // return value ignored for now

        // when
        mELDEventsInteractor.postLogoutEvent().subscribe(testObserver);

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
        when(mServiceApi.logout(any(ELDEvent.class))).thenReturn(Single.error(error));

        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.DRIVING);

        when(mPreferencesManager.getBoxId()).thenReturn(12345); // for getEvents()
        when(mPreferencesManager.getVehicleId()).thenReturn(22222); // for getEvents()

        when(mBlackBoxInteractor.shutdown(anyBoolean())).thenReturn(Observable.just(false)); // return value ignored for now

        // when
        mELDEventsInteractor.postLogoutEvent().subscribe(testObserver);

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
        mELDEventsInteractor.sendReport(start, end, option, comment).subscribe(testObserver);

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
        mELDEventsInteractor.sendReport(start, end, option, comment).subscribe(testObserver);

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
        when(mELDEventDao.getMalfunctionEventCountSync(anyInt(), anyLong(), anyLong())).thenReturn(10);

        // when
        mELDEventsInteractor.getMalfunctionCountSync(driverId, startTime, endTime);

        // then
        verify(mELDEventDao).getMalfunctionEventCountSync(eq(driverId), eq(startTime), eq(endTime));
    }

    @Test
    public void testGetDiagnosticCountSync() {
        // given
        int driverId = 1111;
        long startTime = 2222222L;
        long endTime = 3333333L;
        when(mELDEventDao.getDiagnosticEventCountSync(anyInt(), anyLong(), anyLong())).thenReturn(10);

        // when
        mELDEventsInteractor.getDiagnosticCountSync(driverId, startTime, endTime);

        // then
        verify(mELDEventDao).getDiagnosticEventCountSync(eq(driverId), eq(startTime), eq(endTime));
    }

    // TODO: test getEvents after changing scope for testing

    @Test
    public void testIsConnectedBoxSuccess() {
        // given
        BlackBoxModel blackBoxModel = new BlackBoxModel();
        blackBoxModel.setBoxId(2017);
        when(mBlackBoxInteractor.getLastData()).thenReturn(blackBoxModel);

        // when
        boolean result = mELDEventsInteractor.isConnected();

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
        boolean result = mELDEventsInteractor.isConnected();

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
        ELDEvent actualEldEvent = mELDEventsInteractor.getEvent(loginLogoutCode);

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
        ELDEvent actualEldEvent = mELDEventsInteractor.getEvent(enginePowerCode);

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
        ELDEvent result = mELDEventsInteractor.getEvent(dutyType, comment);

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
        ELDEvent result = mELDEventsInteractor.getEvent(dutyType);

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
        ELDEvent eldEvent = mELDEventsInteractor.getEvent(malfunction, malfunctionCode, blackBoxModel);

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