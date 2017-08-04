package com.bsmwireless.screens.selectasset;

import android.support.annotation.Nullable;

import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.models.Vehicle;

import java.util.List;

public interface SelectAssetView {

    enum Error {
        ERROR_UNEXPECTED,
        ERROR_PERMISSION
    }

    void setVehicleList(@Nullable List<Vehicle> vehicles, @Nullable String searchText);

    void setLastVehicleList(@Nullable List<Vehicle> vehicles);

    void setEmptyList();

    void goToHomeScreen();

    void showErrorMessage(Error error);

    void showErrorMessage(RetrofitException error);

    void showSearchErrorMessage();

    void showEmptyListMessage();

    void showEmptyLastListMessage();
}
