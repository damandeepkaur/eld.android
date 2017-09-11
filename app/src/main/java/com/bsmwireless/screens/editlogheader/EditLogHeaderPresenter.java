package com.bsmwireless.screens.editlogheader;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.screens.logs.LogHeaderModel;

import javax.inject.Inject;

import timber.log.Timber;

@ActivityScope
public class EditLogHeaderPresenter extends BaseMenuPresenter {

    private EditLogHeaderView mView;
    private LogHeaderModel mLogHeaderModel;

    @Inject
    public EditLogHeaderPresenter(EditLogHeaderView view,
                                  UserInteractor userInteractor,
                                  DutyTypeManager dutyTypeManager,
                                  ELDEventsInteractor eldEventsInteractor) {
        super(dutyTypeManager, eldEventsInteractor, userInteractor);
        mView = view;

        Timber.d("CREATED");
    }

    public void onViewCreated(LogHeaderModel logHeaderModel) {
        mLogHeaderModel = logHeaderModel;
        mView.setLogHeaderModel(logHeaderModel);
    }

    public void onSaveLogHeaderButtonClicked() {
        LogHeaderModel logHeaderModel = mView.getLogHeader(mLogHeaderModel);
        mView.saveLogHeader(logHeaderModel);
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }
}
