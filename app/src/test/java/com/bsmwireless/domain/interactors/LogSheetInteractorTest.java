package com.bsmwireless.domain.interactors;

import android.util.Log;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalDao;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.logsheets.LogSheetDao;
import com.bsmwireless.data.storage.logsheets.LogSheetEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for LogSheetInteractor.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogSheetInteractorTest {

    private static final int INT_USYNC = LogSheetEntity.SyncType.UNSYNC.ordinal();
    private static final int INT_SYNC = LogSheetEntity.SyncType.SYNC.ordinal();

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    PreferencesManager mPreferencesManager;

    @Mock
    AppDatabase mAppDatabase;

    @Mock
    UserDao mUserDao;

    @Mock
    HomeTerminalDao mHomeTerminalDao;

    @Mock
    AccountManager mAccountManager;

    @Mock
    LogSheetDao mLogSheetDao;


    private LogSheetInteractor mLogSheetInteractor;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mAppDatabase.logSheetDao()).thenReturn(mLogSheetDao);

        mLogSheetInteractor = new LogSheetInteractor(mPreferencesManager, mAppDatabase, mAccountManager);
    }

    @Test
    public void testGetLogSheetHeaders() {
        // given
        final Long startLogDay = 1503014401L; // validity of date doesn't matter for these tests
        final Long endLogDay = 1503100801L; // validity of date doesn't matter for these tests

        final LogSheetEntity[] logSheets = {new LogSheetEntity(), new LogSheetEntity()};
        final List<LogSheetEntity> logHeaderList = Arrays.asList(logSheets);

        TestSubscriber<List<LogSheetHeader>> testSubscriber = TestSubscriber.create();

        when(mLogSheetDao.getLogSheets(any(Long.class), any(Long.class), anyInt())).thenReturn(Flowable.just(logHeaderList));

        // when
        mLogSheetInteractor.getLogSheetHeaders(startLogDay, endLogDay).subscribe(testSubscriber);

        // then
        verify(mLogSheetDao).getLogSheets(eq(startLogDay), eq(endLogDay), anyInt());
    }

    @Test
    public void testGetLogSheetHeadersFromDBOnce() {
        // given
        final Long startLogDay = 1503014401L; // validity of date doesn't matter for these tests
        TestObserver<LogSheetHeader> testObserver = TestObserver.create();
        when(mAccountManager.getCurrentUserId()).thenReturn(1234);
        when(mAppDatabase.logSheetDao()).thenReturn(mLogSheetDao);
        when(mLogSheetDao.getLogSheet(anyLong(), anyInt())).thenReturn(Single.just(new LogSheetEntity()));

        // when
        mLogSheetInteractor.getLogSheetHeadersFromDBOnce(startLogDay).subscribe(testObserver);

        // then
        verify(mAccountManager).getCurrentUserId();
        verify(mLogSheetDao).getLogSheet(eq(startLogDay), anyInt());
    }

    @Test
    public void testGetLogSheetHeadersForMonth() {
        // given
        String timezone = "America/Los_Angeles"; // user timezone for example "America/Los_Angeles"
        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);

        final int numDaysAgo = 30;

        TestSubscriber<List<LogSheetHeader>> testSubscriber = TestSubscriber.create();

        List<LogSheetEntity> headers = new ArrayList<>();

        LogSheetInteractor spy = Mockito.spy(mLogSheetInteractor);

        when(mLogSheetDao.getLogSheets(anyLong(), anyLong(), anyInt()))
                .thenReturn(Flowable.just(headers));

        // when
        spy.getLogSheetHeadersForMonth(timezone).subscribe(testSubscriber);

        // then
        verify(spy).getLogSheetHeaders(captor1.capture(), captor2.capture());

        long expected1 = yyyyMmDdNumDaysAgo(captor2.getValue(), numDaysAgo, timezone);
        assertEquals((long)captor1.getValue(), expected1);
    }

    @Test
    public void testGetLogSheet() {
        // given
        final Long logDay = 1503014401L;
        TestObserver<LogSheetHeader> testObserver = TestObserver.create();
        LogSheetInteractor spy = Mockito.spy(mLogSheetInteractor);

        // when
        spy.getLogSheet(logDay).subscribe(testObserver);

        // then
        verify(spy).getLogSheetEntity(eq(logDay));
    }

    @Test
    public void testUpdateLogSheetHeader() {
        // given
        LogSheetHeader logSheetHeader = new LogSheetHeader();
        TestObserver<Long> testObserver = TestObserver.create();

        // when
        mLogSheetInteractor.updateLogSheetHeader(logSheetHeader).subscribe(testObserver);

        // then
        verify(mLogSheetDao).insert(argThat(new ArgumentMatcher<LogSheetEntity>(){

            @Override
            public boolean matches(LogSheetEntity argument) {
                return argument.getSync() == INT_USYNC;
            }
        }));
    }

    @Test
    public void testSignLogSheetNotAlreadySigned() {
        // given
        LogSheetInteractor spy = Mockito.spy(mLogSheetInteractor);

        long logDay = 20000000L;
        TestObserver<LogSheetHeader> testObserver = TestObserver.create();

        LogSheetEntity logSheetEntity = new LogSheetEntity();
        logSheetEntity.setSigned(false);

        when(mLogSheetDao.getByLogDaySync(anyLong(), anyInt())).thenReturn(logSheetEntity);

        // when
        spy.signLogSheet(logDay).subscribe(testObserver);

        // then
        verify(mLogSheetDao).insert(argThat(new ArgumentMatcher<LogSheetEntity>() {
            @Override
            public boolean matches(LogSheetEntity argument) {
                return logSheetEntity.getSigned() == true
                        && logSheetEntity.getSync() == INT_USYNC;
            }
        }));

        // TODO: verify addCertificationEvent
    }


    @Test
    public void testSignLogSheetAlreadySigned() {
        // given
        LogSheetInteractor spy = Mockito.spy(mLogSheetInteractor);

        long logDay = 20000000L;
        TestObserver<LogSheetHeader> testObserver = TestObserver.create();

        LogSheetEntity logSheetEntity = new LogSheetEntity();
        logSheetEntity.setSigned(true);

        when(mLogSheetDao.getByLogDaySync(anyLong(), anyInt())).thenReturn(logSheetEntity);

        // when
        spy.signLogSheet(logDay).subscribe(testObserver);

        // then
        verify(mLogSheetDao, never()).insert(any(LogSheetEntity.class));
    }


    // TODO: test addCertificationEvent
    // TODO: test createCertificationEvent

    @Test
    public void testResetLogSheetHeaderSigning() {
        // given
        String timezone = "America/Los_Angeles";

        // day 1 - Oct 1, 2017 @ America/Los_Angeles
        long logstart1 = 1506841200000L; // unix time milliseconds
        long logday1 = 20171001;
        ELDEvent event1 = new ELDEvent();
        event1.setTimezone(timezone);
        event1.setEventTime(logstart1+10000); // 10 seconds past midnight
        LogSheetEntity logSheetEntity1 = new LogSheetEntity();
        logSheetEntity1.setLogDay(logday1);
        logSheetEntity1.setSigned(false);
        logSheetEntity1.setSync(LogSheetEntity.SyncType.UNSYNC.ordinal());

        // day 2 - Oct 2, 2017 @ America/Los_Angeles
        long logstart2 = 1506927600000L; // unix time milliseconds
        long logday2 = 20171002;
        ELDEvent event2 = new ELDEvent();
        event2.setTimezone(timezone);
        event2.setEventTime(logstart2+10000); // 10 seconds past midnight
        LogSheetEntity logSheetEntity2 = new LogSheetEntity();
        logSheetEntity2.setLogDay(logday2);
        logSheetEntity2.setSigned(true);
        logSheetEntity2.setSync(LogSheetEntity.SyncType.UNSYNC.ordinal());

        List<ELDEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        LogSheetInteractor spy = Mockito.spy(mLogSheetInteractor);

        when(mLogSheetDao.getByLogDaySync(eq(logday1), anyInt())).thenReturn(logSheetEntity1);
        when(mLogSheetDao.getByLogDaySync(eq(logday2), anyInt())).thenReturn(logSheetEntity2);

        when(mAccountManager.getCurrentUserId()).thenReturn(1111);

        // when
        spy.resetLogSheetHeaderSigning(events);

        // then
        verify(mLogSheetDao, times(2)).insert(argThat(new ArgumentMatcher<LogSheetEntity>() {
            @Override
            public boolean matches(LogSheetEntity entity) {
                return entity.getSigned() == false;
            }
        }));

        verify(mLogSheetDao, times(2)).insert(argThat(new ArgumentMatcher<LogSheetEntity>() {
            @Override
            public boolean matches(LogSheetEntity entity) {
                return entity.getSync() == LogSheetEntity.SyncType.UNSYNC.ordinal();
            }
        }));

        verify(mLogSheetDao, times(2)).insert(argThat(new ArgumentMatcher<LogSheetEntity>() {

            @Override
            public boolean matches(LogSheetEntity entity) {
                return (entity.getLogDay() == logday1 || entity.getLogDay() == logday2);
            }
        }));

        // TODO: validate dates LogSheetInteractor#resetLogSheetHeaderSigning(ELDEvent event) when scope changed for unit testing
    }


    // TODO: getLogSheetEntity()


    @Test
    public void testCreateLogSheetHeaderModel() {
        // given
        final Long startLogDay = 1503014401L;
        int driverId = 111111;
        int boxId = 2222222;
        int vehicleId = 33333;
        int homeTermId = 444444;

        UserEntity fakeUser = new UserEntity();
        fakeUser.setHomeTermId(2134);

        HomeTerminalEntity fakeHomeTerminalentity = new HomeTerminalEntity();
        fakeHomeTerminalentity.setId(homeTermId);

        when(mAccountManager.getCurrentDriverId()).thenReturn(driverId);
        when(mPreferencesManager.getBoxId()).thenReturn(boxId);
        when(mPreferencesManager.getVehicleId()).thenReturn(vehicleId);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserSync(any(Integer.class))).thenReturn(fakeUser);
        when(mAppDatabase.homeTerminalDao()).thenReturn(mHomeTerminalDao);
        when(mHomeTerminalDao.getHomeTerminalSync(any(Integer.class))).thenReturn(fakeHomeTerminalentity);


        // when
        LogSheetHeader result = mLogSheetInteractor.createLogSheetHeaderModel(startLogDay);

        // then
        verify(mAccountManager).getCurrentDriverId();
        verify(mPreferencesManager).getBoxId();
        verify(mPreferencesManager).getVehicleId();
        verify(mUserDao).getUserSync(anyInt());
        verify(mHomeTerminalDao).getHomeTerminalSync(anyInt());

        // TODO: add more once LogSheetInteractor#createLogSheetHeader is completed
    }


    /**
     * Get the start of a given day.
     *
     * Time is returned in unix time in milliseconds, and is converted to the local time of
     * <code>timezone</code>.
     *
     * @param sometimeTodayMillis  some time during the day in milliseconds since epoch
     * @param timezone  valid java timezone string
     * @return
     */
    private static long startOfDay(long sometimeTodayMillis, String timezone) {
        long result = 0L;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        calendar.setTimeInMillis(sometimeTodayMillis);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        result = calendar.getTimeInMillis();

        return result;
    }

    /**
     * Get a Long date in the form of yyyyMMdd, e.g. 20171031
     *
     * @param millis unix time in milliseconds
     * @param timezone timezone of calendar date
     * @return date in the form of yyyyMMdd, e.g. 20171031
     */
    private static long getYYYYMMDDFromMillis(long millis, String timezone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        calendar.setTimeInMillis(millis);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);

        return Long.parseLong(format.format(calendar.getTime()));
    }


    /**
     * Get the start of numDays ago.
     *
     * Time is returned in unix time in milliseconds, and is converted to the local time of
     * <code>timezone</code>.
     *
     * @param numDays number of days ago
     * @param sometimeTodayMillis some time during the day in milliseconds since epoch
     * @param timezone valid java timezone string
     * @return
     */
    private static long startOfNumDaysAgo(int numDays, long sometimeTodayMillis, String timezone) {
        long result = 0L;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        calendar.setTimeInMillis(sometimeTodayMillis);

        calendar.add(Calendar.DAY_OF_YEAR, -1*numDays);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        result = calendar.getTimeInMillis();

        return result;
    }

    /**
     * Get date at a number of days ago.
     *
     * Long format returned is yyyyMMdd, e.g. 20171031
     *
     * @param yyyyMmDd current date e.g. 20171031
     * @param numDaysAgo number of days ago
     * @param timezone timezone to perform calculation in
     * @return date at a number of days ago e.g. 20170930
     */
    private static long yyyyMmDdNumDaysAgo(long yyyyMmDd, int numDaysAgo, String timezone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));

        long year = yyyyMmDd/10000;
        long month = (yyyyMmDd-year*10000)/100;  // Calendar month, Jan = 1. For Java Calendar: Jan = 0
        long calDay = yyyyMmDd-year*10000-month*100;

        calendar.set((int)year, (int)month-1, (int)calDay, 0, 0, 0);

        calendar.add(Calendar.DAY_OF_YEAR, -1*numDaysAgo);

        Date date = calendar.getTime();

        return getYYYYMMDDFromMillis(date.getTime(), timezone);
    }

}
