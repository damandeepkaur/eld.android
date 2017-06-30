package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.HttpClientManager;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.ResponseMessage;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class RecordsInteractor {

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

    public RecordsInteractor() {
        App.getComponent().inject(this);
    }

    public Observable<List<ELDDriverStatus>> syncUnidentifyRecords() {
        return mServiceApi.syncUnidentifyRecords().subscribeOn(mIoThread);
    }

    public Observable<ResponseMessage> postUnidentifyRecords(List<ELDDriverStatus> records) {
        return mServiceApi.postUnidentifyRecords(records).subscribeOn(mIoThread);
    }

    public Observable<List<ELDDriverStatus>> syncUnidentifyRecords(long startTime, long endTime) {
        return mServiceApi.syncUnidentifyRecords(startTime, endTime).subscribeOn(mIoThread);
    }
}
