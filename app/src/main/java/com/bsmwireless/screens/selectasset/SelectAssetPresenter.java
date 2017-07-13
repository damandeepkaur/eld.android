package com.bsmwireless.screens.selectasset;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Vehicle;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
public class SelectAssetPresenter {
    private SelectAssetView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private ELDEventsInteractor mELDEventsInteractor;
    private BlackBoxInteractor mBlackBoxInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SelectAssetPresenter(SelectAssetView view, VehiclesInteractor vehiclesInteractor, ELDEventsInteractor eventsInteractor, BlackBoxInteractor blackBoxInteractor) {
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mELDEventsInteractor = eventsInteractor;
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
        ELDEvent event = new ELDEvent();
        int id = mVehiclesInteractor.getDriverId();

        //TODO: get real data for hos
        event.setEngineHours(50);

        event.setMobileTime(System.currentTimeMillis());
        event.setDriverId(id);
        event.setVehicleId(vehicle.getId());
        event.setBoxId(vehicle.getBoxId());

        mDisposables.add(mVehiclesInteractor.saveSelectedVehicle(vehicle)
                .andThen(mVehiclesInteractor.getTimezone(id))
                .flatMap(timeZone -> {
                    event.setTimezone(timeZone);

                    return mBlackBoxInteractor.getData();
                })
                .flatMap(blackBox -> {
                    event.setOdometer(blackBox.getOdometer());
                    event.setLat(blackBox.getLat());
                    event.setLng(blackBox.getLon());

                    return mVehiclesInteractor.pairVehicle(event);
                })
                .doOnNext(events -> mELDEventsInteractor.storeEvents(events, true))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> mView.goToMainScreen(),
                        Timber::e));
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
