package com.bsmwireless.domain.interactors;

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

public class ELDEventsInteractor {

    private Disposable mSyncEventsDisposable;
    private ServiceApi mServiceApi;
    private BlackBoxInteractor mBlackBoxInteractor;
    private UserInteractor mUserInteractor;
    private DutyManager mDutyManager;
    private ELDEventDao mELDEventDao;
    private PreferencesManager mPreferencesManager;

    @Inject
    public ELDEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager,
                               AppDatabase appDatabase, UserInteractor userInteractor,
                               BlackBoxInteractor blackBoxInteractor, DutyManager dutyManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mUserInteractor = userInteractor;
        mBlackBoxInteractor = blackBoxInteractor;
        mDutyManager = dutyManager;
        mELDEventDao = appDatabase.ELDEventDao();
        mPreferencesManager = preferencesManager;
    }

    public void getELDEventsFromServer(Long startTime, Long endTime) {
        mServiceApi.getELDEvents(startTime, endTime)
                .subscribeOn(Schedulers.io())
                .subscribe(eldEvents -> {
                            ELDEventEntity[] entities = ELDEventConverter.toEntityArray(eldEvents);
                            mELDEventDao.insertAll(entities);
                        },
                        error -> {
                            Timber.e(error);
                        });
    }

    public Flowable<List<ELDEvent>> getELDEvents(long startTime, long endTime) {
        return getDutyEventsFromDB(startTime, endTime);
    }

    public Flowable<List<ELDEvent>> getDutyEventsFromDB(long startTime, long endTime) {
        int driverId = mPreferencesManager.getDriverId();
        return mELDEventDao.getDutyEventsFromStartToEndTime(startTime, endTime, driverId)
                .map(ELDEventConverter::toModelList);
    }

    public Flowable<ELDEvent> getLatestActiveDutyEventFromDB(long latestTime) {
        return mELDEventDao.getLatestActiveDutyEvent(latestTime, mPreferencesManager.getDriverId())
                .map(ELDEventConverter::toModel);
    }

    public Flowable<List<ELDEvent>> getActiveDutyEventsFromDB(long startTime, long endTime) {
        int driverId = mPreferencesManager.getDriverId();
        return mELDEventDao.getActiveDutyEventsAndFromStartToEndTime(startTime, endTime, driverId)
                .map(ELDEventConverter::toModelList);
    }

    public Observable<long[]> updateELDEvents(List<ELDEvent> events) {
        return Observable.fromCallable(() ->
                mELDEventDao.insertAll(ELDEventConverter.toEntityArray(events, ELDEventEntity.SyncType.UPDATE_UNSYNC)));
    }

    public Observable<Long> postNewELDEvent(ELDEvent event) {
        return Observable.fromCallable(() ->
                mELDEventDao.insertEvent(ELDEventConverter.toEntity(event, ELDEventEntity.SyncType.NEW_UNSYNC)));
    }

    public Observable<long[]> postNewELDEvents(List<ELDEvent> events) {
        return Observable.fromCallable(() ->
                mELDEventDao.insertAll(ELDEventConverter.toEntityArray(events, ELDEventEntity.SyncType.NEW_UNSYNC)));
    }

    public void storeUnidentifiedEvents(List<ELDEvent> events) {
        //TODO: probably additional action with unidentified records is required.
        mELDEventDao.insertAll(ELDEventConverter.toEntityArray(events));
    }

    public Observable<long[]> postNewDutyTypeEvent(DutyType dutyType) {
        return mBlackBoxInteractor.getData()
                .flatMap(blackBoxModel -> postNewELDEvents(getEvents(dutyType, blackBoxModel)))
                .doOnNext(isSuccess -> mDutyManager.setDutyType(dutyType, true));
    }

    public ArrayList<ELDEvent> getEvents(DutyType dutyType, BlackBoxModel blackBoxModel) {
        ArrayList<ELDEvent> events = new ArrayList<>();

        //clear PU or YM status
        if (mDutyManager.getDutyType() == DutyType.PERSONAL_USE || mDutyManager.getDutyType() == DutyType.YARD_MOVES) {
            events.add(getEvent(DutyType.CLEAR, ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION, blackBoxModel));
        }

        switch (dutyType) {
            case PERSONAL_USE:
                //switch to off-duty if needed
                if (mDutyManager.getDutyType() != DutyType.OFF_DUTY) {
                    events.add(getEvent(DutyType.OFF_DUTY, ELDEvent.EventType.DUTY_STATUS_CHANGING, blackBoxModel));
                }
                events.add(getEvent(DutyType.PERSONAL_USE, ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION, blackBoxModel));
                break;

            case YARD_MOVES:
                //switch to on-duty if needed
                if (mDutyManager.getDutyType() != DutyType.ON_DUTY) {
                    events.add(getEvent(DutyType.ON_DUTY, ELDEvent.EventType.DUTY_STATUS_CHANGING, blackBoxModel));
                }
                events.add(getEvent(DutyType.YARD_MOVES, ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION, blackBoxModel));
                break;

            default:
                events.add(getEvent(dutyType, ELDEvent.EventType.DUTY_STATUS_CHANGING, blackBoxModel));
                break;
        }

        return events;
    }

    private ELDEvent getEvent(DutyType dutyType, ELDEvent.EventType eventType, BlackBoxModel blackBoxModel) {
        long currentTime = System.currentTimeMillis();
        int driverId = mUserInteractor.getDriverId();

        ELDEvent event = new ELDEvent();
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setOrigin(ELDEvent.EventOrigin.DRIVER.getValue());
        event.setEventType(eventType.getValue());
        event.setEventCode(dutyType.getCode());
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
