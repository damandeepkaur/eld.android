package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalConverter;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.logsheets.LogSheetConverter;
import com.bsmwireless.data.storage.logsheets.LogSheetDao;
import com.bsmwireless.data.storage.logsheets.LogSheetEntity;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LogSheetHeader;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.bsmwireless.data.storage.logsheets.LogSheetEntity.SyncType.UNSYNC;

public class LogSheetInteractor {

    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;
    private AppDatabase mAppDatabase;
    private LogSheetDao mLogSheetDao;
    private AccountManager mAccountManager;

    @Inject
    public LogSheetInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager,
                              AppDatabase appDatabase, AccountManager accountManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mLogSheetDao = appDatabase.logSheetDao();
        mAccountManager = accountManager;
    }

    public Flowable<List<LogSheetHeader>> getLogSheetHeaders(Long startLogDay, Long endLogDay) {
        return mLogSheetDao.getLogSheets(startLogDay, endLogDay, mAccountManager.getCurrentUserId())
                .map(logSheetEntities -> LogSheetConverter.toModelList(logSheetEntities));
    }

    public Single<LogSheetHeader> getLogSheet(Long logDay) {
        return Single.fromCallable(() -> {
            LogSheetEntity entity = mLogSheetDao.getByLogDaySync(logDay, mAccountManager.getCurrentUserId());
            if (entity == null) {
                LogSheetHeader logSheetHeader = createLogSheetHeaderModel(logDay);
                mLogSheetDao.insert(LogSheetConverter.toEntity(logSheetHeader, UNSYNC));
                return logSheetHeader;
            } else {
                return LogSheetConverter.toModel(entity);
            }
        });
    }

    public Observable<Long> updateLogSheetHeader(LogSheetHeader logSheetHeader) {
        LogSheetEntity entity = LogSheetConverter.toEntity(logSheetHeader, LogSheetEntity.SyncType.UNSYNC);
        return Observable.fromCallable(() -> mLogSheetDao.insert(entity));
    }

    public LogSheetHeader createLogSheetHeaderModel(long logday) {
        LogSheetHeader logSheetHeader = new LogSheetHeader();
        int driverId = mAccountManager.getCurrentDriverId();
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
