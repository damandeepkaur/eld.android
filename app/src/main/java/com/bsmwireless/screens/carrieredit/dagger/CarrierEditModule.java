package com.bsmwireless.screens.carrieredit.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.carrieredit.CarrierEditPresenter;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsFragment;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsPresenter;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsView;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsFragment;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsPresenter;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsView;

import dagger.Module;
import dagger.Provides;

@ActivityScope
@Module
public final class CarrierEditModule {

    @Provides
    CarrierEditPresenter provideCarrierEditPresenter() {
        return new CarrierEditPresenter();
    }

    @Provides
    EditedEventsPresenter provideEditedEventsPresenter(ELDEventsInteractor eldEventsInteractor,
                                                       LogSheetInteractor logSheetInteractor,
                                                       UserInteractor userInteractor,
                                                       ServiceApi serviceApi,
                                                       VehiclesInteractor vehiclesInteractor) {
        return new EditedEventsPresenter(eldEventsInteractor, logSheetInteractor, userInteractor, vehiclesInteractor, serviceApi);
    }

    @Provides
    UnassignedEventsPresenter provideUnassignedEventsPresenter(ELDEventsInteractor interactor,
                                                               ServiceApi serviceApi) {
        return new UnassignedEventsPresenter(interactor, serviceApi);
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
