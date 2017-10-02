package com.bsmwireless.screens.logs;


import android.util.SparseArray;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.Carrier;
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

    private static final String DRIVERS_NAME_DIVIDER = ", ";
    private static final String DISTANCE_DELIMITER = "/";
    private static final String CARRIER_DELIMITER = ",";

    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private UserInteractor mUserInteractor;
    private DutyTypeManager mDutyTypeManager;
    private AccountManager mAccountManager;
    private Map<Integer, Vehicle> mVehicleIdToNameMap = new HashMap<>();
    private Map<Long, LogSheetHeader> mLogSheetHeadersMap = new HashMap<>();
    private DutyTypeManager.DutyTypeListener mListener = dutyType -> mView.dutyUpdated();

    private CompositeDisposable mDisposables;
    private CompositeDisposable mUpdateDayDataDisposables;
    private Disposable mUpdateEventsDisposable;

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor eventsInteractor, LogSheetInteractor logSheetInteractor,
                         VehiclesInteractor vehiclesInteractor, UserInteractor userInteractor, DutyTypeManager dutyTypeManager,
                         AccountManager accountManager) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mDutyTypeManager = dutyTypeManager;
        mAccountManager = accountManager;
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

                    String driverName = String.format("%1$s %2$s", user.getFirstName(), user.getLastName());
                    String selectedExemptions = user.getRuleException();

                    String allExemptions = null;
                    List<SyncConfiguration> configurations = user.getConfigurations();
                    if (configurations != null) {
                        for (SyncConfiguration configuration : configurations) {
                            if (SyncConfiguration.Type.EXCEPT.getName().equals(configuration.getName())) {
                                allExemptions = configuration.getValue();
                                break;
                            }
                        }
                    }

                    //set carrier name
                    String carriername;
                    List<Carrier> carriers = user.getCarriers();
                    if (carriers != null && !carriers.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Carrier carrier : carriers) {
                            sb.append(carrier.getName()).append(CARRIER_DELIMITER);
                        }
                        carriername = sb.substring(0, sb.length() - CARRIER_DELIMITER.length());
                    } else {
                        carriername = "";
                    }

                    return new UserHeaderInfo(timeZone, driverName, selectedExemptions,
                            allExemptions != null ? allExemptions : "", carriername);
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
                    String coDriversName = getCoDriversName(selectedLogHeader);

                    return new SelectedLogHeaderInfo(vehicleName, vehicleLicense, vehicleTrailers,
                            homeTerminalAddress, homeTerminalName, shippingId, coDriversName);
                });
    }

    private Single<OdometerResult> loadOdometerValue(String mTimeZone) {

        final long startDate = DateUtils.getStartDate(mTimeZone, mView.getSelectedDay().getCalendar());
        return mELDEventsInteractor
                .getActiveDutyEventsForDay(startDate)
                .zipWith(mELDEventsInteractor
                                .getLatestActiveDutyEventFromDBOnce(startDate,
                                        mAccountManager.getCurrentUserId()),
                        this::mapLatestAndCurrentEvents)
                .flatMap(events -> Single.fromCallable(() -> {

                    StringBuilder startValueBuilder = new StringBuilder();
                    StringBuilder endValueBuilder = new StringBuilder();
                    long distance = 0;

                    if (events.isEmpty()) {
                        startValueBuilder.append("0");
                        endValueBuilder.append("0");
                    } else {

                        SparseArray<List<ELDEvent>> eventsByBoxId = new SparseArray<>(3);
                        for (ELDEvent event : events) {

                            List<ELDEvent> eldEvents = eventsByBoxId.get(event.getBoxId());
                            if (eldEvents == null) {
                                eldEvents = new ArrayList<>();
                                eventsByBoxId.put(event.getBoxId(), eldEvents);
                            }
                            eldEvents.add(event);
                        }

                        for (int i = 0; i < eventsByBoxId.size(); i++) {
                            List<ELDEvent> eventsForBox = eventsByBoxId.get(eventsByBoxId.keyAt(i));

                            if (eventsForBox.isEmpty()) {
                                continue;
                            }

                            startValueBuilder.append(eventsForBox.get(0).getOdometer())
                                    .append(DISTANCE_DELIMITER);

                            Integer endOdometer = eventsForBox.get(eventsForBox.size() - 1).getOdometer();
                            endValueBuilder.append(endOdometer == null ? 0 : endOdometer)
                                    .append(DISTANCE_DELIMITER);
                            distance += calculateOdometer(eventsForBox);
                        }

                        truncateBuilder(startValueBuilder, DISTANCE_DELIMITER.length());
                        truncateBuilder(endValueBuilder, DISTANCE_DELIMITER.length());
                    }

                    return new OdometerResult(startValueBuilder.toString(),
                            endValueBuilder.toString(),
                            distance);
                }));
    }


    private List<ELDEvent> mapLatestAndCurrentEvents(List<ELDEvent> current, List<ELDEvent> prevDayEvents) {

        List<ELDEvent> eldEvents = new ArrayList<>(current.size() + (prevDayEvents.isEmpty() ? 0 : 1));
        if (!prevDayEvents.isEmpty()) {
            eldEvents.add(prevDayEvents.get(prevDayEvents.size() - 1));
        }
        eldEvents.addAll(current);
        return eldEvents;
    }

    private void truncateBuilder(StringBuilder stringBuilder, int length) {
        stringBuilder.delete(
                stringBuilder.length() - length,
                stringBuilder.length());
    }

    private long calculateOdometer(List<ELDEvent> events) {

        long distance = 0;

        long startDrivingOdometer = 0;
        boolean isPreviousDriving = false;
        for (ELDEvent event : events) {

            if (DutyType.DRIVING.isSame(event.getEventType(), event.getEventCode())) {
                // If few driving events meets successively takes only first
                if (!isPreviousDriving) {
                    // start driving event, save odometer value
                    startDrivingOdometer = event.getOdometer() != null ? event.getOdometer() : 0;
                    isPreviousDriving = true;
                }
            } else {

                if (isPreviousDriving) {
                    // stop driving event
                    Integer odometer = event.getOdometer();
                    int currentOdometer = odometer != null ? odometer : 0;
                    distance += currentOdometer - startDrivingOdometer;
                    startDrivingOdometer = 0;
                    isPreviousDriving = false;
                }
            }
        }

        return distance;
    }

    private LogHeaderModel mapUserAndHeader(UserHeaderInfo userHeaderInfo,
                                            SelectedLogHeaderInfo selectedLogHeaderInfo,
                                            OdometerResult odometerResult) {

        LogHeaderModel model = new LogHeaderModel();
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

    private String getCoDriversName(LogSheetHeader header) {

        if (header == null) return "";

        String codriverStringIds = header.getCoDriverIds();
        List<Integer> coDriversIds = ListConverter.toIntegerList(codriverStringIds);

        if (coDriversIds.isEmpty()) return "";

        List<UserEntity> names = mUserInteractor.getCoDriversName(coDriversIds);

        if (names.isEmpty()) {
            // no users in the database with these ids, workaround - show co-drivers ids
            return codriverStringIds;
        }

        StringBuilder coDriversNames = new StringBuilder();
        for (UserEntity userEntity : names) {
            coDriversNames
                    .append(userEntity.getFirstName()).append(" ").append(userEntity.getLastName())
                    .append(DRIVERS_NAME_DIVIDER);
        }
        return coDriversNames.substring(0, coDriversNames.length() - DRIVERS_NAME_DIVIDER.length());
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
        final String mVehicleName;
        final String mVehicleLicense;
        final String mTrailers;
        final String mHomeTerminalAddress;
        final String mHomeTerminalName;
        final String mShippingId;
        final String mCoDriversName;

        private SelectedLogHeaderInfo(String vehicleName,
                                      String vehicleLicense,
                                      String trailers,
                                      String homeTerminalAddress,
                                      String homeTerminalName,
                                      String shippingId,
                                      String coDriversName) {
            this.mVehicleName = vehicleName;
            this.mVehicleLicense = vehicleLicense;
            this.mTrailers = trailers;
            this.mHomeTerminalAddress = homeTerminalAddress;
            this.mHomeTerminalName = homeTerminalName;
            this.mShippingId = shippingId;
            this.mCoDriversName = coDriversName;
        }

        public SelectedLogHeaderInfo() {
            this.mVehicleName = "";
            this.mVehicleLicense = "";
            this.mTrailers = "";
            this.mHomeTerminalAddress = "";
            this.mHomeTerminalName = "";
            this.mShippingId = "";
            this.mCoDriversName = "";
        }
    }

    private static final class OdometerResult {
        private final String startValue;
        private final String endValue;
        private final long distance;

        private OdometerResult(String startValue, String endValue, long distance) {
            this.startValue = startValue;
            this.endValue = endValue;
            this.distance = distance;
        }
    }
}
