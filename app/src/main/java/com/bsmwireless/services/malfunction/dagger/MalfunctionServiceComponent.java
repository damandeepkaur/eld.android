package com.bsmwireless.services.malfunction.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.services.malfunction.MalfunctionMonitoringService;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = MalfunctionServiceModule.class)
public interface MalfunctionServiceComponent {

    void inject(MalfunctionMonitoringService malfunctionMonitoringService);

    @Subcomponent.Builder
    interface Builder {
        MalfunctionServiceComponent build();
    }
}
