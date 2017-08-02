package com.bsmwireless.screens.selectasset;

import android.content.Context;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
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
    private CompositeDisposable mDisposables;
    private Context mContext;

    @Inject
    public SelectAssetPresenter(Context context, SelectAssetView view, VehiclesInteractor vehiclesInteractor, LoginUserInteractor userInteractor) {
        mContext = context;
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
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
        if (searchText.isEmpty()){
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
                                mView.showErrorMessage(NetworkUtils.getErrorMessage((RetrofitException) error, mContext));
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
                                mView.showErrorMessage(NetworkUtils.getErrorMessage((RetrofitException) error, mContext));
                            }));
        }
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
