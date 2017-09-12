package com.bsmwireless.services.monitoring;


import com.bsmwireless.BaseTest;
import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MonitoringServicePresenterTest extends BaseTest {

    @Mock
    MonitoringServiceView view;
    @Mock
    PreferencesManager preferencesManager;
    @Mock
    BlackBox blackBox;

    private DutyTypeManager dutyTypeManager;
    private MonitoringServicePresenter presenter;

    @Before
    public void setUp() throws Exception {
        dutyTypeManager = spy(new DutyTypeManager(preferencesManager));
        presenter = new MonitoringServicePresenter(view, blackBox, dutyTypeManager, null, checker);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

    }

    @Test
    public void testStartMonitoringCompleted() throws Exception {
        when(blackBox.getDataObservable()).thenReturn(Observable.empty());
        presenter.startMonitoring();
        verify(blackBox).getDataObservable();
        verify(view, never()).startLockScreen();
    }

    @Test
    @Ignore // Handler not Mocked
    public void testStartMonitoring() throws Exception {
        final BlackBoxModel blackBoxModel = mock(BlackBoxModel.class);
        when(blackBoxModel.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final Subject<BlackBoxModel> subject = BehaviorSubject.create();
        when(blackBox.getDataObservable()).thenReturn(subject);

        when(preferencesManager.getDutyType()).thenReturn(0);

        subject.onNext(blackBoxModel);
        presenter.startMonitoring();

        verify(blackBox).getDataObservable();
        verify(view).startLockScreen();
    }

    @Test
    @Ignore // Handler not Mocked
    public void testStartMonitoringTwice() throws Exception {

//        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.newThread());

//        dutyTypeManager.setDutyType(DutyType.ON_DUTY, false);

        final Subject<BlackBoxModel> subject = PublishSubject.create();
        when(blackBox.getDataObservable()).thenReturn(subject);
        presenter.startMonitoring();
        presenter.startMonitoring();
        verify(blackBox).getDataObservable();
        subject.onComplete();
    }

    @Test
    public void testStartMonitoringTwiceAfterCompleted() throws Exception {
        when(blackBox.getDataObservable()).thenReturn(Observable.empty());
        presenter.startMonitoring();
        presenter.startMonitoring();
        verify(blackBox, times(2)).getDataObservable();
    }

    @Test
    @Ignore // Handler not Mocked
    public void testEventReceiving() throws Exception {

        final BlackBoxModel blackBoxModelFirst = mock(BlackBoxModel.class);
        when(blackBoxModelFirst.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.IGNITION_ON);

        final BlackBoxModel blackBoxModelSecond = mock(BlackBoxModel.class);
        when(blackBoxModelSecond.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final Subject<BlackBoxModel> subject = PublishSubject.create();
        when(blackBox.getDataObservable()).thenReturn(subject);

        presenter.startMonitoring();
        subject.onNext(blackBoxModelFirst);
        verify(view, never()).startLockScreen();
        subject.onNext(blackBoxModelSecond);
        verify(view).startLockScreen();
    }

    @Test
    @Ignore // Handler not Mocked
    public void testStopMonitoring() throws Exception {
        final BlackBoxModel blackBoxModelFirst = mock(BlackBoxModel.class);
        when(blackBoxModelFirst.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.IGNITION_ON);

        final BlackBoxModel blackBoxModelSecond = mock(BlackBoxModel.class);
        when(blackBoxModelSecond.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final Subject<BlackBoxModel> subject = PublishSubject.create();
        when(blackBox.getDataObservable()).thenReturn(subject);

        presenter.startMonitoring();
        subject.onNext(blackBoxModelFirst);
        verify(view, never()).startLockScreen();
        presenter.stopMonitoring();
        subject.onNext(blackBoxModelSecond);
        verify(view, never()).startLockScreen();
    }
}