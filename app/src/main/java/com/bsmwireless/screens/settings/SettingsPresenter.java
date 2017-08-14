package com.bsmwireless.screens.settings;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.domain.interactors.SettingsInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
public class SettingsPresenter extends BaseMenuPresenter {

    private SettingsView mView;
    private SettingsInteractor mSettingsInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SettingsPresenter(SettingsView view, SettingsInteractor settingsInteractor, DutyManager dutyManager) {
        mView = view;
        mSettingsInteractor = settingsInteractor;
        mDutyManager = dutyManager;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        mView.setBoxGPSSwitchEnabled(mSettingsInteractor.isBoxGPSEnabled());
        mView.setFixedAmountSwitchEnabled(mSettingsInteractor.isFixedAmountEnabled());
    }

    public void onBoxGPSSwitchChecked(boolean isBoxGPSEnabled) {
        mSettingsInteractor.saveBoxGPSEnabled(isBoxGPSEnabled);
    }

    public void onFixedAmountSwitchChecked(boolean isFixedAmountEnabled) {
        mSettingsInteractor.saveFixedAmountEnabled(isFixedAmountEnabled);
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    public void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
