package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.ResponseMessage;

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
    PreferencesManager mPreferencesManager;

    public DriverStatusInteractor() {
        App.getComponent().inject(this);
    }

    public Observable<ResponseMessage> syncDriverStatus(ELDDriverStatus status) {
        return mServiceApi.syncDriverStatus(status, mPreferencesManager.getSelectedBoxId()).subscribeOn(mIoThread);
    }

    public Observable<ResponseMessage> syncDriverStatuses(List<ELDDriverStatus> statusList) {
        return mServiceApi.syncDriverStatuses(statusList, mPreferencesManager.getSelectedBoxId()).subscribeOn(mIoThread);
    }
}
