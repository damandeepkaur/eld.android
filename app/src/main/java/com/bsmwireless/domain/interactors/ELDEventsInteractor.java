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

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ELDEventsInteractor {
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

    public Observable<List<ELDEvent>> getELDEvents(long startTime, long endTime) {
        return mServiceApi.getELDEvents(startTime, endTime)
                .onErrorReturn(throwable -> ELDEventConverter.toModelList(mELDEventDao.getEventsForInterval(startTime, endTime)))
                .doOnNext(events -> storeEvents(events, false));
    }

    public Observable<ResponseMessage> postNewELDEvent(ELDEvent event) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.postNewELDEvent(event, mPreferencesManager.getSelectedBoxId())
                    .doOnError(throwable -> storeEvent(event, false));
        } else {
            storeEvent(event, false);
            return Observable.error(new NetworkErrorException("No Internet Connection"));
        }
    }

    public Observable<ResponseMessage> postNewELDEvents(List<ELDEvent> events) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.postNewELDEvents(events, mPreferencesManager.getSelectedBoxId())
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
