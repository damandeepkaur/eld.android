package com.bsmwireless.services.malfunction.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;
import com.bsmwireless.services.MonitoringPresenter;
import com.bsmwireless.services.malfunction.MalfunctionServicePresenter;

import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public final class MalfunctionServiceModule {

    @Provides
    @ActivityScope
    static MonitoringPresenter presenter(List<MalfunctionJob> malfunctionJobs){
        return new MalfunctionServicePresenter(malfunctionJobs);
    }
}
