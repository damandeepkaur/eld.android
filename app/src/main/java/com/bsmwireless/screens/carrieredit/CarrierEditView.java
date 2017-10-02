package com.bsmwireless.screens.carrieredit;

import android.support.v4.app.FragmentManager;

import com.bsmwireless.screens.carrieredit.dagger.CarrierEditComponent;
import com.bsmwireless.screens.common.menu.BaseMenuView;

public interface CarrierEditView extends BaseMenuView{

    CarrierEditComponent getComponent();

    FragmentManager getSupportFragmentManager();

    String getString(int resId);

    void setDriverName(String driverName);

    void setVehicleName(String vehicleName);
}
