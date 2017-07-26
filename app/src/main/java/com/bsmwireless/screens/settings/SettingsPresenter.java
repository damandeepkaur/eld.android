package com.bsmwireless.screens.settings;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.SettingsInteractor;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class SettingsPresenter {

    private SettingsView mView;
    private SettingsInteractor mSettingsInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public SettingsPresenter(SettingsView view, SettingsInteractor settingsInteractor) {
        mView = view;
        mSettingsInteractor = settingsInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onBoxGPSSwitchChecked(boolean isBoxGPSEnabled) {
        mDisposables.add(mSettingsInteractor.saveBoxGPSEnabled(isBoxGPSEnabled)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void onFixedAmountSwitchChecked(boolean isFixedAmountEnabled) {
        mDisposables.add(mSettingsInteractor.saveFixedAmountEnabled(isFixedAmountEnabled)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void setBoxGPSSwitch() {
        mView.setBoxGPSSwitchEnabled(mSettingsInteractor.isBoxGPSEnabled());
    }

    public void setFixedAmountSwitch() {
        mView.setFixedAmountSwitchEnabled(mSettingsInteractor.isFixedAmountEnabled());
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
