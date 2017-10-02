package com.bsmwireless.screens.roadsidenavigation;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

public final class RoadsideNavigationPresenter extends BaseMenuPresenter {

    private BaseMenuView mView;

    @Inject
    public RoadsideNavigationPresenter(BaseMenuView view,
                                       UserInteractor userInteractor,
                                       ELDEventsInteractor eventsInteractor,
                                       DutyTypeManager dutyTypeManager,
                                       AccountManager accountManager) {
        super(dutyTypeManager, eventsInteractor, userInteractor, accountManager);
        mView = view;
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }
}
