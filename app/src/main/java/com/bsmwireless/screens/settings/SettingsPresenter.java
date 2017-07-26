package com.bsmwireless.screens.settings;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.SettingsInteractor;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
