package com.bsmwireless.services.malfunction;

import com.bsmwireless.common.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface MalfunctionServiceComponent {

    void inject(MalfunctionMonitoringService malfunctionMonitoringService);

    @Subcomponent.Builder
    interface Builder {
        MalfunctionServiceComponent build();
    }
}
