package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalConverter;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LogSheetHeader;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

import static com.bsmwireless.common.Constants.SUCCESS;

public class LogSheetInteractor {

    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;
    private AppDatabase mAppDatabase;

    @Inject
    public LogSheetInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
    }

    public Flowable<List<LogSheetHeader>> getLogSheetHeaders(Long startLogDay, Long endLogDay) {
        return mServiceApi.getLogSheets(startLogDay, endLogDay).toFlowable(BackpressureStrategy.LATEST);
    }

    public Observable<Boolean> updateLogSheetHeader(LogSheetHeader logSheetHeader) {
        return mServiceApi.updateLogSheetHeader(logSheetHeader)
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Observable<LogSheetHeader> createLogSheetHeader(long logday) {
        LogSheetHeader logSheetHeader = new LogSheetHeader();
        return Observable.fromCallable(() -> {
            int driverId = mPreferencesManager.getDriverId();
            int boxId = mPreferencesManager.getBoxId();
            int vehicleId = mPreferencesManager.getVehicleId();

            int homeTermId = mAppDatabase.userDao().getUserSync(driverId).getHomeTermId();
            HomeTerminalEntity entity = mAppDatabase.homeTerminalDao().getHomeTerminalSync(homeTermId);
            HomeTerminal homeTerminal = HomeTerminalConverter.toHomeTerminal(entity);

            logSheetHeader.setHomeTerminal(homeTerminal);
            logSheetHeader.setBoxId(boxId);
            logSheetHeader.setDriverId(driverId);
            logSheetHeader.setVehicleId(vehicleId);

            logSheetHeader.setLogDay(logday);
            logSheetHeader.setSigned(false);

            //TODO: fill appropriate fields to real data
            logSheetHeader.setDutyCycle("");
            logSheetHeader.setCoDriverIds("");
            logSheetHeader.setTrailerIds("");
            logSheetHeader.setStartOfDay(0L);
            return logSheetHeader;
        })
                .flatMap(this::updateLogSheetHeader)
                .map(updated -> {
                    if (updated) {
                        return logSheetHeader;
                    }
                    return null;
                });
    }

}
