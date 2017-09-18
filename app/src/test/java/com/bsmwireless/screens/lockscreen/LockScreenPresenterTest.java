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
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LockScreenPresenterTest {

    @Mock
    LockScreenView mLockScreenView;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    BlackBox mBlackBox;
    @Mock
    PreferencesManager mPreferencesManager;
    @Mock
    AccountManager mAccountManager;
    @Mock
    ELDEventsInteractor mELDEventsInteractor;

    LockScreenPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mPresenter = new LockScreenPresenter(
                mDutyTypeManager,
                new BlackBoxConnectionManagerImpl(mBlackBox),
                mPreferencesManager,
                new BlackBoxSimpleChecker(),
                mELDEventsInteractor,
                TimeUnit.MILLISECONDS.toMillis(1),
                TimeUnit.MILLISECONDS.toMillis(1),
                mAccountManager);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @Test
    public void testStatuses() throws Exception {
        given(mDutyTypeManager.getDutyTypeTime(DutyType.DRIVING)).willReturn(1L);
        given(mDutyTypeManager.getDutyTypeTime(DutyType.SLEEPER_BERTH)).willReturn(2L);
        given(mDutyTypeManager.getDutyTypeTime(DutyType.ON_DUTY)).willReturn(3L);
        given(mDutyTypeManager.getDutyTypeTime(DutyType.OFF_DUTY)).willReturn(4L);

        given(mBlackBox.getDataObservable()).willReturn(Observable.empty());

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
        given(mBlackBox.getDataObservable()).willReturn(Observable.empty());
        mPresenter.onStart(mLockScreenView);
        mPresenter.switchCoDriver();
        verify(mLockScreenView).openCoDriverDialog();
    }

    @Test
    public void startMonitoring() throws Exception {
        given(mBlackBox.getDataObservable()).willReturn(Observable.empty());
        mPresenter.onStart(mLockScreenView);
        verify(mLockScreenView).removeAnyPopup();
    }

    @Test
    public void idling() throws Exception {

        final BlackBoxModel stoppedMock = mock(BlackBoxModel.class);
        given(stoppedMock.getResponseType()).willReturn(BlackBoxResponseModel.ResponseType.STOPPED);

        final BlackBoxModel anyMock = mock(BlackBoxModel.class);
        given(anyMock.getResponseType()).willReturn(BlackBoxResponseModel.ResponseType.MOVING);

        final BehaviorSubject<BlackBoxModel> subject = BehaviorSubject.create();
        given(mBlackBox.getDataObservable()).willReturn(subject);

        given(mELDEventsInteractor.postNewELDEvent(any())).willReturn(Single.just(1L));

        mPresenter.onStart(mLockScreenView);
        subject.onNext(stoppedMock);
        subject.onNext(anyMock);
        subject.onComplete();
        verify(mLockScreenView).closeLockScreen();
    }

    @Test
    public void startMonitoringIgnitionOff() throws Exception {

        final BlackBoxModel ignitionOffMock = mock(BlackBoxModel.class);
        given(ignitionOffMock.getResponseType()).willReturn(BlackBoxResponseModel.ResponseType.IGNITION_OFF);

        final BehaviorSubject<BlackBoxModel> subject = BehaviorSubject.create();
        given(mBlackBox.getDataObservable()).willReturn(subject);

        given(mELDEventsInteractor.postNewELDEvent(any())).willReturn(Single.just(1L));

        mPresenter.onStart(mLockScreenView);
        verify(mLockScreenView).removeAnyPopup();
        subject.onNext(ignitionOffMock);
        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mLockScreenView).showIgnitionOffDetectedDialog();

    }
}