package com.bsmwireless.common.dagger;

import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;
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
    static List<MalfunctionJob> provideJobs(SynchronizationJob synchronizationJob,
                                            TimingJob timingJob) {
        return Arrays.asList(synchronizationJob, timingJob);
    }
}
