package com.bsmwireless.common.dagger;

import com.bsmwireless.common.utils.SettingsManager;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;
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
public class MalfunctionModule {

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
                                                 SettingsManager settingsManager){
        return new StorageCapacityJob(eldEventsInteractor, dutyTypeManager, settingsManager);
    }

    @Provides
    static List<MalfunctionJob> provideJobs(SynchronizationJob synchronizationJob,
                                            TimingJob timingJob,
                                            StorageCapacityJob storageCapacityJob){
        return Arrays.asList(synchronizationJob, timingJob, storageCapacityJob);
    }
}
