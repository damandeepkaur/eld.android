package com.bsmwireless.screens.carrieredit;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

public final class CarrierEditPresenterImpl  extends BaseMenuPresenter implements CarrierEditPresenter {

    private CarrierEditView mView;

    @Inject
    public CarrierEditPresenterImpl(DutyTypeManager dutyTypeManager,
                                    ELDEventsInteractor eventsInteractor, UserInteractor userInteractor,
                                    AccountManager accountManager) {
        super(dutyTypeManager, eventsInteractor, userInteractor, accountManager);
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    @Override
    public void bind(CarrierEditView view) {
        mView = view;
    }
}
