package com.bsmwireless.screens.carrieredit;

import android.support.v4.app.FragmentManager;

import com.bsmwireless.screens.carrieredit.dagger.CarrierEditComponent;

/**
 * Created by osminin on 21.09.2017.
 */

public interface CarrierEditView {

    CarrierEditComponent getComponent();

    FragmentManager getSupportFragmentManager();

    String getString(int resId);
}
