package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.ResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;

public class SyncEventsInteractor {
    private static final int MAX_EVENTS_IN_REQUEST = 5;
    private CompositeDisposable mSyncEventsDisposable;
    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;
    private ELDEventDao mELDEventDao;
    private boolean mIsSyncActive;
    private ResponseMessage mErrorResponse;


    @Inject
    public SyncEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager,
                                AppDatabase appDatabase) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mELDEventDao = appDatabase.ELDEventDao();
        mErrorResponse = new ResponseMessage("error");
        mSyncEventsDisposable = new CompositeDisposable();
    }

    public void startSync() {
        if (!mIsSyncActive) {
            mIsSyncActive = true;
            syncNewEvents();
            syncUpdatedEvents();
        }
    }

    private void syncNewEvents() {
        mSyncEventsDisposable.add(Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .filter(t -> mPreferencesManager.getBoxId() != PreferencesManager.NOT_FOUND_VALUE)
                .map(t -> filterIncorrectEvents(ELDEventConverter.toModelList(mELDEventDao.getNewUnsyncEvents())))
                .filter(eldEvents -> !eldEvents.isEmpty())
                .flatMap(events -> Observable.fromIterable(parseELDEventsList(events)))
                .flatMap(events -> mServiceApi.postNewELDEvents(events)
                        .onErrorResumeNext(Observable.just(mErrorResponse))
                        .onExceptionResumeNext(Observable.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? events : new ArrayList<ELDEvent>())
                )
                .filter(eldEvents -> !eldEvents.isEmpty())
                .flatMap(events -> {
                    ELDEventEntity[] entities = ELDEventConverter.toEntityList(events).toArray(new ELDEventEntity[events.size()]);
                    mELDEventDao.deleteAll(entities);
                    return mServiceApi.getELDEvents(events.get(0).getEventTime(), events.get(events.size() - 1).getEventTime());
                })
                .subscribe(events -> {
                            ELDEventEntity[] entities = ELDEventConverter.toEntityArray(events);
                            mELDEventDao.insertAll(entities);
                        },
                        error -> Timber.e(error))
        );
    }

    private void syncUpdatedEvents() {
        mSyncEventsDisposable.add(Observable.interval(Constants.SYNC_TIMEOUT_IN_MIN, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .filter(t -> NetworkUtils.isOnlineMode())
                .filter(t -> mPreferencesManager.getBoxId() != PreferencesManager.NOT_FOUND_VALUE)
                .map(t -> filterIncorrectEvents(ELDEventConverter.toModelList(mELDEventDao.getUpdateUnsyncEvents())))
                .filter(events -> !events.isEmpty())
                .flatMap(events -> Observable.fromIterable(parseELDEventsList(events)))
                .flatMap(events -> mServiceApi.updateELDEvents(events)
                        .onErrorResumeNext(Observable.just(mErrorResponse))
                        .onExceptionResumeNext(Observable.just(mErrorResponse))
                        .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS) ? events : new ArrayList<ELDEvent>())
                )
                .filter(eldEvents -> !eldEvents.isEmpty())
                .subscribe(events -> {
                            ELDEventEntity[] entities = ELDEventConverter.toEntityArray(events);
                            mELDEventDao.insertAll(entities);
                        },
                        error -> Timber.e(error))
        );
    }

    public void stopSync() {
        if (mIsSyncActive) {
            mSyncEventsDisposable.dispose();
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

    private List<List<ELDEvent>> parseELDEventsList(List<ELDEvent> events) {
        List<List<ELDEvent>> list = new ArrayList<>();
        if (events.size() < MAX_EVENTS_IN_REQUEST) {
            list.add(events);
        } else {
            for (int i = 0; i < events.size(); i += MAX_EVENTS_IN_REQUEST) {
                int toIndex = (i + MAX_EVENTS_IN_REQUEST) < events.size() - 1 ? (i + MAX_EVENTS_IN_REQUEST) : events.size() - 1;
                list.add(events.subList(i, toIndex));
            }
        }
        return list;
    }
}
