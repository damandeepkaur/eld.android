package com.bsmwireless.common.dagger;

import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class AutoDutyModule {
    @Singleton
    @Provides
    static AutoDutyTypeManager provideAutoDutyTypeManager(BlackBoxInteractor blackBoxInteractor,
                                                          PreferencesManager preferencesManager,
                                                          ELDEventsInteractor eventsInteractor,
                                                          DutyTypeManager dutyTypeManager,
                                                          AppSettings appSettings,
                                                          BlackBoxStateChecker blackBoxStateChecker) {
        AutoDutyTypeManager manager = new AutoDutyTypeManager(blackBoxInteractor,
                preferencesManager, eventsInteractor, dutyTypeManager, appSettings, blackBoxStateChecker);
        manager.doSubscribe();
        return manager;
    }
}
