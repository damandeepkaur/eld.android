package com.bsmwireless.domain.interactors;

import android.accounts.NetworkErrorException;

import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.ResponseMessage;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ELDEventsInteractor {
    private Disposable mSyncEventsDisposable;
    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;
    private ELDEventDao mELDEventDao;

    @Inject
    public ELDEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mELDEventDao = appDatabase.ELDEventDao();
    }

    public Observable<ResponseMessage> updateELDEvents(List<ELDEvent> events) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.updateELDEvents(events)
                    .doOnError(throwable -> storeEvents(events, false));
        } else {
            storeEvents(events, false);
            return Observable.error(new NetworkErrorException("No Internet Connection"));
        }
    }

    public Flowable<List<ELDEvent>> getELDEvents(long startTime, long endTime) {
        return getELDEventsFromDB(startTime, endTime);
    }

    public Flowable<List<ELDEvent>> getELDEventsFromDB(long startTime, long endTime) {
        return mELDEventDao.getEventFromStartToEndTime(startTime, endTime)
                .map(ELDEventConverter::toModelList);
    }

    public void syncELDEvents(Long startTime, Long endTime) {
        if (mSyncEventsDisposable != null) {
            mSyncEventsDisposable.dispose();
        }
        mSyncEventsDisposable = mServiceApi.getELDEvents(startTime, endTime)
                .subscribeOn(Schedulers.io())
                .subscribe(eldEvents -> storeEvents(eldEvents, false), Timber::d);
    }

    public Observable<ResponseMessage> postNewELDEvent(ELDEvent event) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.postNewELDEvent(event)
                    .doOnError(throwable -> storeEvent(event, false));
        } else {
            storeEvent(event, false);
            return Observable.error(new NetworkErrorException("No Internet Connection"));
        }
    }

    public Observable<ResponseMessage> postNewELDEvents(List<ELDEvent> events) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.postNewELDEvents(events)
                    .doOnError(throwable -> events.forEach(event -> storeEvent(event, false)));
        } else {
            storeEvents(events, false);
            return Observable.error(new NetworkErrorException("No Internet Connection"));
        }
    }

    public void storeEvent(ELDEvent event, boolean isSynced) {
        ELDEventEntity entity = ELDEventConverter.toEntity(event);
        entity.setSync(isSynced);
        mELDEventDao.insertEvent(entity);
    }

    public void storeEvents(List<ELDEvent> events, boolean isSynced) {
        ELDEventEntity[] entities = ELDEventConverter.toEntityList(events).toArray(new ELDEventEntity[events.size()]);
        for (ELDEventEntity entity : entities) {
            entity.setSync(isSynced);
        }
        mELDEventDao.insertAll(entities);
    }

    public void sendUnsyncEventsIfExist() {
        mELDEventDao.getUnsyncEvents().subscribe(eldEventEntities -> {
            List<ELDEvent> unsyncEvents = ELDEventConverter.toModelList(eldEventEntities);
            if (unsyncEvents != null && !unsyncEvents.isEmpty()) {
                postNewELDEvents(unsyncEvents)
                        .subscribe(responseMessage -> storeEvents(unsyncEvents, true));
            }
        });
    }
}
