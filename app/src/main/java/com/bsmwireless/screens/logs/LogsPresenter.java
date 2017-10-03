package com.bsmwireless.screens.logs;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.LogHeaderUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.SyncConfiguration;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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


@ActivityScope
public final class LogsPresenter implements AccountManager.AccountListener {


    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private UserInteractor mUserInteractor;
    private DutyTypeManager mDutyTypeManager;
    private AccountManager mAccountManager;
    private LogHeaderUtils mLogHeaderUtils;
    private Map<Integer, Vehicle> mVehicleIdToNameMap = new HashMap<>();
    private Map<Long, LogSheetHeader> mLogSheetHeadersMap = new HashMap<>();
    private DutyTypeManager.DutyTypeListener mListener = dutyType -> mView.dutyUpdated();

    private CompositeDisposable mDisposables;
    private CompositeDisposable mUpdateDayDataDisposables;
    private Disposable mUpdateEventsDisposable;

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor eventsInteractor,
                         LogSheetInteractor logSheetInteractor,
                         VehiclesInteractor vehiclesInteractor,
                         UserInteractor userInteractor,
                         DutyTypeManager dutyTypeManager,
                         AccountManager accountManager, LogHeaderUtils logHeaderUtils) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mDutyTypeManager = dutyTypeManager;
        mAccountManager = accountManager;
        mLogHeaderUtils = logHeaderUtils;
        Timber.d("CREATED");
        mDutyTypeManager.addListener(mListener);
    }

    public void onViewCreated() {
        mDisposables = new CompositeDisposable();
        mUpdateDayDataDisposables = new CompositeDisposable();
        mUpdateEventsDisposable = Disposables.disposed();
        mDisposables.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .subscribe(timezone -> {
                    long logDay = DateUtils.convertTimeToLogDay(timezone, System.currentTimeMillis());
                    updateDataForDay(logDay);
                }));
        updateCalendarData();
        mAccountManager.addListener(this);
    }

    private void updateCalendarData() {
        mUpdateEventsDisposable.dispose();
        mUpdateEventsDisposable = mUserInteractor.getTimezone()
                .flatMap(timezone ->
                        mLogSheetInteractor.getLogSheetHeadersForMonth(timezone))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logSheetHeaders -> {
                    mLogSheetHeadersMap = new HashMap<>(logSheetHeaders.size());
                    for (LogSheetHeader logSheetHeader : logSheetHeaders) {
                        mLogSheetHeadersMap.put(logSheetHeader.getLogDay(), logSheetHeader);
                    }
                    mView.setLogSheetHeaders(logSheetHeaders);
                });
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        updateDataForDay(calendarItem.getLogDay());
    }

    public void updateDataForDay(long logDay) {
        mUpdateDayDataDisposables.clear();
        mUpdateDayDataDisposables.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .subscribe(timezone -> {
                    long startDayTime = DateUtils.getStartDayTimeInMs(logDay, timezone);
                    setGraphData(startDayTime, timezone);
                    setEventListData(startDayTime, timezone);
                    setLogHeaderData(logDay);
                }));
    }

    private void setGraphData(long startDayTime, String timezone) {
        GraphModel graphModel = new GraphModel();
        graphModel.setStartDayTime(startDayTime);
        mUpdateDayDataDisposables.add(mELDEventsInteractor.getActiveDutyEventsForDay(startDayTime)
                .subscribeOn(Schedulers.io())
                .map(eldEvents -> convertToEventLogModels(eldEvents, startDayTime, timezone))
                .doOnSuccess(graphModel::setEventLogModels)
                .doOnSuccess(eventLogModels -> graphModel.setPrevDayEvent(
                        mELDEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eldEvents -> mView.updateGraph(graphModel)));
    }

    private void setEventListData(long startDayTime, String timezone) {
        mUpdateDayDataDisposables.add(mELDEventsInteractor.getDutyEventsForDay(startDayTime)
                .subscribeOn(Schedulers.io())
                .map(eldEvents -> convertToEventLogModels(eldEvents, startDayTime, timezone))
                .doOnSuccess(this::setVehicleNames)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventLogModels -> mView.setEventLogs(eventLogModels)));
    }

    private void setLogHeaderData(long logDay) {

        Disposable disposable = mUserInteractor.getTimezoneOnce()
                .flatMap(timezone -> Single
                        .zip(loadUserHeaderInfo(timezone),
                                loadLogHeaderInfo(logDay),
                                loadOdometerValue(timezone),
                                this::mapUserAndHeader))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logHeaderModel -> mView.setLogHeader(logHeaderModel));
        mUpdateDayDataDisposables.add(disposable);
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

    public void onSignLogsheetButtonClicked(CalendarItem calendarItem) {
        mUpdateEventsDisposable.dispose();
        mUpdateEventsDisposable = mLogSheetInteractor.signLogSheet(calendarItem.getLogDay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        logSheetHeader -> {
                            mLogSheetHeadersMap.put(logSheetHeader.getLogDay(), logSheetHeader);
                            mView.setLogSheetHeaders(new ArrayList<>(mLogSheetHeadersMap.values()));
                        },
                        Timber::e
                );
    }

    public void onEditEventClicked(EventLogModel event) {
        mView.goToEditEventScreen(event);
    }

    public void onRemovedEventClicked(EventLogModel event) {
        mUpdateEventsDisposable.dispose();
        mUpdateEventsDisposable = mELDEventsInteractor
                .getLatestActiveDutyEventFromDBOnce(event.getEventTime(), mAccountManager
                        .getCurrentUserId())
                .subscribeOn(Schedulers.io())
                .map(events -> events.get(events.size() - 1))
                .map(latestEvent -> {
                    ELDEvent updatedEvent = event.getEvent();
                    ELDEvent originEvent = updatedEvent.clone();
                    updatedEvent.setEventCode(latestEvent.getEventCode());
                    updatedEvent.setEventType(latestEvent.getEventType());
                    originEvent.setStatus(ELDEvent.StatusCode.INACTIVE_CHANGED.getValue());
                    originEvent.setId(null);
                    return Arrays.asList(updatedEvent, originEvent);
                })
                .flatMapObservable(events -> mELDEventsInteractor.updateELDEvents(events))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(longs -> {
                            mView.eventRemoved();
                            updateCalendarData();
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_REMOVE_EVENT);
                        });
    }

    public void onReassignEventClicked(EventLogModel event) {
        mView.showReassignDialog(event.getEvent());
    }

    public void onAddEventClicked(CalendarItem day) {
        mView.goToAddEventScreen(day);
    }

    public void onEditLogHeaderClicked(LogHeaderModel logHeaderModel) {
        mView.goToEditLogHeaderScreen(logHeaderModel);
    }

    public void onEventAdded(List<ELDEvent> newEvents) {
        mUpdateEventsDisposable.dispose();
        mUpdateEventsDisposable = mELDEventsInteractor.postNewELDEvents(newEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mView.eventAdded();
                            updateCalendarData();
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_ADD_EVENT);
                        });
    }

    public void onEventChanged(List<ELDEvent> updatedEvents) {
        mUpdateEventsDisposable.dispose();
        mUpdateEventsDisposable = mELDEventsInteractor.updateELDEvents(updatedEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mView.eventUpdated();
                            updateCalendarData();
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_UPDATE_EVENT);
                        });
    }

    public void onLogHeaderChanged(LogHeaderModel logHeaderModel) {
        mUpdateEventsDisposable.dispose();
        mUpdateEventsDisposable = mLogSheetInteractor.getLogSheet(logHeaderModel.getLogDay())
                .map(logSheetHeader -> updateLogSheetHeader(logSheetHeader, logHeaderModel))
                .flatMap(logSheetHeader -> mLogSheetInteractor.updateLogSheetHeader(logSheetHeader))
                .flatMapCompletable(aLong -> mUserInteractor.updateUserRuleException(logHeaderModel.getSelectedExemptions()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mView.setLogHeader(logHeaderModel));
    }

    private LogSheetHeader updateLogSheetHeader(LogSheetHeader logSheetHeader, LogHeaderModel logHeaderModel) {
        HomeTerminal homeTerminal = logSheetHeader.getHomeTerminal();
        if (homeTerminal == null) {
            homeTerminal = new HomeTerminal();
            homeTerminal.setTimezone(logHeaderModel.getTimezone());
        }

        homeTerminal.setName(logHeaderModel.getHomeTerminalName());
        homeTerminal.setAddress(logHeaderModel.getHomeTerminalAddress());
        logSheetHeader.setHomeTerminal(homeTerminal);
        logSheetHeader.setTrailerIds(logHeaderModel.getTrailers());
        logSheetHeader.setShippingId(logHeaderModel.getShippingId());

        //TODO: remove after clean up incorrect data for logsheetheader
        if (logSheetHeader.getBoxId() == null || logSheetHeader.getBoxId() < 0) {
            logSheetHeader.setBoxId(mVehiclesInteractor.getBoxId());
        }
        if (logSheetHeader.getVehicleId() == null || logSheetHeader.getVehicleId() < 0) {
            logSheetHeader.setVehicleId(mVehiclesInteractor.getVehicleId());
        }
        if (logSheetHeader.getDriverId() == null || logSheetHeader.getDriverId() < 0) {
            logSheetHeader.setDriverId(mUserInteractor.getUserId());
        }
        return logSheetHeader;
    }

    public void onDestroy() {
        mAccountManager.removeListener(this);
        mDutyTypeManager.removeListener(mListener);
        mUpdateDayDataDisposables.dispose();
        mDisposables.dispose();
        mUpdateEventsDisposable.dispose();
        Timber.d("DESTROYED");
    }

    private List<EventLogModel> convertToEventLogModels(List<ELDEvent> events, long startDayTime, String timezone) {
        List<EventLogModel> logs = new ArrayList<>();

        long endDayTime = Math.min(System.currentTimeMillis(), startDayTime + MS_IN_DAY);
        int lastActiveIndex = -1;

        if (!events.isEmpty()) {
            //convert to logs model
            for (int i = 0; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                EventLogModel log = new EventLogModel(event, timezone);
                if (event.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()
                        && event.getEventCode() == DutyType.CLEAR.getCode()) {
                    log.setDutyType(DutyType.CLEAR);
                    //get code of indication ON event for indication OFF event
                    for (int j = i - 1; j >= 0; j--) {
                        ELDEvent dutyEvent = events.get(j);

                        if (dutyEvent.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
                            if (dutyEvent.getEventCode() == DutyType.PERSONAL_USE.getCode()) {
                                log.setDutyType(CLEAR_PU);
                                break;
                            } else if (dutyEvent.getEventCode() == DutyType.YARD_MOVES.getCode()) {
                                log.setDutyType(CLEAR_YM);
                                break;
                            }
                        }
                    }
                } else {
                    log.setDutyType(DutyType.getTypeByCode(log.getEventType(), log.getEventCode()));
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

    /**
     * Creates events list.
     * Takes events for current(selected) day and events for previous day.
     * Result will be all events from current day plus a last event for previous day
     *
     * @param current       events for current day
     * @param prevDayEvents events for previous day
     * @return events for current day with event for previous day
     */
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

    @Override
    public void onUserChanged() {
        mUpdateEventsDisposable.dispose();
        CalendarItem calendarItem = mView.getSelectedDay();
        updateDataForDay(calendarItem.getLogDay());
        updateCalendarData();
    }

    @Override
    public void onDriverChanged() {
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
