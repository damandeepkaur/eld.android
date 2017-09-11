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
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.ResponseMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;
import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;

public class SyncInteractor {
    private static final int MAX_EVENTS_IN_REQUEST = 5;
    private CompositeDisposable mSyncCompositeDisposable;
    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;
    private ELDEventDao mELDEventDao;
    private volatile boolean mIsSyncActive;
    private AccountManager mAccountManager;
    private ResponseMessage mErrorResponse;
    private Disposable mSyncEventsForDayDisposable;


    @Inject
    public SyncInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager,
                          AppDatabase appDatabase, AccountManager accountManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mELDEventDao = appDatabase.ELDEventDao();
        mErrorResponse = new ResponseMessage("error");
        mSyncCompositeDisposable = new CompositeDisposable();
        mAccountManager = accountManager;
    }

    public void startSync() {
        if (!mIsSyncActive) {
            mIsSyncActive = true;
            syncNewEvents();
            syncUpdatedEvents();
        }
    }

    public void syncEventsForDay(Calendar dayCalendar, String timezone) {
        long startTime = DateUtils.getStartDate(timezone, dayCalendar);
        long endTime = startTime + MS_IN_DAY;

        if (mSyncEventsForDayDisposable != null) mSyncEventsForDayDisposable.dispose();
        mSyncEventsForDayDisposable = Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .take(3)
                .map(t -> ELDEventConverter.toModelList(mELDEventDao.getUpdateUnsyncEvents()))
                .map(dbEvents -> filterInactiveEvents(dbEvents))
                .filter(eldEvents -> eldEvents.isEmpty())
                .flatMap(eldEvents -> mServiceApi.getELDEvents(startTime, endTime))
                .subscribe(eldEventsFromServer -> {
                            List<ELDEventEntity> entities = mELDEventDao.getEventsFromStartToEndTimeSync(
                                    startTime, endTime, mAccountManager.getCurrentUserId());
                            List<ELDEvent> eventsFromDB = ELDEventConverter.toModelList(entities);
                            if (!eldEventsFromServer.equals(eventsFromDB)) {
                                ELDEventEntity[] entitiesArray = ELDEventConverter.toEntityArray(eldEventsFromServer);
                                mELDEventDao.insertAll(entitiesArray);
                            }
                        },
                        error -> Timber.e(error));
    }

    private void syncNewEvents() {
        mSyncCompositeDisposable.add(Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .filter(t -> mPreferencesManager.getBoxId() != PreferencesManager.NOT_FOUND_VALUE)
                .map(t -> ELDEventConverter.toModelList(mELDEventDao.getNewUnsyncEvents()))
                .filter(eldEvents -> !eldEvents.isEmpty())
                .map(events -> filterIncorrectEvents(events))
                .filter(eldEvents -> !eldEvents.isEmpty())
                .flatMap(events -> Observable.fromIterable(parseELDEventsList(events)))
                .flatMap(events -> mServiceApi.postNewELDEvents(events)
                        .onErrorResumeNext(Observable.just(mErrorResponse))
                        .onExceptionResumeNext(Observable.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? events : new ArrayList<ELDEvent>())
                )
                .filter(events -> !events.isEmpty())
                .delay(1, TimeUnit.SECONDS)
                .flatMap(dbEvents -> mServiceApi.getELDEvents(dbEvents.get(0).getEventTime(), dbEvents.get(dbEvents.size() - 1).getEventTime())
                        .doOnNext(serverEvents -> {
                            ELDEventEntity[] oldEntities = ELDEventConverter.toEntityArray(dbEvents);
                            mELDEventDao.deleteAll(oldEntities);
                            ELDEventEntity[] entities = ELDEventConverter.toEntityArray(serverEvents);
                            mELDEventDao.insertAll(entities);
                        }))
                .subscribe(events -> Timber.d("Sync added events:" + events),
                        error -> Timber.e(error))
        );
    }

    private void syncUpdatedEvents() {
        mSyncCompositeDisposable.add(Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .filter(t -> mPreferencesManager.getBoxId() != PreferencesManager.NOT_FOUND_VALUE)
                .map(t -> ELDEventConverter.toModelList(mELDEventDao.getUpdateUnsyncEvents()))
                .filter(dbEvents -> !dbEvents.isEmpty())
                .map(dbEvents -> filterIncorrectEvents(dbEvents))
                .filter(dbEvents -> !dbEvents.isEmpty())
                .flatMap(dbEvents -> Observable.fromIterable(parseELDEventsList(dbEvents)))
                .flatMap(dbEvents -> mServiceApi.updateELDEvents(filterInactiveEvents(dbEvents))
                        .onErrorResumeNext(Observable.just(mErrorResponse))
                        .onExceptionResumeNext(Observable.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? dbEvents : new ArrayList<ELDEvent>())
                )
                .filter(events -> !events.isEmpty())
                .flatMap(dbEvents -> mServiceApi.getELDEvents(dbEvents.get(0).getEventTime(), dbEvents.get(dbEvents.size() - 1).getEventTime())
                        .doOnNext(serverEvents -> {
                            ELDEventEntity[] oldEntities = ELDEventConverter.toEntityArray(dbEvents);
                            mELDEventDao.deleteAll(oldEntities);
                            ELDEventEntity[] entities = ELDEventConverter.toEntityArray(serverEvents);
                            mELDEventDao.insertAll(entities);
                        })
                        .map(serverEvents -> dbEvents))
                .subscribe(dbEvents -> Timber.d("Sync updated events:" + dbEvents),
                        error -> Timber.e(error))
        );
    }

    public void stopSync() {
        if (mIsSyncActive) {
            mSyncCompositeDisposable.dispose();
        }
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
            if (event.getStatus() != ELDEvent.StatusCode.ACTIVE.getValue()) {
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
}
