package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.ResponseMessage;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class DriverStatusInteractor {

    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;

    @Inject
    public DriverStatusInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
    }

    public Observable<ResponseMessage> syncDriverStatus(ELDDriverStatus status) {
        return mServiceApi.syncDriverStatus(status, mPreferencesManager.getSelectedBoxId()).subscribeOn(Schedulers.io());
    }

    public Observable<ResponseMessage> syncDriverStatuses(List<ELDDriverStatus> statusList) {
        return mServiceApi.syncDriverStatuses(statusList, mPreferencesManager.getSelectedBoxId()).subscribeOn(Schedulers.io());
    }
}
