package com.bsmwireless.screens.selectasset;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.Vehicle;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class SelectAssetPresenter {
    private SelectAssetView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SelectAssetPresenter(SelectAssetView view, VehiclesInteractor vehiclesInteractor) {
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onSearchTextChanged(String searchText) {
        if (searchText.isEmpty() || searchText.length() < 3) {
            mView.showEmptyList();
            mView.showErrorMessage();
        } else {
            mDisposables.add(mVehiclesInteractor.searchVehicles(searchText)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            vehicles -> {
                                if (vehicles != null && !vehicles.isEmpty()) {
                                    mView.setVehicleList(vehicles);
                                } else {
                                    mView.showEmptyList();
                                }
                            },
                            Timber::e
                    ));
        }
    }

    public void onCancelButtonPressed() {
        mView.goToMainScreen();
    }

    public void onNotInVehicleButtonClicked() {
        mDisposables.add(mVehiclesInteractor.cleanSelectedVehicle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mView.goToMainScreen(),
                        Timber::e));
    }

    public void onVehicleListItemClicked(Vehicle vehicle) {
        mDisposables.add(mVehiclesInteractor.pairVehicle(vehicle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> mView.goToMainScreen(),
                        Timber::e));
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
