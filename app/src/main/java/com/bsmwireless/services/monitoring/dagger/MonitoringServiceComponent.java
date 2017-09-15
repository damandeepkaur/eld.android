package com.bsmwireless.services.monitoring.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.services.monitoring.MonitoringServiceView;
import com.bsmwireless.services.monitoring.StatusMonitoringService;

import dagger.BindsInstance;
import dagger.Subcomponent;

@Subcomponent
@ActivityScope
public interface MonitoringServiceComponent {

    void inject(StatusMonitoringService statusMonitoringService);

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance Builder view(MonitoringServiceView view);
        MonitoringServiceComponent build();
    }
}
