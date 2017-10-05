package com.bsmwireless.common.utils.malfunction;

import com.bsmwireless.BaseTest;
import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.StorageUtil;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
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
    AppSettings mAppSettings;
    @Mock
    StorageUtil mStorageUtil;
    @Mock
    BlackBoxInteractor mBlackBoxInteractor;
    @Mock
    PreferencesManager mPreferencesManager;

    StorageCapacityJob mStorageCapacityJob;

    @Before
    public void setUp() throws Exception {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(mPreferencesManager.getBoxId()).thenReturn(0);
        when(mAppSettings.getIntervalForCheckTime()).thenReturn(0L);


        mStorageCapacityJob = spy(new StorageCapacityJob(mELDEventsInteractor, mDutyTypeManager,
                mBlackBoxInteractor, mPreferencesManager, mAppSettings, mStorageUtil));
    }

    @Test
    public void complianceNotDetectedNoEventsInDb() throws Exception {

        when(mStorageUtil.getTotalSpace()).thenReturn(100L);
        when(mStorageUtil.getAvailableSpace()).thenReturn(10L);

        when(mAppSettings.getFreeSpaceThreshold()).thenReturn(0.05);

        doReturn(Observable.just(1L)).when(mStorageCapacityJob).getIntervalObservable();

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.DATA_RECORDING_COMPLIANCE))
                .thenReturn(Flowable.empty());
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        BlackBoxModel blackBoxModel = new BlackBoxModel();
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(blackBoxModel));

        mStorageCapacityJob.start();
        mStorageCapacityJob.stop();
        verify(mELDEventsInteractor, never()).postNewELDEvent(any());
        verify(mStorageUtil, atLeastOnce()).getTotalSpace();
        verify(mStorageUtil, atLeastOnce()).getAvailableSpace();
        verify(mAppSettings, atLeastOnce()).getFreeSpaceThreshold();
    }

    @Test
    public void complianceDetectedNoEventsInDb() throws Exception {

        when(mStorageUtil.getTotalSpace()).thenReturn(100L);
        when(mStorageUtil.getAvailableSpace()).thenReturn(10L);

        when(mAppSettings.getFreeSpaceThreshold()).thenReturn(0.15);

        doReturn(Observable.just(1L)).when(mStorageCapacityJob).getIntervalObservable();

        when(mELDEventsInteractor.getLatestMalfunctionEvent(Malfunction.DATA_RECORDING_COMPLIANCE))
                .thenReturn(Flowable.empty());
        ELDEvent defaultEvent = new ELDEvent();
        defaultEvent.setEventCode(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode());
        when(mELDEventsInteractor.getEvent(any(), any(), any())).thenReturn(defaultEvent);
        when(mELDEventsInteractor.postNewELDEvent(any())).thenReturn(Single.just(1L));

        BlackBoxModel blackBoxModel = new BlackBoxModel();
        when(mBlackBoxInteractor.getData(anyInt())).thenReturn(Observable.just(blackBoxModel));

        mStorageCapacityJob.start();
        mStorageCapacityJob.stop();
        verify(mELDEventsInteractor).postNewELDEvent(any());
        verify(mStorageUtil, atLeastOnce()).getTotalSpace();
        verify(mStorageUtil, atLeastOnce()).getAvailableSpace();
        verify(mAppSettings, atLeastOnce()).getFreeSpaceThreshold();
        verify(mELDEventsInteractor).getEvent(Malfunction.DATA_RECORDING_COMPLIANCE,
                ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED, blackBoxModel);
    }
}