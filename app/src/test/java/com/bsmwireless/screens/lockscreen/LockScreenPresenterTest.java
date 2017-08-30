package com.bsmwireless.screens.lockscreen;

import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LockScreenPresenterTest {

    @Mock
    LockScreenView lockScreenView;
    @Mock
    DutyManager dutyManager;
    LockScreenPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new LockScreenPresenter(lockScreenView, dutyManager);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @Test
    public void testOnStart() throws Exception {
        when(dutyManager.getDutyTypeTime(DutyType.DRIVING)).thenReturn(1L);
        when(dutyManager.getDutyTypeTime(DutyType.SLEEPER_BERTH)).thenReturn(2L);
        when(dutyManager.getDutyTypeTime(DutyType.ON_DUTY)).thenReturn(3L);
        when(dutyManager.getDutyTypeTime(DutyType.OFF_DUTY)).thenReturn(4L);

        presenter.onStart();
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
}