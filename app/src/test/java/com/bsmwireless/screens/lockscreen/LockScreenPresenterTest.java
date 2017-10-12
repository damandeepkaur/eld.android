package com.bsmwireless.screens.lockscreen;

import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
    AccountManager mAccountManager;
    @Mock
    ELDEventsInteractor mELDEventsInteractor;
    @Mock
    AppSettings mAppSettings;
    @Mock
    AutoDutyTypeManager mAutoDutyTypeManager;

    LockScreenPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mAppSettings.lockScreenDisconnectionTimeout()).thenReturn(1L);
        when(mAppSettings.lockScreenIdlingTimeout()).thenReturn(1L);
        when(mAppSettings.ignitionOffDialogTimeout()).thenReturn(1L);

        mPresenter = spy(new LockScreenPresenter(
                mDutyTypeManager,
                mELDEventsInteractor,
                mAppSettings,
                mAccountManager,
                mAutoDutyTypeManager));

        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @Test
    public void testStatuses() throws Exception {
        doNothing().when(mPresenter).startTimer();

        BehaviorSubject<DutyType> subject = BehaviorSubject.create();
        doReturn(subject).when(mPresenter).createChangingDutyTypeObservable();

        when(mDutyTypeManager.getDutyTypeTime(DutyType.DRIVING)).thenReturn(1L);
        when(mDutyTypeManager.getDutyTypeTime(DutyType.SLEEPER_BERTH)).thenReturn(2L);
        when(mDutyTypeManager.getDutyTypeTime(DutyType.ON_DUTY)).thenReturn(3L);
        when(mDutyTypeManager.getDutyTypeTime(DutyType.OFF_DUTY)).thenReturn(4L);

        mPresenter.bind(mLockScreenView);
        subject.onNext(DutyType.DRIVING);
        subject.onComplete();

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
        doNothing().when(mPresenter).startTimer();

        mPresenter.bind(mLockScreenView);
        mPresenter.switchCoDriver();
        verify(mLockScreenView).openCoDriverDialog();
    }
}