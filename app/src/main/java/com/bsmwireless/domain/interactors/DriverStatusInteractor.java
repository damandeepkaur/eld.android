package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.DriverStatus;
import com.bsmwireless.models.Response;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class DriverStatusInteractor {

    @Inject
    ServiceApi mServiceApi;

    @Inject
    @Named(Constants.IO_THREAD)
    Scheduler mIoThread;

    @Inject
    AppDatabase mAppDatabase;

    @Inject
    HttpClientManager mClientManager;

    @Inject
    TokenManager mTokenManager;

    @Inject
    PreferencesManager mPreferencesManager;

    public DriverStatusInteractor() {
        App.getComponent().inject(this);
    }

    public Observable<Response> syncDriverStatus(List<DriverStatus> statusList) {
        return mServiceApi.syncDriverStatus(statusList).subscribeOn(mIoThread);
    }

    public Observable<Response> logoutDriver(DriverStatus status) {
        return mServiceApi.logoutDriver(status, mPreferencesManager.getSelectedBoxId()).subscribeOn(mIoThread);
    }

    public Observable<Response> certifyDriver(DriverStatus status) {
        return mServiceApi.certifyDriver(status, mPreferencesManager.getSelectedBoxId()).subscribeOn(mIoThread);
    }
}
