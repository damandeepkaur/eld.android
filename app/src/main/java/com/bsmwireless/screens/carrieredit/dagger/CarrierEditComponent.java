package com.bsmwireless.screens.carrieredit.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.carrieredit.CarrierEditActivity;
import com.bsmwireless.screens.carrieredit.CarrierEditAdapter;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsFragment;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsFragment;

import dagger.Component;

/**
 * Created by osminin on 22.09.2017.
 */

@ActivityScope
@Component(dependencies = AppComponent.class, modules = CarrierEditModule.class)
public interface CarrierEditComponent {

    void inject(CarrierEditActivity activity);

    void inject(UnassignedEventsFragment fragment);

    void inject(EditedEventsFragment fragment);

    void inject(CarrierEditAdapter carrierEditAdapter);
}
