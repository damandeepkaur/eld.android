package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalConverter;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.logsheets.LogSheetConverter;
import com.bsmwireless.data.storage.logsheets.LogSheetEntity;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LogSheetHeader;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
        return mServiceApi.getLogSheets(startLogDay, endLogDay)
                .doOnNext(logSheetHeaders -> mAppDatabase.logSheetDao().insert(LogSheetConverter.toEntityList(logSheetHeaders)))
                .toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<LogSheetHeader> getLogSheet(Long logDay) {
        return Single.fromCallable(() -> {
            LogSheetEntity entity = mAppDatabase.logSheetDao().getByLogDaySync(logDay);
            if (entity == null) {
                LogSheetHeader logSheetHeader = createLogSheetHeaderModel(logDay);
                mAppDatabase.logSheetDao().insert(LogSheetConverter.toEntity(logSheetHeader));
                syncLogSheetHeader(logSheetHeader);
                return logSheetHeader;
            } else {
                return LogSheetConverter.toModel(entity);
            }
        });
    }

    public Single<Boolean> updateLogSheetHeader(LogSheetHeader logSheetHeader) {
        return mServiceApi.updateLogSheetHeader(logSheetHeader)
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Single<LogSheetHeader> createLogSheetHeader(long logday) {
        return Single.fromCallable(() -> createLogSheetHeaderModel(logday))
                .flatMap(logSheetHeader -> updateLogSheetHeader(logSheetHeader)
                        .map(updated -> (updated) ? logSheetHeader : null
                ));
    }

    public void syncLogSheetHeader(LogSheetHeader logSheetHeader) {
        updateLogSheetHeader(logSheetHeader)
                .observeOn(Schedulers.io())
                .subscribe(isCreated -> {
                        },
                        throwable -> Timber.e(throwable));
    }


    private LogSheetHeader createLogSheetHeaderModel(long logday) {
        LogSheetHeader logSheetHeader = new LogSheetHeader();
        int driverId = mPreferencesManager.getDriverId();
        int boxId = mPreferencesManager.getBoxId();
        int vehicleId = mPreferencesManager.getVehicleId();

        UserEntity userEntity = mAppDatabase.userDao().getUserSync(driverId);
        if (userEntity != null && userEntity.getHomeTermId() != null) {
            HomeTerminalEntity entity = mAppDatabase.homeTerminalDao().getHomeTerminalSync(userEntity.getHomeTermId());
            HomeTerminal homeTerminal = HomeTerminalConverter.toHomeTerminal(entity);
            logSheetHeader.setHomeTerminal(homeTerminal);
        }

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
    }

}
