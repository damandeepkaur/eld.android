package com.bsmwireless.screens.carrieredit;

public interface CarrierEditPresenter {

    void bind(CarrierEditView view);

    void requestDriverName();

    void requestVehicleId();
}
