package com.bsmwireless.screens.carrieredit.fragments.edited;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.DutyUtils;
import com.bsmwireless.common.utils.LogHeaderUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.ELDUpdate;
import com.bsmwireless.models.SyncConfiguration;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.logs.GraphModel;
import com.bsmwireless.screens.logs.LogHeaderModel;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_PU;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_YM;

public final class EditedEventsPresenterImpl implements EditedEventsPresenter {
    private static final int CHANGE_REQUEST = 2;

    private final ELDEventsInteractor mELDEventsInteractor;
    private final LogSheetInteractor mLogSheetInteractor;
    private final UserInteractor mUserInteractor;
    private final VehiclesInteractor mVehiclesInteractor;
    private final ServiceApi mServiceApi;
    private CompositeDisposable mDisposable;
    private final AccountManager mAccountManager;
    private Disposable mUpdateEventsDisposable = Disposables.disposed();
    private Disposable mSendUpdatedDisposable = Disposables.disposed();
    private EditedEventsView mView;
    private volatile String mTimeZone;
    private final LogHeaderUtils mLogHeaderUtils;
    private Map<Integer, Vehicle> mVehicleIdToNameMap = new HashMap<>();
    private Context mContext;

    @Inject
    public EditedEventsPresenterImpl(ELDEventsInteractor eldEventsInteractor,
                                     LogSheetInteractor logSheetInteractor, UserInteractor userInteractor,
                                     VehiclesInteractor vehiclesInteractor, ServiceApi serviceApi, AccountManager accountManager, LogHeaderUtils logHeaderUtils, Context context) {
        mAccountManager = accountManager;
        Timber.v("EditedEventsPresenterImpl: ");
        mELDEventsInteractor = eldEventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mLogHeaderUtils = logHeaderUtils;
        mUserInteractor = userInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mServiceApi = serviceApi;
        mContext = context;
        mDisposable = new CompositeDisposable();
    }

    public void setView(EditedEventsView view) {
        Timber.v("setView: ");
        mView = view;
        mDisposable.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .subscribe(timezone -> {
                    mTimeZone = timezone;
                    long logDay = DateUtils.convertTimeToLogDay(timezone, System.currentTimeMillis());
                    updateDataForDay(logDay);
                    updateCalendarData();
                }));
    }

    private void updateCalendarData() {
        Timber.v("updateCalendarData: ");
        mDisposable.add(mUserInteractor.getTimezone()
                .flatMap(timezone ->
                        mLogSheetInteractor.getLogSheetHeadersForMonth(timezone))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logSheetHeaders -> {
                    mView.setLogSheetHeaders(logSheetHeaders);
                }));
    }

    private void setEventListData(long startDayTime, String timezone) {
        Timber.v("setEventListData: startDayTime = %d, timezone = %s", startDayTime, timezone);
        mDisposable.add(mELDEventsInteractor.getUnidentifiedEvents()
                .subscribeOn(Schedulers.io())
                .switchMap(list -> Observable.fromIterable(list))
                .filter(eldEvent -> eldEvent.getEventTime() >= startDayTime
                        && eldEvent.getEventTime() < startDayTime + MS_IN_DAY)
                .toList()
                .map(eldEvents -> convertToEventLogModels(eldEvents, startDayTime, timezone))
                .doOnSuccess(this::setVehicleNames)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventLogModels -> mView.setEvents(eventLogModels), Timber::e));
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        Timber.v("onCalendarDaySelected: %d", calendarItem.getLogDay());
        updateDataForDay(calendarItem.getLogDay());
    }

    public void updateDataForDay(long logDay) {
        if (!mUpdateEventsDisposable.isDisposed()) {
            return;
        }
        Timber.v("updateDataForDay: ");
        mUpdateEventsDisposable = mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timezone -> {
                    mTimeZone = timezone;
                    long startDayTime = DateUtils.getStartDayTimeInMs(logDay, timezone);
                    setGraphData(startDayTime, timezone);
                    setEventListData(startDayTime, timezone);
                    setLogHeaderData(logDay);
                    mUpdateEventsDisposable.dispose();
                }, Timber::e);
    }

    @Override
    public void approveEdits(List<EventLogModel> events, long logDay) {
        if (events != null && !events.isEmpty()) {
            Timber.v("approveEdits: ");
            sendUpdatedEvents(events, logDay, true);
        }
    }

    @Override
    public void disapproveEdits(List<EventLogModel> events, long logDay) {
        if (events != null && !events.isEmpty()) {
            Timber.v("disapproveEdits: ");
            sendUpdatedEvents(events, logDay, false);
        }
    }

    @Override
    public void markCalendarItems(List<CalendarItem> list) {
        Timber.v("markCalendarItems: ");
        mDisposable.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .doOnNext(timezone -> mTimeZone = timezone)
                .toObservable()
                .switchMap(s -> mELDEventsInteractor.getUnidentifiedEvents())
                .map(eldEvents -> {
                    for (int i = 0; i < list.size(); ++i) {
                        CalendarItem calendarItem = list.get(i);
                        long startTime = DateUtils.getStartDayTimeInMs(calendarItem.getLogDay(), mTimeZone);
                        for (ELDEvent eldEvent : eldEvents) {
                            if (eldEvent.getEventTime() > startTime &&
                                    eldEvent.getEventTime() <= startTime + MS_IN_DAY) {
                                calendarItem.setExternalColor(ContextCompat.getColor(mContext, R.color.coral));
                                list.set(i, calendarItem);
                                break;
                            }
                        }
                    }
                    return list;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list1 -> mView.updateCalendarItems(list1), Timber::e));
    }

    @Override
    public void destroy() {
        Timber.v("destroy: ");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (!mSendUpdatedDisposable.isDisposed()) {
            mSendUpdatedDisposable.dispose();
        }
        if (!mUpdateEventsDisposable.isDisposed()) {
            mUpdateEventsDisposable.dispose();
        }
    }

    private void sendUpdatedEvents(List<EventLogModel> events, long logDay, boolean isAccepted) {
        if (!mSendUpdatedDisposable.isDisposed()) {
            return;
        }
        Timber.v("sendUpdatedEvents: ");
        final List<ELDEvent> cachedEvents = new ArrayList<>();
        mSendUpdatedDisposable = Observable.fromIterable(events)
                .subscribeOn(Schedulers.io())
                .map(logModel -> logModel.getEvent().setStatus(isAccepted ?
                        ELDEvent.StatusCode.ACTIVE.getValue() : ELDEvent.StatusCode.INACTIVE_CHANGE_REJECTED.getValue()))
                .toList()
                .doOnSuccess(eldEvents -> cachedEvents.addAll(eldEvents))
                .map(eldEvents -> {
                    List<ELDUpdate> updateEvents = new ArrayList<>();
                    for (ELDEvent eldEvent : eldEvents) {
                        ELDUpdate eldUpdate = new ELDUpdate()
                                .setType(CHANGE_REQUEST)
                                .setId(eldEvent.getId())
                                .setMobileTime(eldEvent.getMobileTime())
                                .setTimezone(eldEvent.getTimezone())
                                .setAccept(isAccepted);
                        updateEvents.add(eldUpdate);
                    }
                    return updateEvents;
                })
                .flatMap(eldUpdate -> mServiceApi.updateRecords(eldUpdate))
                .doOnSuccess(resp -> updateDbEldEvents(cachedEvents, logDay))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseMessage -> {
                    mSendUpdatedDisposable.dispose();
                }, t -> {
                    Timber.e(t);
                    mView.showConnectionError();
                    mSendUpdatedDisposable.dispose();
                });
    }

    private void updateDbEldEvents(List<ELDEvent> events, long logDay) {
        Timber.v("updateDbEldEvents: ");
        mDisposable.add(mELDEventsInteractor.updateELDEvents(events)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> updateDataForDay(logDay), Timber::e));
    }

    private void setLogHeaderData(long logDay) {
        mDisposable.add(mUserInteractor.getTimezoneOnce()
                .flatMap(timezone -> Single
                        .zip(loadUserHeaderInfo(timezone),
                                loadLogHeaderInfo(logDay),
                                loadOdometerValue(timezone),
                                this::mapUserAndHeader))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logHeaderModel -> mView.setLogHeader(logHeaderModel)));
    }

    private Single<UserHeaderInfo> loadUserHeaderInfo(String timeZone) {

        return mUserInteractor.getFullUser()
                .map(user -> {
                    if (user == null) return new UserHeaderInfo();

                    String driverName = mLogHeaderUtils.makeDriverName(user);
                    String selectedExemptions = user.getRuleException();
                    String allExemptions = mLogHeaderUtils.getAllExemptions(user,
                            SyncConfiguration.Type.EXCEPT);
                    String carrierName = mLogHeaderUtils.makeCarrierName(user);

                    return new UserHeaderInfo(timeZone, driverName, selectedExemptions,
                            allExemptions, carrierName);
                });
    }

    private Single<SelectedLogHeaderInfo> loadLogHeaderInfo(long logDay) {

        return mLogSheetInteractor.getLogSheet(logDay)
                .map(selectedLogHeader -> {
                    if (selectedLogHeader == null) return new SelectedLogHeaderInfo();

                    int vehicleId = selectedLogHeader.getVehicleId();
                    Vehicle vehicle;
                    if (mVehicleIdToNameMap.containsKey(vehicleId)) {
                        vehicle = mVehicleIdToNameMap.get(vehicleId);
                    } else {
                        vehicle = mVehiclesInteractor.getVehicle(vehicleId);
                    }
                    String vehicleName;
                    String vehicleLicense;
                    if (vehicle != null) {
                        vehicleName = vehicle.getName();
                        vehicleLicense = vehicle.getLicense();
                    } else {
                        vehicleName = "";
                        vehicleLicense = "";
                    }

                    String vehicleTrailers = selectedLogHeader.getTrailerIds();

                    String homeTerminalAddress;
                    String homeTerminalName;
                    if (selectedLogHeader.getHomeTerminal() != null) {
                        homeTerminalAddress = selectedLogHeader.getHomeTerminal().getAddress();
                        homeTerminalName = selectedLogHeader.getHomeTerminal().getName();
                    } else {
                        homeTerminalAddress = "";
                        homeTerminalName = "";
                    }

                    String shippingId = selectedLogHeader.getShippingId();
                    String coDriversName = mLogHeaderUtils.getCoDriversName(selectedLogHeader);

                    return new SelectedLogHeaderInfo(logDay, vehicleName, vehicleLicense, vehicleTrailers,
                            homeTerminalAddress, homeTerminalName, shippingId, coDriversName);
                });
    }

    private Single<LogHeaderUtils.OdometerResult> loadOdometerValue(String mTimeZone) {

        final long startDate = DateUtils.getStartDate(mTimeZone, mView.getSelectedDay().getCalendar());

        // Day may stared from event in previous day, that's why loads event for previous day
        return mELDEventsInteractor
                .getActiveDutyEventsForDay(startDate)
                .zipWith(mELDEventsInteractor
                                .getLatestActiveDutyEventFromDBOnce(startDate,
                                        mAccountManager.getCurrentUserId()),
                        this::mapLatestAndCurrentEvents)
                .flatMap(events -> Single.fromCallable(() ->
                        mLogHeaderUtils.calculateOdometersValue(events)));
    }

    private List<ELDEvent> mapLatestAndCurrentEvents(List<ELDEvent> current, List<ELDEvent> prevDayEvents) {

        List<ELDEvent> eldEvents = new ArrayList<>(current.size() + (prevDayEvents.isEmpty() ? 0 : 1));
        if (!prevDayEvents.isEmpty()) {
            eldEvents.add(prevDayEvents.get(prevDayEvents.size() - 1));
        }
        eldEvents.addAll(current);
        return eldEvents;
    }

    private LogHeaderModel mapUserAndHeader(UserHeaderInfo userHeaderInfo,
                                            SelectedLogHeaderInfo selectedLogHeaderInfo,
                                            LogHeaderUtils.OdometerResult odometerResult) {

        LogHeaderModel model = new LogHeaderModel();
        model.setLogDay(selectedLogHeaderInfo.logDay);
        model.setTimezone(userHeaderInfo.mTimezone);
        model.setDriverName(userHeaderInfo.mDriverName);
        model.setSelectedExemptions(userHeaderInfo.mSelectedExemption);
        model.setAllExemptions(userHeaderInfo.mAllExemptions);
        model.setCarrierName(userHeaderInfo.mCarrierName);

        model.setVehicleName(selectedLogHeaderInfo.mVehicleName);
        model.setVehicleLicense(selectedLogHeaderInfo.mVehicleLicense);
        model.setTrailers(selectedLogHeaderInfo.mTrailers);
        model.setHomeTerminalAddress(selectedLogHeaderInfo.mHomeTerminalAddress);
        model.setHomeTerminalName(selectedLogHeaderInfo.mHomeTerminalName);
        model.setShippingId(selectedLogHeaderInfo.mShippingId);
        model.setCoDriversName(selectedLogHeaderInfo.mCoDriversName);

        model.setStartOdometer(odometerResult.startValue);
        model.setEndOdometer(odometerResult.endValue);
        model.setDistanceDriven(String.valueOf(odometerResult.distance));
        return model;
    }

    private void setGraphData(long startDayTime, String timezone) {
        Timber.v("setGraphData: ");
        GraphModel graphModel = new GraphModel();
        graphModel.setStartDayTime(startDayTime);
        mDisposable.add(mELDEventsInteractor.getActiveDutyEventsForDay(startDayTime)
                .subscribeOn(Schedulers.io())
                .map(eldEvents -> convertToEventLogModels(eldEvents, startDayTime, timezone))
                .doOnSuccess(eventLogModels -> graphModel.setEventLogModels(eventLogModels))
                .doOnSuccess(eventLogModels -> graphModel.setPrevDayEvent(
                        mELDEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eldEvents -> mView.updateGraph(graphModel), Timber::e));
    }

    private List<EventLogModel> convertToEventLogModels(List<ELDEvent> events, long startDayTime, String timezone) {
        List<EventLogModel> logs = new ArrayList<>();

        long endDayTime = Math.min(DateUtils.currentTimeMillis(), startDayTime + MS_IN_DAY);
        int lastActiveIndex = -1;

        if (!events.isEmpty()) {
            //convert to logs model
            for (int i = 0; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                EventLogModel log = new EventLogModel(event, timezone);
                if (event.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()
                        && event.getEventCode() == DutyType.CLEAR.getCode()) {
                    log.setType(DutyType.CLEAR);
                    //get code of indication ON event for indication OFF event
                    for (int j = i - 1; j >= 0; j--) {
                        ELDEvent dutyEvent = events.get(j);

                        if (dutyEvent.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
                            if (dutyEvent.getEventCode() == DutyType.PERSONAL_USE.getCode()) {
                                log.setType(CLEAR_PU);
                                break;
                            } else if (dutyEvent.getEventCode() == DutyType.YARD_MOVES.getCode()) {
                                log.setType(CLEAR_YM);
                                break;
                            }
                        }
                    }
                } else {
                    log.setType(DutyUtils.getTypeByCode(log.getEventType(), log.getEventCode()));
                }
                logs.add(log);
                if (logs.get(0).getEventTime() < startDayTime) {
                    logs.get(0).setEventTime(startDayTime);
                }

                if (logs.get(i).isActive()) {
                    if (lastActiveIndex >= 0) {
                        logs.get(lastActiveIndex).setDuration(logs.get(i).getEventTime() - logs.get(lastActiveIndex).getEventTime());
                    }
                    lastActiveIndex = i;
                }

            }

            if (lastActiveIndex >= 0) {
                logs.get(lastActiveIndex).setDuration(endDayTime - logs.get(lastActiveIndex).getEventTime());
            }
        }
        return logs;
    }

    private List<EventLogModel> setVehicleNames(List<EventLogModel> eventLogModels) {
        HashSet<Integer> vehicleIds = new HashSet<>();
        for (EventLogModel log : eventLogModels) {
            int vehicleId = log.getEvent().getVehicleId();
            if (mVehicleIdToNameMap.containsKey(vehicleId)) {
                vehicleIds.add(vehicleId);
            }
        }
        if (!vehicleIds.isEmpty()) {
            List<Vehicle> vehicles = mVehiclesInteractor.getVehiclesByIds(new ArrayList<>(vehicleIds));
            for (Vehicle vehicle : vehicles) {
                mVehicleIdToNameMap.put(vehicle.getId(), vehicle);
            }
        }
        for (EventLogModel log : eventLogModels) {
            if (mVehicleIdToNameMap.containsKey(log.getEvent().getVehicleId())) {
                log.setVehicleName(mVehicleIdToNameMap.get(log.getEvent().getVehicleId()).getName());
            }
        }
        return eventLogModels;
    }

    private static final class UserHeaderInfo {
        final String mTimezone;
        final String mDriverName;
        final String mSelectedExemption;
        final String mAllExemptions;
        final String mCarrierName;

        private UserHeaderInfo(String timezone,
                               String driverName,
                               String selectedExemption,
                               String allExemptions,
                               String carrierName) {
            this.mTimezone = timezone;
            this.mDriverName = driverName;
            this.mSelectedExemption = selectedExemption;
            this.mAllExemptions = allExemptions;
            this.mCarrierName = carrierName;
        }

        public UserHeaderInfo() {
            mTimezone = "";
            mDriverName = "";
            mSelectedExemption = "";
            mAllExemptions = "";
            mCarrierName = "";
        }
    }

    private static final class SelectedLogHeaderInfo {
        final long logDay;
        final String mVehicleName;
        final String mVehicleLicense;
        final String mTrailers;
        final String mHomeTerminalAddress;
        final String mHomeTerminalName;
        final String mShippingId;
        final String mCoDriversName;

        private SelectedLogHeaderInfo(long logDay,
                                      String vehicleName,
                                      String vehicleLicense,
                                      String trailers,
                                      String homeTerminalAddress,
                                      String homeTerminalName,
                                      String shippingId,
                                      String coDriversName) {
            this.logDay = logDay;
            this.mVehicleName = vehicleName;
            this.mVehicleLicense = vehicleLicense;
            this.mTrailers = trailers;
            this.mHomeTerminalAddress = homeTerminalAddress;
            this.mHomeTerminalName = homeTerminalName;
            this.mShippingId = shippingId;
            this.mCoDriversName = coDriversName;
        }

        public SelectedLogHeaderInfo() {
            this.logDay = 0;
            this.mVehicleName = "";
            this.mVehicleLicense = "";
            this.mTrailers = "";
            this.mHomeTerminalAddress = "";
            this.mHomeTerminalName = "";
            this.mShippingId = "";
            this.mCoDriversName = "";
        }
    }
}
