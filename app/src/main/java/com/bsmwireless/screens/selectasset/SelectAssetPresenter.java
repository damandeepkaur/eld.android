package com.bsmwireless.screens.selectasset;

import android.util.Log;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionException;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.Vehicle;

import java.net.ConnectException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class SelectAssetPresenter {

    private SelectAssetView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private UserInteractor mUserInteractor;
    private ELDEventsInteractor mEventsInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SelectAssetPresenter(SelectAssetView view, VehiclesInteractor vehiclesInteractor, UserInteractor userInteractor, ELDEventsInteractor eventsInteractor) {
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mEventsInteractor = eventsInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        mUserInteractor.setShowSelectAssetScreenEnabled(false);

        mDisposables.add(mVehiclesInteractor.cleanSelectedVehicle()
                .andThen(mVehiclesInteractor.getLastVehicles())
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
        if (searchText.isEmpty()) {
            mView.setEmptyList();
        } else if (searchText.length() < 3) {
            mView.showSearchErrorMessage();
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
                            error -> {
                                Timber.e("SelectAsset error: %s", error);
                                if (error instanceof RetrofitException) {
                                    mView.showErrorMessage((RetrofitException) error);
                                }
                            }
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
                    .subscribe(events -> {
                                mView.goToHomeScreen();
                            },
                            error -> {
                                Timber.e("SelectAsset error: %s", error);
                                if (error instanceof RetrofitException) {
                                    mView.showErrorMessage((RetrofitException) error);
                                }

                                if (error instanceof BlackBoxConnectionException || error instanceof ConnectException) {
                                    mView.showErrorMessage(SelectAssetView.Error.ERROR_BLACKBOX);
                                }
                            }));
        }
    }

    public void onBackButtonPressed() {
        mUserInteractor.setShowSelectAssetScreenEnabled(true);

        mView.showConfirmationDialog();
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
