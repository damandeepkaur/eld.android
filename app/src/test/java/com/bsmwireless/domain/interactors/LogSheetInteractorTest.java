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
import com.bsmwireless.models.LogSheetHeader;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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


    // TODO: resetLogSheetHeaderSignging()
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

}
