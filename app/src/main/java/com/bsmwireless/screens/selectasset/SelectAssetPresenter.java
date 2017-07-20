package com.bsmwireless.screens.selectasset;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
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
    private LoginUserInteractor mUserInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SelectAssetPresenter(SelectAssetView view, VehiclesInteractor vehiclesInteractor, LoginUserInteractor userInteractor) {
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onCreated() {
        mDisposables.add(mVehiclesInteractor.getLastVehicles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vehicles -> {
                            if (vehicles != null && !vehicles.isEmpty()) {
                                mView.setLastVehicleList(vehicles);
                            } else {
                                mView.showEmptyLastListMessage();
                            }
                        },
                        Timber::e
                )
        );
    }

    public void onSearchTextChanged(String searchText) {
        if (searchText.isEmpty()){
            mView.setEmptyList();
        } else if (searchText.length() < 3) {
            mView.showErrorMessage();
        } else {
            mDisposables.add(mVehiclesInteractor.searchVehicles(searchText)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            vehicles -> {
                                if (vehicles != null && !vehicles.isEmpty()) {
                                    mView.setVehicleList(vehicles, searchText);
                                } else {
                                    mView.showEmptyListMessage();
                                }
                            },
                            Timber::e
                    ));
        }
    }

    public void onNotInVehicleButtonClicked() {
        mDisposables.add(mVehiclesInteractor.cleanSelectedVehicle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mView.goToHomeScreen(),
                        Timber::e));
    }

    public void onVehicleListItemClicked(Vehicle vehicle) {
        if (vehicle != null) {
            mDisposables.add(mVehiclesInteractor.pairVehicle(vehicle)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(events -> mView.goToHomeScreen(),
                            Timber::e));
        }
    }

    public void onBackPressed() {
        mUserInteractor.clearToken();
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
