package com.bsmwireless.domain.interactors;

import android.accounts.NetworkErrorException;

import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;

public class ELDEventsInteractor {

    private Disposable mSyncEventsDisposable;
    private ServiceApi mServiceApi;
    private PreferencesManager mPreferencesManager;
    private BlackBoxInteractor mBlackBoxInteractor;
    private UserInteractor mUserInteractor;
    private DutyManager mDutyManager;
    private ELDEventDao mELDEventDao;

    @Inject
    public ELDEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager, AppDatabase appDatabase, UserInteractor userInteractor, BlackBoxInteractor blackBoxInteractor, DutyManager dutyManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mUserInteractor = userInteractor;
        mBlackBoxInteractor = blackBoxInteractor;
        mDutyManager = dutyManager;
        mELDEventDao = appDatabase.ELDEventDao();
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
                .doOnNext(events -> {
                    //remove doubled records
                    ArrayList<Long> times = new ArrayList<>();
                    for (ELDEvent event : events) {
                        times.add(event.getMobileTime());
                    }
                    mELDEventDao.deleteDoubledEvents(times);
                })
                .subscribeOn(Schedulers.io())
                .subscribe(eldEvents -> storeEvents(eldEvents, false), Timber::d);
    }

    public Observable<Boolean> updateELDEvents(List<ELDEvent> events) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.updateELDEvents(events)
                    .doOnError(throwable -> storeEvents(events, false))
                    .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
        } else {
            return Observable.create(e -> {
                storeEvents(events, false);
                e.onError(new NetworkErrorException("No Internet Connection"));
            });
        }
    }

    public Observable<Boolean> postNewELDEvent(ELDEvent event) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.postNewELDEvent(event)
                    .doOnError(throwable -> storeEvent(event, false))
                    .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
        } else {
            return Observable.create(e -> {
                storeEvent(event, false);
                e.onError(new NetworkErrorException("No Internet Connection"));
            });
        }
    }

    public Observable<Boolean> postNewELDEvents(List<ELDEvent> events) {
        if (NetworkUtils.isOnlineMode()) {
            return mServiceApi.postNewELDEvents(events)
                    .doOnError(throwable -> storeEvents(events, false))
                    .doOnNext(responseMessage -> storeEvents(events, true))
                    .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
        } else {
            return Observable.create(e -> {
                storeEvents(events, false);
                e.onError(new NetworkErrorException("No Internet Connection"));
            });
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
                postNewELDEvents(unsyncEvents).subscribe(responseMessage -> storeEvents(unsyncEvents, true));
            }
        });
    }

    public Observable<Boolean> postNewDutyTypeEvent(DutyType dutyType) {
        return mBlackBoxInteractor.getData()
                .flatMap(blackBoxModel -> postNewELDEvents(getEvents(dutyType, blackBoxModel)))
                .doOnNext(isSuccess -> mDutyManager.setDutyType(dutyType));
    }

    public ArrayList<ELDEvent> getEvents(DutyType dutyType, BlackBoxModel blackBoxModel) {
        ArrayList<ELDEvent> events = new ArrayList<>();

        switch (dutyType) {
            case PERSONAL_USE:
                if (mDutyManager.getDutyType() != DutyType.OFF_DUTY) {
                    events.add(getEvent(DutyType.OFF_DUTY, blackBoxModel));
                }
                events.add(getEvent(DutyType.PERSONAL_USE, blackBoxModel));
                break;

            case YARD_MOVES:
                if (mDutyManager.getDutyType() != DutyType.ON_DUTY) {
                    events.add(getEvent(DutyType.ON_DUTY, blackBoxModel));
                }
                events.add(getEvent(DutyType.YARD_MOVES, blackBoxModel));
                break;

            default:
                events.add(getEvent(dutyType, blackBoxModel));
                break;
        }

        return events;
    }

    private ELDEvent getEvent(DutyType dutyType, BlackBoxModel blackBoxModel) {
        long currentTime = System.currentTimeMillis();
        int driverId = mUserInteractor.getDriverId();

        ELDEvent event = new ELDEvent();
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setOrigin(ELDEvent.EventOrigin.DRIVER.getValue());
        event.setEventType((dutyType == DutyType.PERSONAL_USE || dutyType == DutyType.YARD_MOVES) ?
                ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue() :
                ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue());
        event.setEventCode(dutyType.getValue());
        event.setEventTime(currentTime);
        event.setEngineHours(blackBoxModel.getEngineHours());
        event.setLat(blackBoxModel.getLat());
        event.setLng(blackBoxModel.getLon());
        event.setLocation("");
        event.setDistance(0);
        event.setMalfunction(false);
        event.setDiagnostic(false);
        event.setTimezone(mUserInteractor.getTimezoneSync(driverId));
        event.setDriverId(driverId);
        event.setBoxId(mPreferencesManager.getBoxId());
        event.setVehicleId(mPreferencesManager.getVehicleId());
        event.setMobileTime(currentTime);

        return event;
    }
}
