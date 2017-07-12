package com.bsmwireless.domain.interactors;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
    private Context mContext;

    @Inject
    public ELDEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase, Context context) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mELDEventDao = appDatabase.getELDEventDao();
        mContext = context;
    }

    public Observable<ResponseMessage> updateELDEvents(List<ELDEvent> events) {
        if (isOnlineMode()) {
            return mServiceApi.updateELDEvents(events)
                    .subscribeOn(Schedulers.io())
                    .doOnError(throwable -> events.forEach(event -> storeEvent(event, false)));
        } else {
            events.forEach(event -> storeEvent(event, false));
            return Observable.error(new NetworkErrorException("No Internet Connection"));
        }
    }

    public Observable<List<ELDEvent>> getELDEvents(long startTime, long endTime) {
        return mServiceApi.getELDEvents(startTime, endTime)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> ELDEventConverter.toModelList(mELDEventDao.getEventsForInterval(startTime, endTime)))
                .doOnNext(events -> storeEvents(events, false));
    }

    public Observable<ResponseMessage> postNewELDEvent(ELDEvent event) {
        if (isOnlineMode()) {
            return mServiceApi.postNewELDEvent(event, mPreferencesManager.getSelectedBoxId())
                    .subscribeOn(Schedulers.io())
                    .doOnError(throwable -> storeEvent(event, false));
        } else {
            storeEvent(event, false);
            return Observable.error(new NetworkErrorException("No Internet Connection"));
        }
    }

    public Observable<ResponseMessage> postNewELDEvents(List<ELDEvent> events) {
        if (isOnlineMode()) {
            return mServiceApi.postNewELDEvents(events, mPreferencesManager.getSelectedBoxId())
                    .subscribeOn(Schedulers.io())
                    .doOnError(throwable -> events.forEach(event -> storeEvent(event, false)));
        } else {
            events.forEach(event -> storeEvent(event, false));
            return Observable.error(new NetworkErrorException("No Internet Connection"));
        }
    }

    private void storeEvent(ELDEvent event, boolean isSynced) {
        ELDEventEntity entity = ELDEventConverter.toEntity(event);
        entity.setIsSync(isSynced);
        mELDEventDao.insertEvent(entity);
    }

    private void storeEvents(List<ELDEvent> events, boolean isSynced) {
        ELDEventEntity[] entities = ELDEventConverter.toEntityList(events).toArray(new ELDEventEntity[events.size()]);
        for (int i = 0; i < entities.length; i++) {
            entities[i].setIsSync(isSynced);
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

    public boolean isOnlineMode() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
