package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.ResponseMessage;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class LogSheetInteractor {

    private ServiceApi mServiceApi;

    @Inject
    public LogSheetInteractor(ServiceApi serviceApi) {
        mServiceApi = serviceApi;
    }

    public Observable<List<LogSheetHeader>> syncLogSheetHeader(Long startLogDay, Long endLogDay) {
        return mServiceApi.getLogSheets(startLogDay, endLogDay);
    }

    public Observable<ResponseMessage> updateLogSheetHeader(LogSheetHeader logSheetHeader) {
        return mServiceApi.updateLogSheetHeader(logSheetHeader);
    }
}
