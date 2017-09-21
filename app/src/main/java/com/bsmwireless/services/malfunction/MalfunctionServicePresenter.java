package com.bsmwireless.services.malfunction;

import com.bsmwireless.common.dagger.ActivityScope;
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
