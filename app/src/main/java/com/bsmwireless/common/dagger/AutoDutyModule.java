package com.bsmwireless.common.dagger;

import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class AutoDutyModule {
    @Singleton
    @Provides
    AutoDutyTypeManager provideAutoDutyTypeManager(BlackBoxInteractor blackBoxInteractor, PreferencesManager preferencesManager, ELDEventsInteractor eventsInteractor, DutyTypeManager dutyTypeManager) {
        return new AutoDutyTypeManager(blackBoxInteractor, preferencesManager, eventsInteractor, dutyTypeManager);
    }
}
