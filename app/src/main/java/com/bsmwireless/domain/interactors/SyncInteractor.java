package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.logsheets.LogSheetConverter;
import com.bsmwireless.data.storage.logsheets.LogSheetDao;
import com.bsmwireless.data.storage.logsheets.LogSheetEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.models.DriverHomeTerminal;
import com.bsmwireless.models.DriverSignature;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.models.RuleSelectionModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;
import static com.bsmwireless.common.Constants.SYNC_DELAY;
import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.data.storage.eldevents.ELDEventEntity.SyncType.SEND;
import static com.bsmwireless.data.storage.logsheets.LogSheetEntity.SyncType.UNSYNC;

public final class SyncInteractor {
    private static final int MAX_EVENTS_IN_REQUEST = 5;
    private CompositeDisposable mSyncCompositeDisposable;
    private ServiceApi mServiceApi;
    private ELDEventDao mELDEventDao;
    private UserDao mUserDao;
    private volatile boolean mIsSyncActive;
    private AccountManager mAccountManager;
    private ResponseMessage mErrorResponse;

    private Disposable mDriverProfileDisposable;
    private Disposable mSyncEventsDisposable;
    private LogSheetDao mLogSheetDao;
    private LogSheetInteractor mLogSheetInteractor;


    @Inject
    public SyncInteractor(ServiceApi serviceApi, AppDatabase appDatabase,
                          AccountManager accountManager, LogSheetInteractor logSheetInteractor) {
        mServiceApi = serviceApi;
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtils.currentTimeMillis());

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
                            .setApplyTime(calendar.getTimeInMillis()))
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

    public void syncEventsForDaysAgo(int days, String timezone) {
        long current = DateUtils.currentTimeMillis();
        long start = DateUtils.getStartDayTimeInMs(timezone, current - days * MS_IN_DAY);
        long end = DateUtils.getEndDayTimeInMs(timezone, current);
        if (NetworkUtils.isOnlineMode()) {
            if (mSyncEventsDisposable != null) mSyncEventsDisposable.dispose();
            mSyncEventsDisposable = mServiceApi.getELDEvents(start, end)
                    .subscribeOn(Schedulers.io())
                    .map(this::filterIncorrectEvents)
                    .subscribe(this::replaceRecords, Timber::e);
        }
    }

    private void syncNewEvents() {
        Disposable syncNewEventsDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .map(t -> mAccountManager.getCurrentUserId())
                .map(userId -> ELDEventConverter.toModelList(mELDEventDao.getNewUnsyncEvents()))
                .filter(eldEvents -> !eldEvents.isEmpty())
                .flatMap(events -> Observable.fromIterable(parseELDEventsList(events)))
                .flatMapSingle(events -> mServiceApi.postNewELDEvents(setActive(events))
                        .onErrorResumeNext(Single.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? events : new ArrayList<ELDEvent>())
                )
                .filter(events -> !events.isEmpty())
                .map(this::setSync)
                .delay(SYNC_DELAY, TimeUnit.SECONDS)
                .flatMapSingle(dbEvents -> mServiceApi.getELDEvents(dbEvents.get(0).getEventTime(), dbEvents.get(dbEvents.size() - 1).getEventTime())
                        .map(this::filterIncorrectEvents)
                        .doOnSuccess(this::replaceRecords))
                .subscribe(events -> Timber.d("Sync added events:" + events),
                        Timber::e);
        mSyncCompositeDisposable.add(syncNewEventsDisposable);
    }

    private void syncUpdatedEvents() {
        Disposable syncUpdatedEventDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .map(t -> ELDEventConverter.toModelList(mELDEventDao.getUpdateUnsyncEvents()))
                .map(this::filterDoubleEvents)
                .filter(dbEvents -> !dbEvents.isEmpty())
                .flatMap(dbEvents -> Observable.fromIterable(parseELDEventsList(dbEvents)))
                .flatMapSingle(dbEvents -> mServiceApi.updateELDEvents(setActive(dbEvents))
                        .onErrorResumeNext(Single.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? dbEvents : new ArrayList<ELDEvent>())
                )
                .filter(dbEvents -> !dbEvents.isEmpty())
                .map(this::setSync)
                .delay(SYNC_DELAY, TimeUnit.SECONDS)
                .flatMapSingle(dbEvents -> mServiceApi.getELDEvents(dbEvents.get(0).getEventTime(), dbEvents.get(dbEvents.size() - 1).getEventTime())
                        .map(this::filterIncorrectEvents)
                        .doOnSuccess(this::replaceRecords)
                        .map(serverEvents -> dbEvents))
                .subscribe(dbEvents -> Timber.d("Sync updated events:" + dbEvents),
                        Timber::e);
        mSyncCompositeDisposable.add(syncUpdatedEventDisposable);
    }

    private List<ELDEvent> setSync(List<ELDEvent> events) {
        List<ELDEventEntity> entities = ELDEventConverter.toEntityList(events);
        for (ELDEventEntity entity : entities) {
            entity.setSync(SEND.ordinal());
        }
        mELDEventDao.insertAll(entities.toArray(new ELDEventEntity[entities.size()]));
        return events;
    }

    private List<ELDEvent> setActive(List<ELDEvent> events) {
        ArrayList<ELDEvent> activeEvents = new ArrayList<>();
        for (ELDEvent event : events) {
            ELDEvent activeEvent = event.clone();
            activeEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
            activeEvents.add(activeEvent);
        }
        return activeEvents;
    }

    public void replaceRecords(List<ELDEvent> events) {
        for (int i = events.size() - 1; i >= 0; i--) {
            ELDEvent event = events.get(i);
            ELDEventEntity oldEvent = mELDEventDao.getEventById(event.getId(), event.getDriverId());

            if (oldEvent != null) {
                ELDEventEntity updatedEvent = ELDEventConverter.toEntity(event);
                updatedEvent.setInnerId(oldEvent.getInnerId());
                mELDEventDao.insertEvent(updatedEvent);
            } else {
                ELDEventEntity sentEvent = mELDEventDao.getSentEvent(event.getDriverId(), event.getMobileTime());

                if (sentEvent != null) {
                    sentEvent.setSync(event.getSync());
                    sentEvent.setId(event.getId());
                    sentEvent.setLocation(event.getLocation());
                    mELDEventDao.insertEvent(sentEvent);
                } else {
                    mELDEventDao.insertEvent(ELDEventConverter.toEntity(event));
                }
            }
        }
    }

    public void syncLogSheetHeaders() {
        Disposable syncHeadersDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
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
                .onErrorReturn(throwable -> new ArrayList<>())
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

    private List<ELDEvent> filterIncorrectEvents(List<ELDEvent> list) {
        ListIterator<ELDEvent> iterator = list.listIterator();
        while (iterator.hasNext()) {
            ELDEvent event = iterator.next();
            if (event.getEventCode() < 0 || event.getEventType() < 0) {
                iterator.remove();
            }
        }
        return list;
    }

    /**
     * If user updates single event several times we should send separate request to each
     * update to prevent loosing data
     * @param list all updated events
     * @return filtered list
     */
    private List<ELDEvent> filterDoubleEvents(List<ELDEvent> list) {
        HashSet<Long> uniqueTimes = new HashSet<>();
        ListIterator<ELDEvent> iterator = list.listIterator();
        while (iterator.hasNext()) {
            Long time = iterator.next().getMobileTime();
            if (uniqueTimes.contains(time)) {
                iterator.remove();
            } else {
                uniqueTimes.add(time);
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
