package com.bsmwireless.domain.interactors;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.network.authenticator.TokenManager;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AppDatabase;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.data.storage.eldevents.ELDEventConverter;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.models.AppInfo;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.Malfunction;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.widgets.alerts.DutyType;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;
import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.common.utils.DateUtils.SEC_IN_HOUR;

public final class ELDEventsInteractor {

    private static String mTimezone = "";
    private ServiceApi mServiceApi;
    private BlackBoxInteractor mBlackBoxInteractor;
    private UserInteractor mUserInteractor;
    private DutyTypeManager mDutyTypeManager;
    private ELDEventDao mELDEventDao;
    private UserDao mUserDao;
    private PreferencesManager mPreferencesManager;
    private AccountManager mAccountManager;
    private TokenManager mTokenManager;
    private LogSheetInteractor mLogSheetInteractor;
    private ELDEventEntity mLatestEldEvent;

    @Inject
    public ELDEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager,
                               AppDatabase appDatabase, UserInteractor userInteractor,
                               BlackBoxInteractor blackBoxInteractor, DutyTypeManager dutyTypeManager,
                               AccountManager accountManager, TokenManager tokenManager,
                               LogSheetInteractor logSheetInteractor) {
        mServiceApi = serviceApi;
        mPreferencesManager = preferencesManager;
        mUserInteractor = userInteractor;
        mBlackBoxInteractor = blackBoxInteractor;
        mDutyTypeManager = dutyTypeManager;
        mELDEventDao = appDatabase.ELDEventDao();
        mUserDao = appDatabase.userDao();
        mPreferencesManager = preferencesManager;
        mAccountManager = accountManager;
        mTokenManager = tokenManager;
        mLogSheetInteractor = logSheetInteractor;

        mUserInteractor.getTimezone().subscribe(timezone -> mTimezone = timezone);

        mELDEventDao.getLatestEvent(mAccountManager.getCurrentUserId(), ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                Malfunction.POSITIONING_COMPLIANCE.getCode(), ELDEvent.StatusCode.ACTIVE.getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eldEventEntity -> mLatestEldEvent = eldEventEntity);
    }

    public Single<List<ELDEvent>> getEventsFromDBOnce(long startTime, long endTime) {
        int driverId = mAccountManager.getCurrentUserId();
        return mELDEventDao.getEventsFromStartToEndTimeOnce(startTime, endTime, driverId)
                .map(ELDEventConverter::toModelList);
    }

    public Flowable<List<ELDEvent>> getDutyEventsFromDB(long startTime, long endTime) {
        int driverId = mAccountManager.getCurrentUserId();
        return mELDEventDao.getDutyEventsFromStartToEndTime(startTime, endTime, driverId)
                .map(ELDEventConverter::toModelList);
    }

    public List<ELDEvent> getLatestActiveDutyEventFromDBSync(long latestTime, int userId) {
        return ELDEventConverter.toModelList(mELDEventDao.getLatestActiveDutyEventSync(latestTime, userId));
    }

    public Single<List<ELDEvent>> getLatestActiveDutyEventFromDBOnce(long latestTime, int userId) {
        return mELDEventDao.getLatestActiveDutyEventOnce(latestTime, userId)
                .map(ELDEventConverter::toModelList);
    }

    public List<ELDEvent> getActiveEventsFromDBSync(long startTime, long endTime) {
        int driverId = mAccountManager.getCurrentUserId();
        return ELDEventConverter.toModelList(mELDEventDao.getActiveEventsFromStartToEndTimeSync(startTime, endTime, driverId));
    }

    public Single<List<ELDEvent>> getDutyEventsForDay(long startDayTime) {
        return mELDEventDao.getDutyEventsFromStartToEndTimeSync(startDayTime,
                startDayTime + MS_IN_DAY, mAccountManager.getCurrentUserId())
                .onErrorReturn(throwable -> Collections.emptyList())
                .map(ELDEventConverter::toModelList);
    }

    public Single<List<ELDEvent>> getActiveDutyEventsForDay(long startDayTime) {
        return Single.fromCallable(() -> mELDEventDao.getActiveEventsFromStartToEndTimeSync(startDayTime,
                startDayTime + MS_IN_DAY, mAccountManager.getCurrentUserId()))
                .map(ELDEventConverter::toModelList);
    }

    public ELDEvent getLatestActiveDutyEventFromDB(long startDayTime) {
        List<ELDEventEntity> entities = mELDEventDao.getLatestActiveDutyEventSync(startDayTime,
                mAccountManager.getCurrentUserId());
        ELDEvent event = null;
        if (!entities.isEmpty()) {
            event = ELDEventConverter.toModel(entities.get(entities.size() - 1));
        }
        return event;
    }

    public Observable<long[]> updateELDEvents(List<ELDEvent> events) {
        return Observable.fromCallable(() ->
                mELDEventDao.insertAll(ELDEventConverter.toEntityArray(events, ELDEventEntity.SyncType.UPDATE_UNSYNC)))
                .doOnNext(longs -> mLogSheetInteractor.resetLogSheetHeaderSigning(events));
    }

    public Single<Long> postNewELDEvent(ELDEvent event) {
        return Single.fromCallable(() ->
                mELDEventDao.insertEvent(ELDEventConverter.toEntity(event, ELDEventEntity.SyncType.NEW_UNSYNC)))
                .doOnSuccess(aLong -> mLogSheetInteractor.resetLogSheetHeaderSigning(Arrays.asList(event)));
    }

    public Observable<long[]> postNewELDEvents(List<ELDEvent> events) {
        return Observable.fromCallable(() ->
                mELDEventDao.insertAll(ELDEventConverter.toEntityArray(events, ELDEventEntity.SyncType.NEW_UNSYNC)))
                .doOnNext(longs -> mLogSheetInteractor.resetLogSheetHeaderSigning(events));
    }

    public void storeUnidentifiedEvents(List<ELDEvent> events) {
        //TODO: probably additional action with unidentified records is required.
        mELDEventDao.insertAll(ELDEventConverter.toEntityArray(events));
    }

    public Observable<long[]> postNewDutyTypeEvent(DutyType dutyType, String comment, long time) {
        return Observable.fromIterable(getEvents(dutyType, comment))
                .map(event -> {
                    event.setEventTime(time);
                    return event;
                })
                .toList()
                .toObservable()
                .flatMap(this::postNewELDEvents)
                .doOnNext(ids -> {
                    if (ids.length > 0) {
                        mDutyTypeManager.setDutyType(dutyType, true);
                    }
                });
    }

    public Observable<long[]> postNewDutyTypeEvent(DutyType dutyType, String comment) {
        return Single.fromCallable(() -> getEvents(dutyType, comment))
                .flatMapObservable(this::postNewELDEvents)
                .doOnNext(ids -> {
                    if (ids.length > 0) {
                        mDutyTypeManager.setDutyType(dutyType, true);
                    }
                });
    }

    public Single<Boolean> postLogoutEvent() {
        return mServiceApi.logout(getEvent(ELDEvent.LoginLogoutCode.LOGOUT))
                .onErrorReturn(throwable -> {
                    if (throwable instanceof RetrofitException ||
                            throwable instanceof IOException) {
                        return new ResponseMessage(SUCCESS);
                    }
                    return new ResponseMessage(throwable.getMessage());
                })
                .map(responseMessage -> SUCCESS.equals(responseMessage.getMessage()))
                .flatMap(isSuccess -> mBlackBoxInteractor.shutdown(isSuccess).singleOrError());
    }

    public Single<Boolean> postLogoutEvent(int userId) {
        return Single.fromCallable(() -> mUserDao.getUserSync(userId))
                .flatMap(userEntity -> {
                    String token = mTokenManager.getToken(userEntity.getAccountName());
                    return mServiceApi.logout(
                            getEvent(ELDEvent.LoginLogoutCode.LOGOUT),
                            token,
                            String.valueOf(userEntity.getId())
                    )
                            .onErrorReturn(throwable -> {
                                if (throwable instanceof RetrofitException ||
                                        throwable instanceof IOException) {
                                    return new ResponseMessage(SUCCESS);
                                }
                                return new ResponseMessage(throwable.getMessage());
                            });
                })
                .map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    public Single<Boolean> sendReport(long start, long end, int option, String comment) {
        ELDEvent report = getLogSheetEvent(comment);
        return mServiceApi.sendReport(start, end, option, report).map(responseMessage -> responseMessage.getMessage().equals(SUCCESS));
    }

    /**
     * Load all active diagnostic events
     *
     * @return
     */
    public Single<List<ELDEvent>> getDiagnosticEvents() {
        return loadLoggedMalfunctionsByParams(Constants.DIAGNOSTIC_CODES);
    }

    /**
     * Load all active malfunction events
     *
     * @return
     */
    public Single<List<ELDEvent>> getMalfunctionEvents() {
        return loadLoggedMalfunctionsByParams(Constants.MALFUNCTION_CODES);
    }

    private Single<List<ELDEvent>> loadLoggedMalfunctionsByParams(String[] malcodes) {
        return mELDEventDao
                .loadMalfunctions(mAccountManager.getCurrentUserId(),
                        ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                        malcodes,
                        ELDEvent.StatusCode.ACTIVE.getValue())
                .flatMap(this::removeClearedEvents)
                .map(ELDEventConverter::toModelList);
    }

    /**
     * Removes cleared events from list. Input list must be sortered by event time from early to late
     *
     * @param eldEventEntities
     * @return
     */
    private Single<List<ELDEventEntity>> removeClearedEvents(List<ELDEventEntity> eldEventEntities) {
        return Single.fromCallable(() -> {

            Map<String, ELDEventEntity> items = new LinkedHashMap<>();
            for (ELDEventEntity entity : eldEventEntities) {
                if (entity.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode() ||
                        entity.getEventCode() == ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode()) {
                    items.put(entity.getMalCode(), entity);
                } else if (entity.getEventCode() == ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode() ||
                        entity.getEventCode() == ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode()) {
                    items.remove(entity.getMalCode());
                }
            }

            return new ArrayList<>(items.values());
        });
    }

    public Flowable<Boolean> hasMalfunctionEvents() {
        return Flowable
                .combineLatest(
                        getMalfunctionCount(ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED,
                                Constants.MALFUNCTION_CODES),
                        getMalfunctionCount(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED,
                                Constants.MALFUNCTION_CODES),
                        (loggedCount, clearedCount) -> {
                            Timber.d("Count malfunction events: logged %1$d, cleared %2$d", loggedCount, clearedCount);
                            return loggedCount > clearedCount;
                        });
    }

    public Flowable<Boolean> hasDiagnosticEvents() {
        return Flowable
                .combineLatest(
                        getMalfunctionCount(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED,
                                Constants.DIAGNOSTIC_CODES),
                        getMalfunctionCount(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED,
                                Constants.DIAGNOSTIC_CODES),
                        (loggedCount, clearedCount) -> {
                            Timber.d("Count diagnostic events: logged %1$d, cleared %2$d", loggedCount, clearedCount);
                            return loggedCount > clearedCount;
                        });
    }

    /**
     * Returns the latest malfunction event with malfunction code
     *
     * @param malfunction malfunction code
     * @return latest malfunction ELD event
     */
    public Maybe<ELDEvent> getLatestMalfunctionEvent(Malfunction malfunction) {
        return mELDEventDao
                .getLatestEvent(mAccountManager.getCurrentUserId(),
                        ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                        malfunction.getCode(),
                        ELDEvent.StatusCode.ACTIVE.getValue())
                .map(ELDEventConverter::toModel);
    }

    /**
     * Check active events with lat lng codes
     *
     * @return true if active events are exist
     */
    public Single<Boolean> isLocationUpdateEventExists() {
        return mELDEventDao
                .getChangingLocationEventCount(mAccountManager.getCurrentUserId(),
                        new String[]{ELDEvent.LatLngFlag.FLAG_E.getCode(),
                                ELDEvent.LatLngFlag.FLAG_X.getCode()},
                        ELDEvent.StatusCode.ACTIVE.getValue())
                .map(count -> count != 0);
    }


    private Flowable<Integer> getMalfunctionCount(ELDEvent.MalfunctionCode code, String[] codes) {
        return mELDEventDao
                .getMalfunctionEventCount(mAccountManager.getCurrentUserId(),
                        ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                        code.getCode(),
                        codes);
    }

    public Integer getMalfunctionCountSync(int driverId, long startTime, long endTime) {
        return mELDEventDao.getMalfunctionEventCountSync(driverId, startTime, endTime);
    }

    public Integer getDiagnosticCountSync(int driverId, long startTime, long endTime) {
        return mELDEventDao.getDiagnosticEventCountSync(driverId, startTime, endTime);
    }

    private ArrayList<ELDEvent> getEvents(DutyType dutyType, String comment) {
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
                events.add(getEvent(DutyType.PERSONAL_USE, comment));
                break;

            case YARD_MOVES:
                //switch to on-duty if needed
                if (current != DutyType.ON_DUTY) {
                    events.add(getEvent(DutyType.ON_DUTY));
                }
                events.add(getEvent(DutyType.YARD_MOVES, comment));
                break;

            default:
                events.add(getEvent(dutyType, comment));
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

    public ELDEvent getEvent(DutyType dutyType, String comment) {
        return getEvent(dutyType, comment, false);
    }

    public ELDEvent getEvent(DutyType dutyType) {
        return getEvent(dutyType, null, false);
    }


    public ELDEvent getEvent(DutyType dutyType, String comment, boolean isAuto) {
        ELDEvent event = getEvent(getBlackBoxState(dutyType == DutyType.PERSONAL_USE), isAuto);
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setEventType(dutyType.getType());
        event.setEventCode(dutyType.getCode());

        if (comment != null) {
            event.setComment(comment);
        }

        return event;
    }

    /**
     * Makes and fills an event for malfunction
     *
     * @param malfunction     malfunction type
     * @param malfunctionCode malfunction code
     * @param blackBoxModel   data from blackbox
     * @return filled event
     */
    public ELDEvent getEvent(Malfunction malfunction,
                             ELDEvent.MalfunctionCode malfunctionCode,
                             BlackBoxModel blackBoxModel) {

        ELDEvent eldEvent = getEvent(blackBoxModel, true);

        eldEvent.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        eldEvent.setMalCode(malfunction);
        eldEvent.setEventCode(malfunctionCode.getCode());
        eldEvent.setEventType(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue());
        return eldEvent;
    }

    private ELDEvent getEvent(BlackBoxModel blackBoxModel, boolean isAuto) {
        long currentTime = System.currentTimeMillis();
        int driverId = mAccountManager.getCurrentUserId();

        ELDEvent event = new ELDEvent();
        event.setEventTime(currentTime);
        event.setEngineHours(blackBoxModel.getEngineHours());
        event.setOdometer(blackBoxModel.getOdometer());
        event.setLat(blackBoxModel.getLat());
        event.setLng(blackBoxModel.getLon());
        try {
            event.setLatLngFlag(getLatLngFlag(blackBoxModel));
        } catch (InterruptedException | ExecutionException e) {
            event.setLatLngFlag(ELDEvent.LatLngFlag.FLAG_NONE);
        }
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

    public ELDEvent getLogSheetEvent(String comment) {
        BlackBoxModel blackBoxModel = getBlackBoxState(mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE);
        long currentTime = System.currentTimeMillis();
        int driverId = mAccountManager.getCurrentUserId();

        ELDEvent event = new ELDEvent();
        event.setDriverId(driverId);
        event.setVehicleId(mPreferencesManager.getVehicleId());
        event.setEventTime(currentTime);
        event.setEngineHours(blackBoxModel.getEngineHours());
        event.setOdometer(blackBoxModel.getOdometer());
        event.setLat(blackBoxModel.getLat());
        event.setLng(blackBoxModel.getLon());
        event.setTimezone(mTimezone);
        event.setMobileTime(currentTime);
        event.setComment(comment);
        event.setAppInfo(new Gson().toJson(new AppInfo()));

        return event;
    }

    private ELDEvent.LatLngFlag getLatLngFlag(BlackBoxModel blackBoxModel) throws ExecutionException, InterruptedException {
        if (mLatestEldEvent == null) {
            mLatestEldEvent = mELDEventDao.getLatestEvent(
                    mAccountManager.getCurrentUserId(),
                    ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                    Malfunction.POSITIONING_COMPLIANCE.getCode(),
                    ELDEvent.StatusCode.ACTIVE.getValue())
                    .subscribeOn(Schedulers.io())
                    .toFlowable()
                    .toFuture()
                    .get();
        }

        ELDEvent.LatLngFlag latLngFlag;

        if (mLatestEldEvent != null &&
                mLatestEldEvent.getEventType() == ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode()) {
            latLngFlag = ELDEvent.LatLngFlag.FLAG_E;
        } else if (!blackBoxModel.getSensorState(BlackBoxSensorState.GPS)) {
            latLngFlag = ELDEvent.LatLngFlag.FLAG_X;
        } else {
            latLngFlag = ELDEvent.LatLngFlag.FLAG_NONE;
        }
        return latLngFlag;
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
        return Math.round(d * 10) / 10.;
    }

    private int multiplyAndRound(int sec) {
        return Math.round(sec / (float) SEC_IN_HOUR * 10);
    }
}
