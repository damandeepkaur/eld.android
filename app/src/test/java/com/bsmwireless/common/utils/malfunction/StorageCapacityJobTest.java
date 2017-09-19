package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.BaseTest;
import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.common.utils.StorageUtil;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.widgets.alerts.DutyType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StorageCapacityJobTest extends BaseTest {

    @Mock
    ELDEventsInteractor mELDEventsInteractor;
    @Mock
    DutyTypeManager mDutyTypeManager;
    @Mock
    SettingsManager mSettingsManager;
    @Mock
    StorageUtil mStorageUtil;

    StorageCapacityJob mStorageCapacityJob;

    @Before
    public void setUp() throws Exception {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        mStorageCapacityJob = spy(new StorageCapacityJob(mELDEventsInteractor, mDutyTypeManager,
                mSettingsManager, mStorageUtil));
    }

    @Test
    public void complianceNotDetectedNoEventsInDb() throws Exception {

        when(mStorageUtil.getTotalSpace()).thenReturn(100L);
        when(mStorageUtil.getAvailableSpace()).thenReturn(10L);

        when(mSettingsManager.getFreeSpaceThreshold()).thenReturn(0.05);

        when(mStorageCapacityJob.getIntervalObservable()).thenReturn(Observable.just(1L));

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.DATA_RECORDING_COMPLIANCE))
                .thenReturn(Maybe.empty());
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);
        when(mELDEventsInteractor.getEvent(any(DutyType.class))).thenReturn(new ELDEvent());
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        mStorageCapacityJob.start();
        mStorageCapacityJob.stop();
        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
        verify(mStorageUtil).getTotalSpace();
        verify(mStorageUtil).getAvailableSpace();
        verify(mSettingsManager).getFreeSpaceThreshold();
    }

    @Test
    public void complianceDetectedNoEventsInDb() throws Exception {

        when(mStorageUtil.getTotalSpace()).thenReturn(100L);
        when(mStorageUtil.getAvailableSpace()).thenReturn(10L);

        when(mSettingsManager.getFreeSpaceThreshold()).thenReturn(0.15);

        when(mStorageCapacityJob.getIntervalObservable()).thenReturn(Observable.just(1L));

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.DATA_RECORDING_COMPLIANCE))
                .thenReturn(Maybe.empty());
        when(mDutyTypeManager.getDutyType()).thenReturn(DutyType.ON_DUTY);
        when(mELDEventsInteractor.getEvent(any(DutyType.class))).thenReturn(new ELDEvent());
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        mStorageCapacityJob.start();
        mStorageCapacityJob.stop();
        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mStorageUtil).getTotalSpace();
        verify(mStorageUtil).getAvailableSpace();
        verify(mSettingsManager).getFreeSpaceThreshold();
    }
}