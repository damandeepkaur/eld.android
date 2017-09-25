package com.bsmwireless.screens.lockscreen;

import com.bsmwireless.common.utils.BlackBoxSimpleChecker;
import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManagerImpl;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LockScreenPresenterTest {

    @Mock
    LockScreenView mLockScreenView;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    PreferencesManager mPreferencesManager;
    @Mock
    AccountManager mAccountManager;
    @Mock
    ELDEventsInteractor mELDEventsInteractor;
    @Mock
    AppSettings mAppSettings;
    @Mock
    BlackBoxInteractor mBlackBoxInteractor;

    LockScreenPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mAppSettings.lockScreenDisconnectionTimeout()).thenReturn(1L);
        when(mAppSettings.lockScreenIdlingTimeout()).thenReturn(1L);


        mPresenter = spy(new LockScreenPresenter(
                mDutyTypeManager,
                mBlackBoxInteractor,
                mPreferencesManager,
                new BlackBoxSimpleChecker(),
                mELDEventsInteractor,
                mAppSettings,
                mAccountManager));

        when(mPreferencesManager.getBoxId()).thenReturn(0);

        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @Test
    public void testStatuses() throws Exception {
        doNothing().when(mPresenter).startTimer();

        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.DRIVING);

        when(mDutyTypeManager.getDutyTypeTime(DutyType.DRIVING)).thenReturn(1L);
        when(mDutyTypeManager.getDutyTypeTime(DutyType.SLEEPER_BERTH)).thenReturn(2L);
        when(mDutyTypeManager.getDutyTypeTime(DutyType.ON_DUTY)).thenReturn(3L);
        when(mDutyTypeManager.getDutyTypeTime(DutyType.OFF_DUTY)).thenReturn(4L);

        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.empty());

        mPresenter.onStart(mLockScreenView);
        verify(mLockScreenView).setTimeForDutyType(DutyType.DRIVING, 1L);
        verify(mLockScreenView).setTimeForDutyType(DutyType.SLEEPER_BERTH, 2L);
        verify(mLockScreenView).setTimeForDutyType(DutyType.ON_DUTY, 3L);
        verify(mLockScreenView).setTimeForDutyType(DutyType.OFF_DUTY, 4L);
    }

    @Test
    public void testSwitchCoDriverInNonStartedState() throws Exception {
        mPresenter.switchCoDriver();
        verify(mLockScreenView, never()).openCoDriverDialog();
    }

    @Test
    public void testSwitchCoDriver() throws Exception {
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.empty());
        doNothing().when(mPresenter).startTimer();

        mPresenter.onStart(mLockScreenView);
        mPresenter.switchCoDriver();
        verify(mLockScreenView).openCoDriverDialog();
    }

    @Test
    public void startMonitoring() throws Exception {
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.empty());
        doNothing().when(mPresenter).startTimer();

        mPresenter.onStart(mLockScreenView);
        verify(mLockScreenView).removeAnyPopup();
    }

    @Test
    public void idling() throws Exception {

        final BlackBoxModel stoppedMock = mock(BlackBoxModel.class);
        when(stoppedMock.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.STOPPED);

        final BlackBoxModel anyMock = mock(BlackBoxModel.class);
        when(anyMock.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final BehaviorSubject<BlackBoxModel> subject = BehaviorSubject.create();
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(subject);

        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        doNothing().when(mPresenter).startTimer();

        mPresenter.onStart(mLockScreenView);
        subject.onNext(stoppedMock);
        subject.onNext(stoppedMock);
        subject.onComplete();
        verify(mLockScreenView).closeLockScreen();
    }

    @Test
    public void startMonitoringIgnitionOff() throws Exception {

        final BlackBoxModel ignitionOffMock = mock(BlackBoxModel.class);
        when(ignitionOffMock.getResponseType()).thenReturn(BlackBoxResponseModel.ResponseType.IGNITION_OFF);

        final BehaviorSubject<BlackBoxModel> subject = BehaviorSubject.create();
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(subject);

        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));
        doNothing().when(mPresenter).startTimer();

        mPresenter.onStart(mLockScreenView);
        verify(mLockScreenView).removeAnyPopup();
        subject.onNext(ignitionOffMock);
        subject.onNext(ignitionOffMock);


        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mLockScreenView).showIgnitionOffDetectedDialog();

    }
}