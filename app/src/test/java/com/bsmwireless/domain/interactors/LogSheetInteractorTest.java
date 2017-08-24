package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalDao;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.ResponseMessage;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
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

    private static final String SUCCESS = "ACK"; // from API specs

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Mock
    ServiceApi mServiceApi;

    @Mock
    PreferencesManager mPreferencesManager;

    @Mock
    AppDatabase mAppDatabase;

    @Mock
    UserDao mUserDao;

    @Mock
    HomeTerminalDao mHomeTerminalDao;


    private LogSheetInteractor mLogSheetInteractor;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        mLogSheetInteractor = new LogSheetInteractor(mServiceApi, mPreferencesManager, mAppDatabase);
    }

    @Test
    public void testGetLogSheetHeadersApiCall() {
        // given
        final Long startLogDay = 1503014401L; // validity of date doesn't matter for these tests
        final Long endLogDay = 1503100801L; // validity of date doesn't matter for these tests

        final LogSheetHeader[] logSheets = {new LogSheetHeader(), new LogSheetHeader()};
        final List<LogSheetHeader> logHeaderList = Arrays.asList(logSheets);

        TestSubscriber<List<LogSheetHeader>> testSubscriber = TestSubscriber.create();

        when(mServiceApi.getLogSheets(any(Long.class), any(Long.class))).thenReturn(Observable.just(logHeaderList));

        // when
        mLogSheetInteractor.getLogSheetHeaders(startLogDay, endLogDay).subscribe(testSubscriber);

        // then
        verify(mServiceApi).getLogSheets(eq(startLogDay), eq(endLogDay));
    }

    @Test
    public void testUpdateLogSheetHeaderApiSuccess() {
        // given
        LogSheetHeader logSheetHeader = new LogSheetHeader();

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.updateLogSheetHeader(any(LogSheetHeader.class))).thenReturn(Observable.just(getSuccessResponse()));


        // when
        mLogSheetInteractor.updateLogSheetHeader(logSheetHeader).subscribe(testObserver);

        // then
        verify(mServiceApi).updateLogSheetHeader(eq(logSheetHeader));
        testObserver.assertResult(true);
    }

    /**
     * Tests when API returns 200 code with unexpected response (!= "ACK")
     */
    @Test
    public void testUpdateLogSheetHeaderApiBug() {
        // given
        LogSheetHeader logSheetHeader = new LogSheetHeader();

        String unexpectedMessage = "this is not ACK";
        ResponseMessage wrongResponse = buildResponseMessage(unexpectedMessage);

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.updateLogSheetHeader(any(LogSheetHeader.class))).thenReturn(Observable.just(wrongResponse));

        // when
        mLogSheetInteractor.updateLogSheetHeader(logSheetHeader).subscribe(testObserver);

        // then
        testObserver.assertResult(false);
    }

    @Test
    public void testUpdateLogSheetHeaderApiError() {
        // given
        LogSheetHeader logSheetHeader = new LogSheetHeader();
        String fakeErrorMessage = "busted";

        TestObserver<Boolean> testObserver = TestObserver.create();

        when(mServiceApi.updateLogSheetHeader(any(LogSheetHeader.class))).thenReturn(Observable.error(new RuntimeException(fakeErrorMessage)));

        // when
        mLogSheetInteractor.updateLogSheetHeader(logSheetHeader).subscribe(testObserver);

        // then
        testObserver.assertErrorMessage(fakeErrorMessage);
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

        when(mPreferencesManager.getDriverId()).thenReturn(driverId);
        when(mPreferencesManager.getBoxId()).thenReturn(boxId);
        when(mPreferencesManager.getVehicleId()).thenReturn(vehicleId);
        when(mAppDatabase.userDao()).thenReturn(mUserDao);
        when(mUserDao.getUserSync(any(Integer.class))).thenReturn(fakeUser);
        when(mAppDatabase.homeTerminalDao()).thenReturn(mHomeTerminalDao);
        when(mHomeTerminalDao.getHomeTerminalSync(any(Integer.class))).thenReturn(fakeHomeTerminalentity);

        TestObserver<LogSheetHeader> testObserver = TestObserver.create();

        // when
        mLogSheetInteractor.createLogSheetHeader(startLogDay).subscribe(testObserver);

        // then
        verify(mPreferencesManager).getDriverId();
        verify(mPreferencesManager).getBoxId();
        verify(mPreferencesManager).getVehicleId();
        verify(mUserDao).getUserSync(anyInt());
        verify(mHomeTerminalDao).getHomeTerminalSync(anyInt());

        // TODO: add more once LogSheetInteractor#createLogSheetHeader is completed
    }



    /**
     * Produces a ResponseMessage that imitates API call success.
     *
     * @return success ResponseMessage
     */
    private ResponseMessage getSuccessResponse() {
        return buildResponseMessage(SUCCESS);
    }

    /**
     * Produces a ResponseMessage with a given message.
     *
     * @param message a message
     * @return a ResponseMessage with a given message
     */
    private ResponseMessage buildResponseMessage(String message) {
        ResponseMessage response = new ResponseMessage();
        response.setMessage(message);
        return response;
    }



}
