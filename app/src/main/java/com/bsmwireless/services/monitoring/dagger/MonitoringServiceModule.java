package com.bsmwireless.services.monitoring.dagger;

import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.services.MonitoringPresenter;
import com.bsmwireless.services.monitoring.MonitoringServicePresenter;
import com.bsmwireless.services.monitoring.MonitoringServiceView;

import dagger.Module;
import dagger.Provides;

@Module
public final class MonitoringServiceModule {

    @Provides
    static MonitoringPresenter presenter(MonitoringServiceView view, BlackBoxConnectionManager blackBoxConnectionManager,
                                         DutyTypeManager dutyTypeManager,
                                         AccountManager accountManager,
                                         BlackBoxStateChecker checker) {
        return new MonitoringServicePresenter(view, blackBoxConnectionManager, dutyTypeManager,
                accountManager, checker);
    }
}
