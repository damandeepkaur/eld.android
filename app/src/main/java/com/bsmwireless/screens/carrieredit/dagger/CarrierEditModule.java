package com.bsmwireless.screens.carrieredit.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.carrieredit.CarrierEditPresenter;
import com.bsmwireless.screens.carrieredit.CarrierEditView;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsFragment;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsPresenter;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsView;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsFragment;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsPresenter;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by osminin on 22.09.2017.
 */

@ActivityScope
@Module
public final class CarrierEditModule {

    @Provides
    CarrierEditPresenter provideCarrierEditPresenter() {
        return new CarrierEditPresenter();
    }

    @Provides
    EditedEventsPresenter provideEditedEventsPresenter() {
        return new EditedEventsPresenter();
    }

    @Provides
    UnassignedEventsPresenter provideUnassignedEventsPresenter() {
        return new UnassignedEventsPresenter();
    }

    @Provides
    EditedEventsView provideEditedEventsView() {
        return new EditedEventsFragment();
    }

    @Provides
    UnassignedEventsView provideUnassignedEventsView() {
        return new UnassignedEventsFragment();
    }
}
