package com.bsmwireless.services.monitoring;


import com.bsmwireless.BaseTest;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.BlackBoxModel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MonitoringServicePresenterTest extends BaseTest {

    @Mock
    MonitoringServiceView mView;
    @Mock
    PreferencesManager mPreferencesManager;
    @Mock
    BlackBoxConnectionManager mBlackBox;
    @Mock
    AccountManager mAccountManager;
    @Mock
    BlackBoxStateChecker mChecker;

    private DutyTypeManager dutyTypeManager;
    private MonitoringServicePresenter presenter;

    @Before
    public void setUp() throws Exception {
        dutyTypeManager = spy(new DutyTypeManager(mPreferencesManager));
        presenter = new MonitoringServicePresenter(mView,
                mBlackBox,
                dutyTypeManager,
                mAccountManager,
                mChecker);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

    }

    @Test
    public void testStartMonitoringCompleted() throws Exception {
        when(mBlackBox.getDataObservable()).thenReturn(Observable.empty());

        when(mAccountManager.isCurrentUserDriver()).thenReturn(false);
        when(mChecker.isMoving(any())).thenReturn(false);

        presenter.startMonitoring();
        verify(mBlackBox).getDataObservable();
        verify(mView, never()).startLockScreen();
    }

    @Test
    @Ignore("Handler not mocked")
    public void testStartMonitoring() throws Exception {
        final BlackBoxModel blackBoxModel = mock(BlackBoxModel.class);
        when(blackBoxModel.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final Subject<BlackBoxModel> subject = BehaviorSubject.create();
        when(mBlackBox.getDataObservable()).thenReturn(subject);

        when(mPreferencesManager.getDutyType()).thenReturn(0);

        subject.onNext(blackBoxModel);
        presenter.startMonitoring();

        verify(mBlackBox).getDataObservable();
        verify(mView).startLockScreen();
    }

    @Test
    public void testStartMonitoringTwice() throws Exception {

//        dutyTypeManager.setDutyType(DutyType.ON_DUTY, false);

        final Subject<BlackBoxModel> subject = PublishSubject.create();
        when(mBlackBox.getDataObservable()).thenReturn(subject);
        presenter.startMonitoring();
        presenter.startMonitoring();
        verify(mBlackBox).getDataObservable();
        subject.onComplete();
    }

    @Test
    public void testStartMonitoringTwiceAfterCompleted() throws Exception {
        when(mBlackBox.getDataObservable()).thenReturn(Observable.empty());
        presenter.startMonitoring();
        presenter.startMonitoring();
        verify(mBlackBox, times(2)).getDataObservable();
    }

    @Test
    @Ignore("Handler not mocked")
    public void testEventReceiving() throws Exception {

        final BlackBoxModel blackBoxModelFirst = mock(BlackBoxModel.class);
        when(blackBoxModelFirst.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.IGNITION_ON);

        final BlackBoxModel blackBoxModelSecond = mock(BlackBoxModel.class);
        when(blackBoxModelSecond.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final Subject<BlackBoxModel> subject = PublishSubject.create();
        when(mBlackBox.getDataObservable()).thenReturn(subject);

        presenter.startMonitoring();
        subject.onNext(blackBoxModelFirst);
        verify(mView, never()).startLockScreen();
        subject.onNext(blackBoxModelSecond);
        verify(mView).startLockScreen();
    }

    @Test
    public void testStopMonitoring() throws Exception {
        final BlackBoxModel blackBoxModelFirst = mock(BlackBoxModel.class);
        when(blackBoxModelFirst.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.IGNITION_ON);

        final BlackBoxModel blackBoxModelSecond = mock(BlackBoxModel.class);
        when(blackBoxModelSecond.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final Subject<BlackBoxModel> subject = PublishSubject.create();
        when(mBlackBox.getDataObservable()).thenReturn(subject);

        presenter.startMonitoring();
        subject.onNext(blackBoxModelFirst);
        verify(mView, never()).startLockScreen();
        presenter.stopMonitoring();
        subject.onNext(blackBoxModelSecond);
        verify(mView, never()).startLockScreen();
    }
}