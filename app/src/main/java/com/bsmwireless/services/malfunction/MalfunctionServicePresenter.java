package com.bsmwireless.services.malfunction;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.malfunction.MalfunctionJob;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;


@ActivityScope
public final class MalfunctionServicePresenter {

    private final List<MalfunctionJob> mMalfunctionJobs;

    @Inject
    public MalfunctionServicePresenter(List<MalfunctionJob> malfunctionJobs) {

        mMalfunctionJobs = malfunctionJobs;
    }

    public void startMonitoring() {
        Timber.d("Start malfunction monitoring");
        for (MalfunctionJob job : mMalfunctionJobs) {
            job.start();
        }
    }

    public void stopMonitoring() {
        Timber.d("Stop malfunction monitoring");
        for (MalfunctionJob job : mMalfunctionJobs) {
            job.stop();
        }
    }
}
