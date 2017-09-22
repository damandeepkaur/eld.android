package com.bsmwireless.services.malfunction.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.services.MonitoringPresenter;
import com.bsmwireless.services.malfunction.MalfunctionServicePresenter;

import dagger.Module;
import dagger.Provides;

@Module
public final class MalfunctionServiceModule {

    @Provides
    @ActivityScope
    static MonitoringPresenter presenter(BlackBoxConnectionManager connectionManager,
                                         ELDEventsInteractor eldEventsInteractor,
                                         DutyTypeManager dutyTypeManager){
        return new MalfunctionServicePresenter(connectionManager, eldEventsInteractor, dutyTypeManager);
    }
}
