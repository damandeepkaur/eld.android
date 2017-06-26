package com.bsmwireless.screens.selectasset;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.Vehicle;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class SelectAssetPresenter {

    @Inject
    @Named(Constants.UI_THREAD)
    Scheduler mUiThread;

    private SelectAssetView mView;

    private VehiclesInteractor mVehiclesInteractor;

    private CompositeDisposable mDisposables;

    public enum SearchProperty {
        SAP(0),
        LEGACY(1),
        SERIAL(2),
        DESCRIPTION(3),
        LICENSE_PLATE(4),
        BOX_ID(5);

        private final int mValue;

        SearchProperty(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    public SelectAssetPresenter(SelectAssetView view, VehiclesInteractor interactor) {
        App.getComponent().inject(this);
        mView = view;
        mVehiclesInteractor = interactor;
        mDisposables = new CompositeDisposable();
    }

    public void onSearchTextChanged(SearchProperty searchProperty, String searchText, boolean isScan) {
        if (searchText.isEmpty()) {
            mView.showEmptyList();
        } else {
            mDisposables.add(mVehiclesInteractor.searchVehicles(searchProperty.getValue(), searchText, isScan).
                    observeOn(mUiThread)
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
                .observeOn(mUiThread)
                .subscribe(() -> mView.goToMainScreen(),
                        Timber::e));
    }

    public void onVehicleListItemClicked(Vehicle vehicle) {
        mDisposables.add(mVehiclesInteractor.saveSelectedVehicle(vehicle)
                .observeOn(mUiThread)
                .subscribe(() -> mView.goToMainScreen(),
                        Timber::e));
    }

    public void onDestroy() {
        mDisposables.dispose();
    }
}
