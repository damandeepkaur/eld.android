package com.bsmwireless.screens.selectasset;

import com.bsmwireless.models.Vehicle;

import java.util.List;

public interface SelectAssetView {

    void setVehicleList(List<Vehicle> vehicles);

    void showEmptyList();

    void goToMainScreen();

}
