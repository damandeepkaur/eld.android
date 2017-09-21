package com.bsmwireless.common.dagger;

import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.common.utils.StorageUtil;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;
import com.bsmwireless.common.utils.malfunction.MissingDataJob;
import com.bsmwireless.common.utils.malfunction.StorageCapacityJob;
import com.bsmwireless.common.utils.malfunction.SynchronizationJob;
import com.bsmwireless.common.utils.malfunction.TimingJob;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public final class MalfunctionModule {

    @Provides
    static MissingDataJob missingDataJob(ELDEventsInteractor eldEventsInteractor,
                                         DutyTypeManager dutyTypeManager){
        return new MissingDataJob(eldEventsInteractor, dutyTypeManager);
    }

    @Provides
    static TimingJob timingJob(ELDEventsInteractor eldEventsInteractor,
                               DutyTypeManager dutyTypeManager,
                               NtpClientManager ntpClientManager,
                               SettingsManager settingsManager){
        return new TimingJob(eldEventsInteractor, dutyTypeManager, ntpClientManager, settingsManager);
    }

    @Provides
    static SynchronizationJob SynchronizationJob(ELDEventsInteractor eldEventsInteractor,
                                          DutyTypeManager dutyTypeManager,
                                          BlackBoxConnectionManager blackBoxConnectionManager){
        return new SynchronizationJob(eldEventsInteractor, dutyTypeManager, blackBoxConnectionManager);
    }

    @Provides
    static StorageCapacityJob storageCapacityJob(ELDEventsInteractor eldEventsInteractor,
                                                 DutyTypeManager dutyTypeManager,
                                                 SettingsManager settingsManager,
                                                 StorageUtil storageUtil){
        return new StorageCapacityJob(eldEventsInteractor, dutyTypeManager, settingsManager, storageUtil);
    }

    @Provides
    static List<MalfunctionJob> provideJobs(SynchronizationJob synchronizationJob,
                                            TimingJob timingJob,
                                            StorageCapacityJob storageCapacityJob,
                                            MissingDataJob missingDataJob){
        return Arrays.asList(synchronizationJob, timingJob, storageCapacityJob, missingDataJob);
    }
}
