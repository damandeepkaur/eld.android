package com.bsmwireless.services.malfunction;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.services.MonitoringPresenter;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;


@ActivityScope
public final class MalfunctionServicePresenter implements MonitoringPresenter {

    private final List<MalfunctionJob> mMalfunctionJobs;

    @Inject
    MalfunctionServicePresenter(List<MalfunctionJob> malfunctionJobs) {

        mMalfunctionJobs = malfunctionJobs;
    }

    @Override
    public void startMonitoring() {
        Timber.d("Start malfunction monitoring");
        for (MalfunctionJob job : mMalfunctionJobs) {
            job.start();
        }
    }

    @Override
    public void stopMonitoring() {
        Timber.d("Stop malfunction monitoring");
        for (MalfunctionJob job : mMalfunctionJobs) {
            job.stop();
        }
    }
}
