package com.bsmwireless.screens.logs;


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
import com.bsmwireless.models.User;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_PU;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_YM;


@ActivityScope
public final class LogsPresenter implements AccountManager.AccountListener {

    private static final String DRIVERS_NAME_DIVIDER = ", ";

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
        mDisposables.clear();
        mDisposables.add(mUserInteractor.getTimezone()
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
                }));
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
                .doOnSuccess(eventLogModels -> graphModel.setEventLogModels(eventLogModels))
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
        LogHeaderModel model = new LogHeaderModel();
        mUpdateDayDataDisposables.add(mUserInteractor.getFullUser()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(user -> updateLogHeaderModelByUser(model, user))
                .flatMap(user -> mLogSheetInteractor.getLogSheet(logDay))
                .doOnSuccess(logSheetHeader -> updateLogHeaderModelByLogSheet(model, logSheetHeader))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventLogModels -> mView.setLogHeader(model)));
    }

    private void updateLogHeaderModelByUser(LogHeaderModel logHeaderModel, User user) {
        logHeaderModel.setTimezone(user.getTimezone());
        logHeaderModel.setDriverName(user.getFirstName() + " " + user.getLastName());
        logHeaderModel.setSelectedExemptions(user.getRuleException() != null ? user.getRuleException() : "");

        List<SyncConfiguration> configurations = user.getConfigurations();
        if (configurations != null) {
            for (SyncConfiguration configuration : configurations) {
                if (SyncConfiguration.Type.EXCEPT.getName().equals(configuration.getName())) {
                    logHeaderModel.setAllExemptions(configuration.getValue());
                    break;
                }
            }
        } else {
            logHeaderModel.setAllExemptions("");
        }

        //set carrier name
        List<Carrier> carriers = user.getCarriers();
        if (carriers != null && !carriers.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Carrier carrier : carriers) {
                sb.append(carrier.getName());
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            logHeaderModel.setCarrierName(sb.toString());
        }
        logHeaderModel.setSelectedExemptions(user.getRuleException());
    }

    private void updateLogHeaderModelByLogSheet(LogHeaderModel logHeaderModel, LogSheetHeader logSheetHeader) {
        logHeaderModel.setLogDay(logSheetHeader.getLogDay());
        int vehicleId = logSheetHeader.getVehicleId();
        Vehicle vehicle;
        if (mVehicleIdToNameMap.containsKey(vehicleId)) {
            vehicle = mVehicleIdToNameMap.get(vehicleId);
        } else {
            vehicle = mVehiclesInteractor.getVehicle(vehicleId);
        }
        if (vehicle != null) {
            logHeaderModel.setVehicleName(vehicle.getName());
            logHeaderModel.setVehicleLicense(vehicle.getLicense());
        }
        logHeaderModel.setTrailers(logSheetHeader.getTrailerIds());

        if (logSheetHeader.getHomeTerminal() != null) {
            logHeaderModel.setHomeTerminalAddress(logSheetHeader.getHomeTerminal().getAddress());
            logHeaderModel.setHomeTerminalName(logSheetHeader.getHomeTerminal().getName());
        }

        logHeaderModel.setShippingId(logSheetHeader.getShippingId());

        String codriverIds = logSheetHeader.getCoDriverIds();

        //TODO: get codriver names by ids
        logHeaderModel.setCoDriversName(codriverIds);

        //TODO: init by data from black box
        logHeaderModel.setStartOdometer("0");
        logHeaderModel.setEndOdometer("0");
        logHeaderModel.setDistanceDriven("-");
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
        mLogSheetInteractor.signLogSheet(calendarItem.getLogDay())
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
        mDisposables.clear();
        mDisposables.add(mELDEventsInteractor.postNewELDEvents(newEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mView.eventAdded();
                            updateCalendarData();
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_ADD_EVENT);
                        }));
    }

    public void onEventChanged(List<ELDEvent> updatedEvents) {
        mDisposables.clear();
        mDisposables.add(mELDEventsInteractor.updateELDEvents(updatedEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mView.eventUpdated();
                            updateCalendarData();
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_UPDATE_EVENT);
                        }));
    }

    public void onLogHeaderChanged(LogHeaderModel logHeaderModel) {
        mDisposables.clear();
        mDisposables.add(mLogSheetInteractor.getLogSheet(logHeaderModel.getLogDay())
                .map(logSheetHeader -> updateLogSheetHeader(logSheetHeader, logHeaderModel))
                .flatMap(logSheetHeader -> mLogSheetInteractor.updateLogSheetHeader(logSheetHeader))
                .flatMapCompletable(aLong -> mUserInteractor.updateUserRuleException(logHeaderModel.getSelectedExemptions()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mView.setLogHeader(logHeaderModel)));
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

    private Single<UserHeaderInfo> loadUserHeaderInfo() {

        return Single.fromCallable(() -> {

            if (mUser == null) return new UserHeaderInfo();

            String driverName = String.format("%1$s %2$s", mUser.getFirstName(), mUser.getLastName());
            String selectedExemptions = mUser.getRuleException();

            String allExemptions = null;
            List<SyncConfiguration> configurations = mUser.getConfigurations();
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
            List<Carrier> carriers = mUser.getCarriers();
            if (carriers != null && !carriers.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Carrier carrier : carriers) {
                    sb.append(carrier.getName());
                    sb.append(",");
                }
                carriername = sb.substring(0, sb.length() - 1);
            } else {
                carriername = "";
            }

            return new UserHeaderInfo(mTimeZone, driverName, selectedExemptions,
                    allExemptions != null ? allExemptions : "", carriername);
        });
    }

    private Single<SelectedLogHeaderInfo> loadLogHeaderInfo() {
        return Single.fromCallable(() -> {
            if (mSelectedLogHeader == null) return new SelectedLogHeaderInfo();

            int vehicleId = mSelectedLogHeader.getVehicleId();
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

            String vehicleTrailers = mSelectedLogHeader.getCoDriverIds();

            String homeTerminalAddress;
            String homeTerminalName;
            if (mSelectedLogHeader.getHomeTerminal() != null) {
                homeTerminalAddress = mSelectedLogHeader.getHomeTerminal().getAddress();
                homeTerminalName = mSelectedLogHeader.getHomeTerminal().getName();
            } else {
                homeTerminalAddress = "";
                homeTerminalName = "";
            }

            String shippingId = mSelectedLogHeader.getShippingId();
            String coDriversname = getCoDriversName(mSelectedLogHeader);

            return new SelectedLogHeaderInfo(vehicleName, vehicleLicense, vehicleTrailers,
                    homeTerminalAddress, homeTerminalName, shippingId, coDriversname);
        });
    }

    private Single<OdometerResult> loadOdometerValue() {

        final long startDate = DateUtils.getStartDate(mTimeZone, mSelectedDayCalendar);
        final long endDate = startDate + MS_IN_DAY;

        return mELDEventsInteractor.getDutyEventsFromDB(startDate, endDate)
                .first(Collections.emptyList())
                .flatMap(events -> Single.fromCallable(() -> {

                    final long startValue;
                    final long endValue;
                    long distance = 0;

                    if (events.isEmpty()) {
                        startValue = 0;
                        endValue = 0;
                        distance = 0;
                    } else {

                        Integer firstOdometerValue = events.get(0).getOdometer();
                        startValue = firstOdometerValue != null ? firstOdometerValue : 0;
                        int size = events.size();
                        if (size == 1) {
                            endValue = startValue;
                        } else {
                            Integer odometer = events.get(size - 1).getOdometer();
                            endValue = odometer != null ? odometer : 0;
                        }

                        long startDrivingOdometer = 0;
                        boolean isPreviousDriving = false;
                        for (ELDEvent event : events) {

                            if (!isPreviousDriving
                                    && DutyType.DRIVING.isSame(event.getEventType(), event.getEventCode())) {

                                // start driving event, save odometer value
                                startDrivingOdometer = event.getOdometer() != null ? event.getOdometer() : 0;
                                isPreviousDriving = true;
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
                        // subtract a first odometer value for correctness
                        distance -= startValue;
                    }

                    return new OdometerResult(startValue, endValue, distance);
                }));
    }

    private LogHeaderModel mapUserAndHeader(UserHeaderInfo userHeaderInfo,
                                            SelectedLogHeaderInfo selectedLogHeaderInfo) {

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
        return model;
    }

    private LogHeaderModel mapOdometerValue(LogHeaderModel model, OdometerResult odometerResult) {

        model.setStartOdometer(String.valueOf(odometerResult.startValue));
        model.setEndOdometer(String.valueOf(odometerResult.endValue));
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
            // no users in the database with these ids
            return "";
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
        mDisposables.clear();
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
        private final long startValue;
        private final long endValue;
        private final long distance;

        private OdometerResult(long startValue, long endValue, long distance) {
            this.startValue = startValue;
            this.endValue = endValue;
            this.distance = distance;
        }
    }
}
