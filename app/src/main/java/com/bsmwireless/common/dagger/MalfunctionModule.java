package com.bsmwireless.common.dagger;

import com.bsmwireless.common.utils.StorageUtil;
import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;
import com.bsmwireless.common.utils.malfunction.MissingDataJob;
import com.bsmwireless.common.utils.malfunction.StorageCapacityJob;
import com.bsmwireless.common.utils.malfunction.SynchronizationJob;
import com.bsmwireless.common.utils.malfunction.TimingJob;
import com.bsmwireless.data.network.NtpClientManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public final class MalfunctionModule {

    @Provides
    static MissingDataJob missingDataJob(ELDEventsInteractor eldEventsInteractor,
                                         DutyTypeManager dutyTypeManager,
                                         BlackBoxInteractor blackBoxInteractor,
                                         PreferencesManager preferencesManager) {
        return new MissingDataJob(eldEventsInteractor, dutyTypeManager, blackBoxInteractor, preferencesManager);
    }

    @Provides
    static TimingJob timingJob(ELDEventsInteractor eldEventsInteractor,
                               DutyTypeManager dutyTypeManager,
                               NtpClientManager ntpClientManager,
                               AppSettings appSettings,
                               BlackBoxInteractor blackBoxInteractor,
                               PreferencesManager preferencesManager) {
        return new TimingJob(eldEventsInteractor, dutyTypeManager, ntpClientManager, appSettings,
                blackBoxInteractor, preferencesManager);
    }

    @Provides
    static SynchronizationJob SynchronizationJob(ELDEventsInteractor eldEventsInteractor,
                                                 DutyTypeManager dutyTypeManager,
                                                 BlackBoxInteractor blackBoxInteractor,
                                                 PreferencesManager preferencesManager) {
        return new SynchronizationJob(eldEventsInteractor, dutyTypeManager, blackBoxInteractor,
                preferencesManager);
    }

    @Provides
    static StorageCapacityJob storageCapacityJob(ELDEventsInteractor eldEventsInteractor,
                                                 DutyTypeManager dutyTypeManager,
                                                 AppSettings settingsManager,
                                                 StorageUtil storageUtil,
                                                 BlackBoxInteractor blackBoxInteractor,
                                                 PreferencesManager preferencesManager) {
        return new StorageCapacityJob(eldEventsInteractor, dutyTypeManager, blackBoxInteractor,
                preferencesManager, settingsManager, storageUtil);
    }

    @Provides
    static List<MalfunctionJob> provideJobs(SynchronizationJob synchronizationJob,
                                            TimingJob timingJob,
                                            StorageCapacityJob storageCapacityJob,
                                            MissingDataJob missingDataJob) {
        return Arrays.asList(synchronizationJob, timingJob, storageCapacityJob, missingDataJob);
    }
}
