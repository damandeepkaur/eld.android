package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.DriverHomeTerminal;
import com.bsmwireless.models.DriverSignature;
import com.bsmwireless.data.storage.logsheets.LogSheetConverter;
import com.bsmwireless.data.storage.logsheets.LogSheetDao;
import com.bsmwireless.data.storage.logsheets.LogSheetEntity;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.RuleSelectionModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;
import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.data.storage.eldevents.ELDEventEntity.SyncType.SYNC;
import static com.bsmwireless.data.storage.logsheets.LogSheetEntity.SyncType.UNSYNC;

public final class SyncInteractor {
    private static final int MAX_EVENTS_IN_REQUEST = 5;
    private CompositeDisposable mSyncCompositeDisposable;
    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;
    private ELDEventDao mELDEventDao;
    private UserDao mUserDao;
    private volatile boolean mIsSyncActive;
    private AccountManager mAccountManager;
    private ResponseMessage mErrorResponse;

    private Disposable mDriverProfileDisposable;
    private Disposable mSyncEventsForDayDisposable;
    private LogSheetDao mLogSheetDao;
    private LogSheetInteractor mLogSheetInteractor;


    @Inject
    public SyncInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase,
                          AccountManager accountManager, LogSheetInteractor logSheetInteractor) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mELDEventDao = appDatabase.ELDEventDao();
        mUserDao = appDatabase.userDao();
        mLogSheetDao = appDatabase.logSheetDao();
        mErrorResponse = new ResponseMessage("error");
        mAccountManager = accountManager;
        mLogSheetInteractor = logSheetInteractor;
    }

    public void startSync() {
        if (mIsSyncActive) {
            return;
        }
        mIsSyncActive = true;
        if (mSyncCompositeDisposable == null || mSyncCompositeDisposable.isDisposed()) {
            mSyncCompositeDisposable = new CompositeDisposable();
        } else {
            mSyncCompositeDisposable.clear();
        }
        syncNewEvents();
        syncUpdatedEvents();
        syncLogSheetHeaders();
        syncDriverProfile();
    }

    private void syncDriverProfile() {
        if (mDriverProfileDisposable != null && !mDriverProfileDisposable.isDisposed()) {
            mDriverProfileDisposable.dispose();
        }

        mDriverProfileDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .filter(timeout -> NetworkUtils.isOnlineMode())
                .map(timeout -> mAccountManager.getCurrentUserId())
                .map(userId -> mUserDao.getUserSync(Integer.valueOf(userId)))
                .filter(UserEntity::isOfflineChange)
                .switchMapSingle(userEntity -> {
                    Single<Boolean> signatureUpdate = mServiceApi.updateDriverSignature(new DriverSignature()
                            .setDriverId(userEntity.getId())
                            .setSignature(userEntity.getSignature()))
                            .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                            .onErrorReturn(throwable -> false);
                    Single<Boolean> driverRuleUpdate = mServiceApi.updateDriverRule(new RuleSelectionModel()
                            .setDriverId(userEntity.getId())
                            .setRuleException(userEntity.getRuleException())
                            .setDutyCycle(userEntity.getDutyCycle())
                            .setApplyTime(Calendar.getInstance().getTimeInMillis()))
                            .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                            .onErrorReturn(throwable -> false);
                    Single<Boolean> homeTerminalUpdate = mServiceApi.updateDriverHomeTerminal(new DriverHomeTerminal()
                            .setDriverId(userEntity.getId())
                            .setHomeTermId(userEntity.getHomeTermId()))
                            .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                            .onErrorReturn(throwable -> false);
                    return Single.zip(signatureUpdate, driverRuleUpdate, (resultFirst, resultSecond) -> resultFirst && resultSecond)
                            .zipWith(homeTerminalUpdate, (resultFirst, resultSecond) -> resultFirst && resultSecond)
                            .map(updateSuccess -> userEntity.setOfflineChange(!updateSuccess))
                            .doOnSuccess(userEntity1 -> mUserDao.insertUser(userEntity1));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void syncEventsForDay(Calendar dayCalendar, String timezone) {
        long startTime = DateUtils.getStartDate(timezone, dayCalendar);
        long endTime = startTime + MS_IN_DAY;

        if (mSyncEventsForDayDisposable != null && !mSyncEventsForDayDisposable.isDisposed()) {
            mSyncEventsForDayDisposable.dispose();
        }
        mSyncEventsForDayDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .take(3)
                .map(aLong -> ELDEventConverter.toModelList(mELDEventDao.getUpdateUnsyncEvents()))
                .map(this::filterInactiveEvents)
                .filter(List::isEmpty)
                .flatMapSingle(eldEvents -> mServiceApi.getELDEvents(startTime, endTime))
                .subscribe(eldEventsFromServer -> {
                            List<ELDEventEntity> entities = mELDEventDao.getEventsFromStartToEndTimeSync(
                                    startTime, endTime, mAccountManager.getCurrentUserId());
                            List<ELDEvent> eventsFromDB = ELDEventConverter.toModelList(entities);
                            if (!eldEventsFromServer.equals(eventsFromDB)) {
                                ELDEventEntity[] entitiesArray = ELDEventConverter.toEntityArray(eldEventsFromServer);
                                mELDEventDao.insertAll(entitiesArray);
                            }
                        },
                        Timber::e);
    }

    public void syncEventsForDaysAgo(int days, String timezone) {
        long current = System.currentTimeMillis();
        long start = DateUtils.getStartDayTimeInMs(timezone, current - days * MS_IN_DAY);
        long end = DateUtils.getEndDayTimeInMs(timezone, current);
        if (NetworkUtils.isOnlineMode()) {
            mServiceApi.getELDEvents(start, end)
                    .subscribeOn(Schedulers.io())
                    .map(eldEvents -> ELDEventConverter.toEntityArray(eldEvents, SYNC))
                    .subscribe(eldEventEntities -> mELDEventDao.insertAll(eldEventEntities),
                            Timber::e);
        }
    }

    private void syncNewEvents() {
        Disposable syncNewEventsDisposable = Single.timer(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .filter(t -> mPreferencesManager.getBoxId() != PreferencesManager.NOT_FOUND_VALUE)
                .map(t -> mAccountManager.getCurrentUserId())
                .map(userId -> ELDEventConverter.toModelList(mELDEventDao.getNewUnsyncEvents(userId)))
                .filter(eldEvents -> !eldEvents.isEmpty())
                .map(this::filterIncorrectEvents)
                .filter(eldEvents -> !eldEvents.isEmpty())
                .flatMapObservable(events -> Observable.fromIterable(parseELDEventsList(events)))
                .flatMapSingle(events -> mServiceApi.postNewELDEvents(events)
                        .onErrorResumeNext(Single.just(mErrorResponse))
                        .onErrorResumeNext(Single.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? events : new ArrayList<ELDEvent>())
                )
                .filter(events -> !events.isEmpty())
                .delay(1, TimeUnit.SECONDS)
                .flatMapSingle(dbEvents -> mServiceApi.getELDEvents(dbEvents.get(0).getEventTime(), dbEvents.get(dbEvents.size() - 1).getEventTime())
                        .doOnSuccess(serverEvents -> {
                            ELDEventEntity[] oldEntities = ELDEventConverter.toEntityArray(dbEvents);
                            mELDEventDao.deleteAll(oldEntities);
                            ELDEventEntity[] entities = ELDEventConverter.toEntityArray(serverEvents, SYNC);
                            mELDEventDao.insertAll(entities);
                        }))
                .subscribe(events -> Timber.d("Sync added events:" + events),
                        Timber::e);
        mSyncCompositeDisposable.add(syncNewEventsDisposable);
    }

    private void syncUpdatedEvents() {
        Disposable syncUpdatedEventDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .filter(t -> mPreferencesManager.getBoxId() != PreferencesManager.NOT_FOUND_VALUE)
                .map(t -> ELDEventConverter.toModelList(mELDEventDao.getUpdateUnsyncEvents()))
                .filter(dbEvents -> !dbEvents.isEmpty())
                .map(this::filterIncorrectEvents)
                .filter(dbEvents -> !dbEvents.isEmpty())
                .flatMap(dbEvents -> Observable.fromIterable(parseELDEventsList(dbEvents)))
                .map(this::filterInactiveEvents)
                .filter(activeDbEvents -> activeDbEvents.size() > 0)
                .flatMapSingle(dbEvents -> mServiceApi.updateELDEvents(dbEvents)
                        .onErrorResumeNext(Single.just(mErrorResponse))
                        .onErrorResumeNext(Single.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? dbEvents : new ArrayList<ELDEvent>())
                )
                .filter(events -> !events.isEmpty())
                .flatMapSingle(dbEvents -> mServiceApi.getELDEvents(dbEvents.get(0).getEventTime(), dbEvents.get(dbEvents.size() - 1).getEventTime())
                        .doOnSuccess(serverEvents -> {
                            ELDEventEntity[] oldEntities = ELDEventConverter.toEntityArray(dbEvents);
                            mELDEventDao.deleteAll(oldEntities);
                            ELDEventEntity[] entities = ELDEventConverter.toEntityArray(serverEvents);
                            mELDEventDao.insertAll(entities);
                        })
                        .map(serverEvents -> dbEvents))
                .subscribe(dbEvents -> Timber.d("Sync updated events:" + dbEvents),
                        Timber::e);
        mSyncCompositeDisposable.add(syncUpdatedEventDisposable);
    }

    public void syncLogSheetHeaders() {
        Disposable syncHeadersDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .filter(t -> mPreferencesManager.getBoxId() != PreferencesManager.NOT_FOUND_VALUE)
                .map(t -> mAccountManager.getCurrentUserId())
                .map(userId -> mLogSheetDao.getUnSync(userId))
                .filter(logSheetEntities -> !logSheetEntities.isEmpty())
                .map(LogSheetConverter::toModelList)
                .flatMap(Observable::fromIterable)
                .flatMap(logSheetHeader -> mServiceApi.updateLogSheetHeader(logSheetHeader)
                        .onErrorResumeNext(throwable -> Single.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                        .map(isSuccess -> (isSuccess) ? LogSheetEntity.SyncType.SYNC : LogSheetEntity.SyncType.UNSYNC)
                        .flatMapObservable(syncType -> Observable.just(LogSheetConverter.toEntity(logSheetHeader, syncType)))
                )
                .filter(logSheetEntity -> logSheetEntity.getSync() == LogSheetEntity.SyncType.SYNC.ordinal())
                .doOnNext(logSheetEntity -> mLogSheetDao.insert(logSheetEntity))
                .subscribe(logSheetEntity -> Timber.d("Sync LogSheetHeader: " + LogSheetConverter.toModel(logSheetEntity)),
                        Timber::e);
        mSyncCompositeDisposable.add(syncHeadersDisposable);
    }

    public void syncLogSheetHeadersForDaysAgo(int days, String timezone) {
        mServiceApi.getLogSheets(DateUtils.getLogDayForDaysAgo(days, timezone), DateUtils.getLogDayForDaysAgo(0, timezone))
                .map(LogSheetConverter::toEntityList)
                .doOnSuccess(logSheetEntities -> mLogSheetDao.insert(logSheetEntities))
                .doOnSuccess(logSheetHeaders -> createMissingLogSheets(logSheetHeaders, days, timezone))
                .subscribe();
    }

    public void stopSync() {
        if (!mIsSyncActive) {
            return;
        }
        if (mSyncCompositeDisposable == null || mSyncCompositeDisposable.isDisposed()) {
            return;
        }
        mSyncCompositeDisposable.clear();
        mSyncCompositeDisposable.dispose();
        mIsSyncActive = false;
    }

    //TODO: remove after cleanup all incorrect events on server part
    private List<ELDEvent> filterIncorrectEvents(List<ELDEvent> list) {
        ListIterator<ELDEvent> iterator = list.listIterator();
        while (iterator.hasNext()) {
            ELDEvent event = iterator.next();
            if (event.getBoxId() <= 0 || event.getVehicleId() <= 0) {
                iterator.remove();
            }
        }
        return list;
    }

    private List<ELDEvent> filterInactiveEvents(List<ELDEvent> list) {
        ListIterator<ELDEvent> iterator = list.listIterator();
        while (iterator.hasNext()) {
            ELDEvent event = iterator.next();
            if (event.getStatus() == ELDEvent.StatusCode.INACTIVE_CHANGED.getValue()) {
                iterator.remove();
            }
        }
        return list;
    }

    private List<List<ELDEvent>> parseELDEventsList(List<ELDEvent> events) {
        List<List<ELDEvent>> list = new ArrayList<>();
        if (events.size() < MAX_EVENTS_IN_REQUEST) {
            list.add(events);
        } else {
            for (int i = 0; i < events.size(); i += MAX_EVENTS_IN_REQUEST) {
                list.add(events.subList(i, Math.min(i + MAX_EVENTS_IN_REQUEST, events.size() - 1)));
            }
        }
        return list;
    }

    private void createMissingLogSheets(List<LogSheetEntity> logSheetHeaders, int days, String timezone) {
        if (logSheetHeaders.size() < days) {
            int createdCount = 0;
            int userId = mAccountManager.getCurrentUserId();
            for (int i = 0; i < days; i++) {
                long logDay = DateUtils.getLogDayForDaysAgo(i, timezone);
                LogSheetEntity entity = mLogSheetDao.getByLogDaySync(logDay, userId);
                if (entity == null) {
                    LogSheetEntity prevEntity = mLogSheetDao.getLatestLogSheet(logDay, userId);
                    LogSheetEntity newEntity;
                    if (prevEntity == null) {
                        LogSheetHeader logSheetHeader = mLogSheetInteractor.createLogSheetHeaderModel(logDay);
                        newEntity = LogSheetConverter.toEntity(logSheetHeader, UNSYNC);
                    } else {
                        newEntity = prevEntity;
                        newEntity.setSigned(false);
                        newEntity.setSync(UNSYNC.ordinal());
                        newEntity.setLogDay(logDay);
                    }
                    mLogSheetDao.insert(newEntity);
                    createdCount++;
                    if (createdCount == (days - logSheetHeaders.size())) {
                        break;
                    }
                }
            }
        }
    }

}
