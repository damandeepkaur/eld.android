package com.bsmwireless.services.monitoring;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.blackbox.BlackBox;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class MonitoringServicePresenter {

    final MonitoringServiceView mView;
    private final BlackBox mBlackBox;
    private Disposable mMonitoringDisposable;
    private final DutyTypeManager mDutyTypeManager;
    private final AccountManager mAccountManager;

    @Inject
    public MonitoringServicePresenter(MonitoringServiceView mView,
                                      BlackBox blackBox,
                                      DutyTypeManager dutyTypeManager,
                                      AccountManager accountManager) {
        this.mView = mView;
        this.mBlackBox = blackBox;
        this.mDutyTypeManager = dutyTypeManager;
        this.mAccountManager = accountManager;
        mMonitoringDisposable = Disposables.disposed();
    }

    public void stopMonitoring() {
        Timber.d("Stop monitoring");
        mMonitoringDisposable.dispose();
    }

    public void startMonitoring() {
        Timber.d("Start monitoring");

        if (!mMonitoringDisposable.isDisposed()) {
            Timber.d("Monitoring already running. Return from function");
            return;
        }

        mMonitoringDisposable = Observable
                .combineLatest(
                        mBlackBox.getDataObservable(),
                        new DutyManagerObservable(mDutyTypeManager),
                        Result::new)
                .subscribeOn(Schedulers.io())
                // Some callback can be called from different thread(UI for example) that's why observeOn is called
                .observeOn(Schedulers.io())
                .filter(this::checkConditions)
                .firstOrError()
                .subscribe(blackBoxModel -> mView.startLockScreen(),
                        throwable -> {
                            Timber.d(throwable, "Monitoring status error");
                            // Some exception occurs, start monitoring again
//                            startMonitoring();
                        });
    }

    boolean checkConditions(Result result) {
        return mAccountManager.isCurrentUserDriver() &&
                // Now blackbox doesn't return MOVING response type. Workaround: check sensor state
//                BlackBoxResponseModel.ResponseType.MOVING == result.blackBoxModel.getResponseType() &&
                result.blackBoxModel.getSensorState(BlackBoxSensorState.MOVING) &&
                (result.dutyType != DutyType.PERSONAL_USE && result.dutyType != DutyType.YARD_MOVES);
    }

    private final class DutyManagerObservable extends Observable<DutyType> {

        private final DutyTypeManager manager;

        DutyManagerObservable(DutyTypeManager dutyTypeManager) {
            this.manager = dutyTypeManager;
        }

        @Override
        protected void subscribeActual(Observer<? super DutyType> observer) {
            DutyManagerListener listener = new DutyManagerListener(manager, observer);
            observer.onSubscribe(listener);
            manager.addListener(listener);
        }
    }

    private final static class DutyManagerListener implements DutyTypeManager.DutyTypeListener, Disposable {

        private final DutyTypeManager dutyTypeManager;
        private final Observer<? super DutyType> observer;
        private final AtomicBoolean isDisposed;

        DutyManagerListener(DutyTypeManager dutyTypeManager, Observer<? super DutyType> observer) {
            this.dutyTypeManager = dutyTypeManager;
            this.observer = observer;
            isDisposed = new AtomicBoolean(false);
        }

        @Override
        public void onDutyTypeChanged(DutyType dutyType) {
            if (!isDisposed()) {
                observer.onNext(dutyType);
            }
        }

        @Override
        public void dispose() {
            dutyTypeManager.removeListener(this);
            isDisposed.set(true);
        }

        @Override
        public boolean isDisposed() {
            return isDisposed.get();
        }
    }

    private final static class Result {
        private final BlackBoxModel blackBoxModel;
        private final DutyType dutyType;

        Result(BlackBoxModel blackBoxModel, DutyType dutyType) {
            this.blackBoxModel = blackBoxModel;
            this.dutyType = dutyType;
        }
    }

}
