package com.bsmwireless.screens.home;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.observers.DutyManagerObservable;
import com.bsmwireless.data.storage.DutyTypeManager;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@ActivityScope
public final class HomePresenter {

    private final DutyTypeManager mDutyTypeManager;
    private HomeView mHomeView;
    private final CompositeDisposable mCompositeDisposable;

    @Inject
    public HomePresenter(DutyTypeManager dutyTypeManager) {
        mDutyTypeManager = dutyTypeManager;
        mCompositeDisposable = new CompositeDisposable();
    }

    public void onStart(HomeView homeView) {
        mHomeView = homeView;
        startDutyTypeMonitoring();
    }

    public void onStop() {
        mHomeView = null;
        mCompositeDisposable.dispose();
    }

    public void onHoursOfService() {
        if (mHomeView == null) {
            return;
        }

        mHomeView.startHoursOfService();
    }

    public void onPreTrip() {

    }

    public void onPostTrip() {

    }

    public void onInspections() {

    }

    private void startDutyTypeMonitoring() {
        Disposable disposable = DutyManagerObservable.create(mDutyTypeManager)
                .retry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dutyType -> {
                    if (mHomeView != null) {
                        mHomeView.dutyStatusChanged(dutyType);
                    }
                });
        mCompositeDisposable.add(disposable);
    }
}
