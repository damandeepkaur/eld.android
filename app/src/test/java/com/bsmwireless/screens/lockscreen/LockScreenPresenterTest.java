package com.bsmwireless.screens.lockscreen;

import com.bsmwireless.common.utils.BlackBoxSimpleChecker;
import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManagerImpl;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LockScreenPresenterTest {

    @Mock
    LockScreenView lockScreenView;
    @Mock
    DutyTypeManager dutyManager;
    @Mock
    BlackBox blackBox;
    @Mock
    PreferencesManager preferencesManager;
    @Mock
    AccountManager accountManager;
    @Mock
    ELDEventsInteractor eventsInteractor;

    LockScreenPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new LockScreenPresenter(
                dutyManager,
                new BlackBoxConnectionManagerImpl(blackBox),
                preferencesManager,
                new BlackBoxSimpleChecker(),
                eventsInteractor,
                TimeUnit.MILLISECONDS.toMillis(1),
                TimeUnit.MILLISECONDS.toMillis(1),
                accountManager);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @Test
    public void testStatuses() throws Exception {
        when(dutyManager.getDutyTypeTime(DutyType.DRIVING)).thenReturn(1L);
        when(dutyManager.getDutyTypeTime(DutyType.SLEEPER_BERTH)).thenReturn(2L);
        when(dutyManager.getDutyTypeTime(DutyType.ON_DUTY)).thenReturn(3L);
        when(dutyManager.getDutyTypeTime(DutyType.OFF_DUTY)).thenReturn(4L);

        when(blackBox.getDataObservable()).thenReturn(Observable.empty());

        presenter.onStart(lockScreenView);
        verify(lockScreenView).setTimeForDutyType(DutyType.DRIVING, 1L);
        verify(lockScreenView).setTimeForDutyType(DutyType.SLEEPER_BERTH, 2L);
        verify(lockScreenView).setTimeForDutyType(DutyType.ON_DUTY, 3L);
        verify(lockScreenView).setTimeForDutyType(DutyType.OFF_DUTY, 4L);
    }

    @Test
    public void testSwitchCoDriver() throws Exception {
        presenter.switchCoDriver();
        verify(lockScreenView).openCoDriverDialog();
    }

    @Test
    public void startMonitoring() throws Exception {
        when(blackBox.getDataObservable()).thenReturn(Observable.empty());
        presenter.onStart(lockScreenView);
        verify(lockScreenView).removeAnyPopup();
    }

    @Test
    public void idling() throws Exception {

        final BlackBoxModel stoppedMock = mock(BlackBoxModel.class);
        when(stoppedMock.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.STOPPED);

        final BlackBoxModel anyMock = mock(BlackBoxModel.class);
        when(anyMock.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final BehaviorSubject<BlackBoxModel> subject = BehaviorSubject.create();
        when(blackBox.getDataObservable()).thenReturn(subject);
        presenter.onStart(lockScreenView);
        subject.onNext(stoppedMock);
        subject.onNext(anyMock);
        subject.onComplete();
        verify(lockScreenView, times(2)).removeAnyPopup();
        verify(lockScreenView).closeLockScreen();
    }

    @Test
    public void startMonitoringIgnitionOff() throws Exception {

        final BlackBoxModel ignitionOffMock = mock(BlackBoxModel.class);
        when(ignitionOffMock.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.IGNITION_OFF);

        final BehaviorSubject<BlackBoxModel> subject = BehaviorSubject.create();
        when(blackBox.getDataObservable()).thenReturn(subject);
        presenter.onStart(lockScreenView);
        verify(lockScreenView).removeAnyPopup();
        subject.onNext(ignitionOffMock);
        verify(lockScreenView).showIgnitionOffDetectedDialog();

    }
}