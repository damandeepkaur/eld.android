package com.bsmwireless.domain.interactors;

import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.common.Constants;
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
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.ResponseMessage;
import com.bsmwireless.widgets.alerts.DutyType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.SUCCESS;
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

    @Inject
    public ELDEventsInteractor(ServiceApi serviceApi, PreferencesManager preferencesManager,
                               AppDatabase appDatabase, UserInteractor userInteractor,
                               BlackBoxInteractor blackBoxInteractor, DutyTypeManager dutyTypeManager,
                               AccountManager accountManager, TokenManager tokenManager) {
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

        mUserInteractor.getTimezone().subscribe(timezone -> mTimezone = timezone);
    }

    public Flowable<List<ELDEvent>> getDutyEventsFromDB(long startTime, long endTime) {
        int driverId = mAccountManager.getCurrentUserId();
        return mELDEventDao.getDutyEventsFromStartToEndTime(startTime, endTime, driverId)
                .map(ELDEventConverter::toModelList);
    }

    public List<ELDEvent> getLatestActiveDutyEventFromDBSync(long latestTime, int userId) {
        return ELDEventConverter.toModelList(mELDEventDao.getLatestActiveDutyEventSync(latestTime, userId));
    }

    public List<ELDEvent> getActiveEventsFromDBSync(long startTime, long endTime) {
        int driverId = mAccountManager.getCurrentUserId();
        return ELDEventConverter.toModelList(mELDEventDao.getActiveEventsFromStartToEndTimeSync(startTime, endTime, driverId));
    }

    public Observable<long[]> updateELDEvents(List<ELDEvent> events) {
        return Observable.fromCallable(() ->
                mELDEventDao.insertAll(ELDEventConverter.toEntityArray(events, ELDEventEntity.SyncType.UPDATE_UNSYNC)));
    }

    public Single<Long> postNewELDEvent(ELDEvent event) {
        return Single.fromCallable(() ->
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

    public Observable<long[]> postNewDutyTypeEvent(DutyType dutyType, String comment) {
        return postNewELDEvents(getEvents(dutyType, comment))
                .doOnNext(isSuccess -> mDutyTypeManager.setDutyType(dutyType, true));
    }

    public Observable<Boolean> postLogoutEvent() {
        return mServiceApi.logout(getEvent(ELDEvent.LoginLogoutCode.LOGOUT))
                .onErrorReturn(throwable -> {
                    if (throwable instanceof RetrofitException ||
                            throwable instanceof IOException) {
                        return new ResponseMessage(SUCCESS);
                    }
                    return new ResponseMessage(throwable.getMessage());
                })
                .map(responseMessage -> SUCCESS.equals(responseMessage.getMessage()))
                .switchMap(isSuccess -> mBlackBoxInteractor.shutdown(isSuccess));
    }

    public Observable<Boolean> postLogoutEvent(int userId) {
        return Observable.fromCallable(() -> mUserDao.getUserSync(userId))
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

    /**
     * Load all active diagnostic events
     *
     * @return
     */
    public Flowable<List<ELDEvent>> getDiagnosticEvents() {
        return Flowable.just(Collections.emptyList());
    }

    /**
     * Load all active malfunction events
     *
     * @return
     */
    public Flowable<List<ELDEvent>> getMalfunctionEvents() {
        return Flowable.just(Collections.emptyList());
    }

    public Flowable<Boolean> hasMalfunctionEvents() {
        return Flowable
                .combineLatest(
                        getMalfunctionCount(ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED,
                                Constants.MALFUNCTION_CODES),
                        getMalfunctionCount(ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED,
                                Constants.MALFUNCTION_CODES),
                        (loggedCount, clearedCount) -> loggedCount.compareTo(clearedCount) != 0);
    }

    public Flowable<Boolean> hasDiagnosticEvents() {
        return Flowable
                .combineLatest(
                        getMalfunctionCount(ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED,
                                Constants.DIAGNOSTIC_CODES),
                        getMalfunctionCount(ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED,
                                Constants.DIAGNOSTIC_CODES),
                        (loggedCount, clearedCount) -> loggedCount.compareTo(clearedCount) != 0);
    }

    private Flowable<Integer> getMalfunctionCount(ELDEvent.MalfunctionCode code, String[] codes) {
        return mELDEventDao.getMalfunctionEventCount(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
                code.getCode(), codes);
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

    private ELDEvent getEvent(BlackBoxModel blackBoxModel, boolean isAuto) {
        long currentTime = System.currentTimeMillis();
        int driverId = mAccountManager.getCurrentUserId();

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
