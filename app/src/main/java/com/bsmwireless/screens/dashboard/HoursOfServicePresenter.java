package com.bsmwireless.screens.dashboard;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

@ActivityScope
public final class HoursOfServicePresenter extends BaseMenuPresenter {

    private final BaseMenuView mBaseMenuView;

    @Inject
    public HoursOfServicePresenter(DutyTypeManager dutyTypeManager,
                                   ELDEventsInteractor eventsInteractor,
                                   UserInteractor userInteractor,
                                   AccountManager mAccountManager,
                                   BaseMenuView baseMenuView) {
        super(dutyTypeManager, eventsInteractor, userInteractor, mAccountManager);
        mBaseMenuView = baseMenuView;
    }

    @Override
    protected BaseMenuView getView() {
        return mBaseMenuView;
    }
}
