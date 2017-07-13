package com.bsmwireless.screens.selectasset;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.Vehicle;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
public class SelectAssetPresenter {
    private SelectAssetView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private BlackBoxInteractor mBlackBoxInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SelectAssetPresenter(SelectAssetView view, VehiclesInteractor vehiclesInteractor, BlackBoxInteractor blackBoxInteractor) {
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mBlackBoxInteractor = blackBoxInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onSearchTextChanged(String searchText) {
        if (searchText.isEmpty() || searchText.length() < 3) {
            mView.showEmptyList();
            mView.showErrorMessage();
        } else {
            mDisposables.add(mVehiclesInteractor.searchVehicles(searchText)
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mView.goToMainScreen(),
                        Timber::e));
    }

    public void onVehicleListItemClicked(Vehicle vehicle) {
        ELDDriverStatus status = new ELDDriverStatus();
        int id = mVehiclesInteractor.getDriverId();

        //TODO: get real data for hos
        status.setEngineHours(50);

        status.setMobileTime(System.currentTimeMillis());
        status.setDriverId(id);
        status.setVehicleId(vehicle.getId());
        status.setBoxId(vehicle.getBoxId());

        mDisposables.add(mVehiclesInteractor.saveSelectedVehicle(vehicle)
                .andThen(mVehiclesInteractor.getTimezone(id))
                .flatMap(timeZone -> {
                    status.setTimezone(timeZone);

                    return mBlackBoxInteractor.getData();
                })
                .flatMap(blackBox -> {
                    status.setOdometer(blackBox.getOdometer());
                    status.setLat(blackBox.getLat());
                    status.setLng(blackBox.getLon());

                    return mVehiclesInteractor.pairVehicle(status);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(statuses -> {
                            //TODO: store statuses
                            mView.goToMainScreen();
                        },
                        Timber::e));
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
