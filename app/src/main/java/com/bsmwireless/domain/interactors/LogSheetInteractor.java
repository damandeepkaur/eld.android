package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalConverter;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.logsheets.LogSheetConverter;
import com.bsmwireless.data.storage.logsheets.LogSheetDao;
import com.bsmwireless.data.storage.logsheets.LogSheetEntity;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LogSheetHeader;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;
import static com.bsmwireless.data.storage.logsheets.LogSheetEntity.SyncType.UNSYNC;

public final class LogSheetInteractor {
    private final static int MAX_CERTIFICATION_CODE = 9;
    private PreferencesManager mPreferencesManager;
    private AppDatabase mAppDatabase;
    private LogSheetDao mLogSheetDao;
    private AccountManager mAccountManager;

    @Inject
    public LogSheetInteractor(PreferencesManager preferencesManager,
                              AppDatabase appDatabase, AccountManager accountManager) {
        mPreferencesManager = preferencesManager;
        mAppDatabase = appDatabase;
        mLogSheetDao = appDatabase.logSheetDao();
        mAccountManager = accountManager;
    }

    public Flowable<List<LogSheetHeader>> getLogSheetHeaders(Long startLogDay, Long endLogDay) {
        return mLogSheetDao.getLogSheets(startLogDay, endLogDay, mAccountManager.getCurrentUserId())
                .map(LogSheetConverter::toModelList);
    }

    public Flowable<LogSheetHeader> getLogSheetHeadersFromDB(long logDay) {
        int driverId = mAccountManager.getCurrentUserId();
        return mAppDatabase.logSheetDao().getLogSheet(logDay, driverId)
                .map(LogSheetConverter::toModel);
    }

    public Single<LogSheetHeader> getLogSheet(Long logDay) {
        return Single.fromCallable(() -> getLogSheetEntity(logDay))
                .map(LogSheetConverter::toModel);
    }

    public Single<Long> updateLogSheetHeader(LogSheetHeader logSheetHeader) {
        LogSheetEntity entity = LogSheetConverter.toEntity(logSheetHeader, LogSheetEntity.SyncType.UNSYNC);
        return Single.fromCallable(() -> mLogSheetDao.insert(entity));
    }

    public Observable<LogSheetHeader> signLogSheet(long logDay) {
        return Observable.fromCallable(() -> getLogSheetEntity(logDay))
                .filter(entity -> Boolean.FALSE.equals(entity.getSigned()))
                .doOnNext(logSheetEntity -> {
                    logSheetEntity.setSigned(true);
                    logSheetEntity.setSync(UNSYNC.ordinal());
                    mLogSheetDao.insert(logSheetEntity);
                })
                .map(LogSheetConverter::toModel)
                .doOnNext(this::addCertificationEvent);
    }

    private void addCertificationEvent(LogSheetHeader logSheetHeader) {
        Single.fromCallable(() -> mAppDatabase.ELDEventDao().getCertificationEventsSync(
                logSheetHeader.getLogDay(), mAccountManager.getCurrentUserId()))
                .map(certificationEvents -> {
                    int code = 1;
                    if (!certificationEvents.isEmpty()) {
                        code = Math.min((certificationEvents.get(0).getEventCode() + 1), MAX_CERTIFICATION_CODE);
                    }
                    return code;
                })
                .map(code -> createCertificationEvent(logSheetHeader, code))
                .map(event -> ELDEventConverter.toEntity(event, ELDEventEntity.SyncType.NEW_UNSYNC))
                .doOnSuccess(eventEntity -> mAppDatabase.ELDEventDao().insertEvent(eventEntity))
                .subscribe(event -> Timber.i("Certification event added: " + event),
                        Timber::e);
    }

    private ELDEvent createCertificationEvent(LogSheetHeader logSheetHeader, int code) {
        long certDay = DateUtils.convertLogDayToUnixMs(logSheetHeader.getLogDay());
        ELDEvent event = new ELDEvent();
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setOrigin(ELDEvent.EventOrigin.DRIVER.getValue());
        event.setEventType(ELDEvent.EventType.CERTIFICATION_OF_RECORDS.getValue());
        event.setEventCode(code);
        event.setLogSheet(logSheetHeader.getLogDay());
        event.setDriverId(logSheetHeader.getDriverId());
        event.setVehicleId(logSheetHeader.getVehicleId());
        event.setEventTime(certDay);
        event.setMobileTime(Calendar.getInstance().getTimeInMillis());
        String timezone = logSheetHeader.getHomeTerminal().getTimezone();
        event.setTimezone(timezone);
        event.setBoxId(logSheetHeader.getBoxId());
        event.setMobileTime(Calendar.getInstance().getTimeInMillis());
        return event;
    }

    public void resetLogSheetHeaderSigning(List<ELDEvent> events) {
        if (events != null && !events.isEmpty()) {
            Observable.fromIterable(events)
                    .doOnNext(this::resetLogSheetHeaderSigning)
                    .subscribe();
        }
    }

    private void resetLogSheetHeaderSigning(ELDEvent event) {
        long logDay = DateUtils.convertTimeToLogDay(event.getTimezone(), event.getEventTime());
        LogSheetEntity entity = getLogSheetEntity(logDay);
        if (Boolean.TRUE.equals(entity.getSigned())) {
            entity.setSigned(false);
            entity.setSync(UNSYNC.ordinal());
        }
        mLogSheetDao.insert(entity);
    }

    public LogSheetEntity getLogSheetEntity(long logDay) {
        LogSheetEntity entity = mLogSheetDao.getByLogDaySync(logDay, mAccountManager.getCurrentUserId());
        if (entity == null) {
            entity = LogSheetConverter.toEntity(createLogSheetHeaderModel(logDay), UNSYNC);
            mLogSheetDao.insert(entity);
        }
        return entity;
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
