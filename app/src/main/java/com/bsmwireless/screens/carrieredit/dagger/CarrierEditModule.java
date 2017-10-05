package com.bsmwireless.screens.carrieredit.dagger;

import android.content.Context;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.LogHeaderUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.carrieredit.CarrierEditPresenter;
import com.bsmwireless.screens.carrieredit.CarrierEditPresenterImpl;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsFragment;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsPresenter;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsPresenterImpl;
import com.bsmwireless.screens.carrieredit.fragments.edited.EditedEventsView;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsFragment;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsPresenter;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsPresenterImpl;
import com.bsmwireless.screens.carrieredit.fragments.unassigned.UnassignedEventsView;

import dagger.Module;
import dagger.Provides;

@ActivityScope
@Module
public final class CarrierEditModule {

    @Provides
    CarrierEditPresenter provideCarrierEditPresenter(DutyTypeManager dutyTypeManager,
                                                     ELDEventsInteractor eventsInteractor, UserInteractor userInteractor,
                                                     AccountManager accountManager, PreferencesManager preferencesManager, VehiclesInteractor vehiclesInteractor) {
        return new CarrierEditPresenterImpl(dutyTypeManager, eventsInteractor, userInteractor, accountManager, preferencesManager, vehiclesInteractor);
    }

    @Provides
    EditedEventsPresenter provideEditedEventsPresenter(ELDEventsInteractor eldEventsInteractor,
                                                       LogSheetInteractor logSheetInteractor,
                                                       UserInteractor userInteractor,
                                                       ServiceApi serviceApi,
                                                       VehiclesInteractor vehiclesInteractor,
                                                       LogHeaderUtils logHeaderUtils,
                                                       Context context) {
        return new EditedEventsPresenterImpl(eldEventsInteractor, logSheetInteractor, userInteractor, vehiclesInteractor, serviceApi, logHeaderUtils, context);
    }

    @Provides
    UnassignedEventsPresenter provideUnassignedEventsPresenter(ELDEventsInteractor interactor,
                                                               ServiceApi serviceApi) {
        return new UnassignedEventsPresenterImpl(interactor, serviceApi);
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
