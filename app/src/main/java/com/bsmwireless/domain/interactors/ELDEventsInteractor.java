package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;
import static com.bsmwireless.common.utils.DateUtils.SEC_IN_HOUR;

public class ELDEventsInteractor {

    private Disposable mSyncEventsDisposable;
    private ServiceApi mServiceApi;
    private BlackBoxInteractor mBlackBoxInteractor;
    private UserInteractor mUserInteractor;
    private DutyTypeManager mDutyTypeManager;
    private ELDEventDao mELDEventDao;
    private PreferencesManager mPreferencesManager;

    private static String mTimezone = "";

    @Inject
    public ELDEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager,
                               AppDatabase appDatabase, UserInteractor userInteractor,
                               BlackBoxInteractor blackBoxInteractor, DutyTypeManager dutyTypeManager) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mUserInteractor = userInteractor;
        mBlackBoxInteractor = blackBoxInteractor;
        mDutyTypeManager = dutyTypeManager;
        mELDEventDao = appDatabase.ELDEventDao();
        mPreferencesManager = preferencesManager;

        mUserInteractor.getTimezone().subscribe(timezone -> mTimezone = timezone);
    }

    public Flowable<List<ELDEvent>> getELDEvents(long startTime, long endTime) {
        return getDutyEventsFromDB(startTime, endTime);
    }

    public Flowable<List<ELDEvent>> getDutyEventsFromDB(long startTime, long endTime) {
        int driverId = mPreferencesManager.getDriverId();
        return mELDEventDao.getDutyEventsFromStartToEndTime(startTime, endTime, driverId)
                .map(ELDEventConverter::toModelList);
    }

    public Flowable<List<ELDEvent>> getLatestActiveDutyEventFromDB(long latestTime) {
        return mELDEventDao.getLatestActiveDutyEvent(latestTime, mPreferencesManager.getDriverId())
                .map(ELDEventConverter::toModelList);
    }

    public List<ELDEvent> getLatestActiveDutyEventFromDBSync(long latestTime) {
        return ELDEventConverter.toModelList(mELDEventDao.getLatestActiveDutyEventSync(latestTime, mPreferencesManager.getDriverId()));
    }

    public Flowable<List<ELDEvent>> getActiveDutyEventsFromDB(long startTime, long endTime) {
        int driverId = mPreferencesManager.getDriverId();
        return mELDEventDao.getActiveDutyEventsAndFromStartToEndTime(startTime, endTime, driverId)
                .map(ELDEventConverter::toModelList);
    }

    public List<ELDEvent> getActiveEventsFromDBSync(long startTime, long endTime) {
        int driverId = mPreferencesManager.getDriverId();
        return ELDEventConverter.toModelList(mELDEventDao.getActiveEventsFromStartToEndTimeSync(startTime, endTime, driverId));
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
                e.onError(RetrofitException.networkError(new ConnectException()));
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
                e.onError(RetrofitException.networkError(new ConnectException()));
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
                e.onError(RetrofitException.networkError(new ConnectException()));
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
        return postNewELDEvents(getEvents(dutyType))
                .doOnNext(isSuccess -> mDutyTypeManager.setDutyType(dutyType, true));
    }

    public Observable<Boolean> postLogoutEvent() {
        return mServiceApi.logout(getEvent(ELDEvent.LoginLogoutCode.LOGOUT))
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS))
                .switchMap(aBoolean -> mBlackBoxInteractor.shutdown(aBoolean));
    }

    private ArrayList<ELDEvent> getEvents(DutyType dutyType) {
        ArrayList<ELDEvent> events = new ArrayList<>();
        DutyType current = mDutyTypeManager.getDutyType();

        //clear PU status
        if (current == DutyType.PERSONAL_USE) {
            events.add(getEvent(DutyType.CLEAR));

            if (dutyType == DutyType.OFF_DUTY) {
                return events;
            }
        }

        //clear YM status
        if (current == DutyType.YARD_MOVES) {
            events.add(getEvent(DutyType.CLEAR));

            if (dutyType == DutyType.ON_DUTY) {
                return events;
            }
        }

        switch (dutyType) {
            case PERSONAL_USE:
                //switch to off-duty if needed
                if (current != DutyType.OFF_DUTY) {
                    events.add(getEvent(DutyType.OFF_DUTY));
                }
                events.add(getEvent(DutyType.PERSONAL_USE));
                break;

            case YARD_MOVES:
                //switch to on-duty if needed
                if (current != DutyType.ON_DUTY) {
                    events.add(getEvent(DutyType.ON_DUTY));
                }
                events.add(getEvent(DutyType.YARD_MOVES));
                break;

            default:
                events.add(getEvent(dutyType));
                break;
        }

        return events;
    }

    public boolean isConnected() {
        return mBlackBoxInteractor.getLastData().getBoxId() != 0;
    }

    public ELDEvent getEvent(ELDEvent.LoginLogoutCode loginLogoutCode) {
        ELDEvent event = getEvent(getBlackBoxState(mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE), false);
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setOrigin(ELDEvent.EventOrigin.DRIVER.getValue());
        event.setEventType(ELDEvent.EventType.LOGIN_LOGOUT.getValue());
        event.setEventCode(loginLogoutCode.getValue());

        return event;
    }

    public ELDEvent getEvent(ELDEvent.EnginePowerCode enginePowerCode) {
        ELDEvent event = getEvent(getBlackBoxState(mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE), true);
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setOrigin(ELDEvent.EventOrigin.AUTOMATIC_RECORD.getValue());
        event.setEventType(ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue());
        event.setEventCode(enginePowerCode.getValue());

        return event;
    }

    public ELDEvent getEvent(DutyType dutyType) {
        return getEvent(dutyType, false);
    }

    public ELDEvent getEvent(DutyType dutyType, boolean isAuto) {
        ELDEvent event = getEvent(getBlackBoxState(dutyType == DutyType.PERSONAL_USE), isAuto);
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setEventType(dutyType.getType());
        event.setEventCode(dutyType.getCode());

        return event;
    }

    private ELDEvent getEvent(BlackBoxModel blackBoxModel, boolean isAuto) {
        long currentTime = System.currentTimeMillis();
        int driverId = mUserInteractor.getDriverId();

        ELDEvent event = new ELDEvent();
        event.setEventTime(currentTime);
        event.setEngineHours(blackBoxModel.getEngineHours());
        event.setLat(blackBoxModel.getLat());
        event.setLng(blackBoxModel.getLon());
        event.setLocation("");
        event.setDistance(0);
        event.setMalfunction(false);
        event.setDiagnostic(false);
        event.setTimezone(mTimezone);
        event.setDriverId(driverId);
        event.setBoxId(mPreferencesManager.getBoxId());
        event.setVehicleId(mPreferencesManager.getVehicleId());
        event.setMobileTime(currentTime);
        event.setOrigin(isAuto ? ELDEvent.EventOrigin.AUTOMATIC_RECORD.getValue() : ELDEvent.EventOrigin.DRIVER.getValue());

        return event;
    }

    private BlackBoxModel getBlackBoxState(boolean isInPersonalUse) {
        BlackBoxModel blackBoxState = mBlackBoxInteractor.getLastData();
        if (isInPersonalUse) {
            blackBoxState.setLat(roundOneDecimal(blackBoxState.getLat()));
            blackBoxState.setLon(roundOneDecimal(blackBoxState.getLon()));
        }
        blackBoxState.setEngineHours(multiplyAndRound(blackBoxState.getEngineHours()));
        return blackBoxState;
    }

    private double roundOneDecimal(double d) {
        return Math.round(d * 10) / 10;
    }

    private int multiplyAndRound(int sec) {
        return Math.round(sec / (float) SEC_IN_HOUR * 10);
    }
}
