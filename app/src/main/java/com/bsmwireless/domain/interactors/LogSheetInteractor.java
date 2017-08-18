package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.models.LogSheetHeader;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.bsmwireless.common.Constants.SUCCESS;

public class LogSheetInteractor {

    private ServiceApi mServiceApi;

    @Inject
    public LogSheetInteractor(ServiceApi serviceApi) {
        mServiceApi = serviceApi;
    }

    public Observable<List<LogSheetHeader>> syncLogSheetHeader(Long startLogDay, Long endLogDay) {
        return mServiceApi.getLogSheets(startLogDay, endLogDay);
    }

    public Observable<Boolean> updateLogSheetHeader(LogSheetHeader logSheetHeader) {
        return mServiceApi.updateLogSheetHeader(logSheetHeader).map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }
}
