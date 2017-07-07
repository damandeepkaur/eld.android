package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.models.ELDDriverStatus;
import com.bsmwireless.models.ResponseMessage;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RecordsInteractor {

    private ServiceApi mServiceApi;

    @Inject
    public RecordsInteractor(ServiceApi serviceApi) {
        mServiceApi = serviceApi;
    }

    public Observable<ResponseMessage> postUnidentifyRecords(List<ELDDriverStatus> records) {
        return mServiceApi.updateUnidentifyRecords(records).subscribeOn(Schedulers.io());
    }

    public Observable<List<ELDDriverStatus>> syncUnidentifyRecords(long startTime, long endTime) {
        return mServiceApi.syncUnidentifyRecords(startTime, endTime).subscribeOn(Schedulers.io());
    }
}
