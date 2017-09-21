package com.bsmwireless.domain.interactors;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for LogSheetInteractor.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogSheetInteractorTest {

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
    public void testCreateLogSheetHeader() {
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
