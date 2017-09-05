package com.bsmwireless.services.monitoring;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class MonitoringServicePresenter {

    /*private*/ final MonitoringServiceView mView;
    private final BlackBox blackBox;
    private Disposable monitoringDisposable;

    @Inject
    public MonitoringServicePresenter(MonitoringServiceView mView, BlackBox blackBox) {
        this.mView = mView;
        this.blackBox = blackBox;
        monitoringDisposable = Disposables.disposed();
    }

    public void stopMonitoring() {
        Timber.d("Stop monitoring");
        monitoringDisposable.dispose();
    }

    public void startMonitoring() {
        Timber.d("Start monitoring");

        if (!monitoringDisposable.isDisposed()) {
            Timber.d("Monitoring already running. Return from function");
            return;
        }

        // TODO: 31.08.2017 Need to check current driver
        // TODO: 31.08.2017 Check Duty Status. Should not be in one of YM, PU, TOWING
        monitoringDisposable = blackBox.getDataObservable()
                .subscribeOn(Schedulers.io())
                .filter(blackBoxModel -> BlackBoxResponseModel.ResponseType.MOVING
                        .equals(blackBoxModel.getResponseType()))
                .firstOrError()
                .subscribe(blackBoxModel -> mView.startLockScreen(),
                        throwable -> {
                            Timber.d(throwable, "Monitoring status error");
                            // Some exception occurs, start monitoring again
                            startMonitoring();
                        });
    }
}
