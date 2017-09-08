package com.bsmwireless.screens.settings;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.SettingsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class SettingsPresenter extends BaseMenuPresenter implements AccountManager.AccountListener {

    private SettingsView mView;
    private SettingsInteractor mSettingsInteractor;

    @Inject
    public SettingsPresenter(SettingsView view, SettingsInteractor settingsInteractor,
                             DutyTypeManager dutyTypeManager, UserInteractor userInteractor,
                             AccountManager accountManager) {
        mView = view;
        mSettingsInteractor = settingsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mUserInteractor = userInteractor;
        mAccountManager = accountManager;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onViewCreated() {
        mView.setBoxGPSSwitchEnabled(mSettingsInteractor.isBoxGPSEnabled());
        mView.setFixedAmountSwitchEnabled(mSettingsInteractor.isFixedAmountEnabled());

        // set current selected value for odometer units
        mView.checkOdometerUnit(loadLastSelectedOdometerUnit());

        mAccountManager.addListener(this);
        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(() -> mUserInteractor.getFullUserNameSync())
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(name -> mView.showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            mView.hideCoDriverView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccountManager.removeListener(this);
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

    public void onUnitsSelected(boolean isKMOdometerUnitsSelected) {
        if (isKMOdometerUnitsSelected) {
            mView.checkOdometerUnit(SettingsView.OdometerUnits.ODOMETER_UNITS_KM);
        } else {
            mView.checkOdometerUnit(SettingsView.OdometerUnits.ODOMETER_UNITS_MI);
        }

        mSettingsInteractor.saveKMOdometerUnitsSelected(isKMOdometerUnitsSelected);
    }

    private SettingsView.OdometerUnits loadLastSelectedOdometerUnit() {
        if (mSettingsInteractor.isKMOdometerUnitsSelected()) {
            return SettingsView.OdometerUnits.ODOMETER_UNITS_KM;
        }
        return SettingsView.OdometerUnits.ODOMETER_UNITS_MI;
    }

    @Override
    public void onUserChanged() {
        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(() -> mUserInteractor.getFullUserNameSync())
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(name -> mView.showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            mView.hideCoDriverView();
        }
    }

    @Override
    public void onDriverChanged() {

    }
}
