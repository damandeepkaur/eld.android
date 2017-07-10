package com.bsmwireless.screens.selectasset;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.InspectionsInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.Vehicle;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
public class SelectAssetPresenter {
    private SelectAssetView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private InspectionsInteractor mInspectionsInteractor;
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

    @Inject
    public SelectAssetPresenter(SelectAssetView view, VehiclesInteractor interactor) {
        mView = view;
        mVehiclesInteractor = interactor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onSearchTextChanged(SearchProperty searchProperty, String searchText, boolean isScan) {
        if (searchText.isEmpty()) {
            mView.showEmptyList();
        } else {
            mDisposables.add(mVehiclesInteractor.searchVehicles(searchProperty.getValue(), searchText, isScan)
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
        mDisposables.add(mVehiclesInteractor.saveSelectedVehicle(vehicle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mView.goToMainScreen(),
                        Timber::e));
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
