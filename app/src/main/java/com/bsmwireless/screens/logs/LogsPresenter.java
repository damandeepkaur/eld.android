package com.bsmwireless.screens.logs;


import com.bsmwireless.common.Constants;
import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.SyncInteractor;
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
import java.util.TimeZone;

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

    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private UserInteractor mUserInteractor;
    private DutyTypeManager mDutyTypeManager;
    private AccountManager mAccountManager;
    private CompositeDisposable mDisposables;
    private String mTimeZone;
    private User mUser;
    private LogHeaderModel mLogHeaderModel;
    private Map<Integer, Vehicle> mVehicleIdToNameMap = new HashMap<>();
    private Map<Long, LogSheetHeader> mLogSheetHeadersMap = new HashMap<>();
    private Disposable mGetEventsFromDBDisposable;
    private Disposable mGetTimezoneDisposable;
    private Calendar mSelectedDayCalendar;
    private LogSheetHeader mSelectedLogHeader;
    private SyncInteractor mSyncInteractor;

    private DutyTypeManager.DutyTypeListener mListener = dutyType -> mView.dutyUpdated();

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor eventsInteractor, LogSheetInteractor logSheetInteractor,
                         VehiclesInteractor vehiclesInteractor, UserInteractor userInteractor, DutyTypeManager dutyTypeManager,
                         AccountManager accountManager, SyncInteractor syncInteractor) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mSyncInteractor = syncInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mDutyTypeManager = dutyTypeManager;
        mDisposables = new CompositeDisposable();
        mGetEventsFromDBDisposable = Disposables.disposed();
        mGetTimezoneDisposable = Disposables.disposed();
        mLogHeaderModel = new LogHeaderModel();
        mAccountManager = accountManager;
        Timber.d("CREATED");

        mDutyTypeManager.addListener(mListener);
    }

    public void onViewCreated() {
        mDisposables.add(
                mUserInteractor.getFullUser()
                        .subscribeOn(Schedulers.io())
                        .flatMap(user -> {
                            mUser = user;
                            mTimeZone = user.getTimezone();
                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            long todayDateLong = DateUtils.convertTimeToLogDay(mTimeZone, currentTime);
                            long monthAgoLong = DateUtils.convertTimeToLogDay(mTimeZone, currentTime
                                    - MS_IN_DAY * Constants.DEFAULT_CALENDAR_DAYS_COUNT);
                            return mLogSheetInteractor.getLogSheetHeaders(monthAgoLong, todayDateLong);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                logSheetHeaders -> {
                                    mLogSheetHeadersMap = new HashMap<>(logSheetHeaders.size());
                                    for (LogSheetHeader logSheetHeader : logSheetHeaders) {
                                        mLogSheetHeadersMap.put(logSheetHeader.getLogDay(), logSheetHeader);
                                    }

                                    mView.setLogSheetHeaders(logSheetHeaders);
                                    if (mSelectedDayCalendar == null) {
                                        mSelectedDayCalendar = Calendar.getInstance(TimeZone.getTimeZone(mTimeZone));
                                    }

                                    setEventsForDay(mSelectedDayCalendar);
                                    setLogHeaderForDay(mSelectedDayCalendar);
                                }, Timber::e));
        mAccountManager.addListener(this);
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        mSelectedLogHeader = calendarItem.getAssociatedLogSheet();
        setEventsForDay(calendarItem.getCalendar());
        setLogHeaderForDay(mSelectedDayCalendar);
    }

    public void setEventsForDay(Calendar calendar) {
        //not yet initialized
        if (mTimeZone == null) {
            return;
        }

        mSelectedDayCalendar = calendar;

        long startDayTime = DateUtils.getStartDate(mTimeZone, calendar);
        long endDayTime = startDayTime + MS_IN_DAY;

        mSyncInteractor.syncEventsForDay(calendar, mTimeZone);

        mGetEventsFromDBDisposable = mELDEventsInteractor.getDutyEventsFromDB(startDayTime, endDayTime)
                .map(selectedDayEvents -> {
                    List<ELDEvent> prevDayLatestEvents = mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(startDayTime, mUserInteractor.getUserId());
                    ELDEvent prevDayLatestEvent = null;
                    if (!prevDayLatestEvents.isEmpty()) {
                        prevDayLatestEvent = prevDayLatestEvents.get(prevDayLatestEvents.size() - 1);
                        prevDayLatestEvent.setEventTime(startDayTime);
                    }
                    mView.setPrevDayEvent(prevDayLatestEvent);
                    return selectedDayEvents;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eldEvents -> {
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    List<EventLogModel> dutyStateLogs = preparingLogs(eldEvents, startDayTime,
                            endDayTime < currentTime ? endDayTime : currentTime);

                    HashSet<Integer> vehicleIds = new HashSet<>();
                    for (EventLogModel log : dutyStateLogs) {
                        int vehicleId = log.getEvent().getVehicleId();
                        if (mVehicleIdToNameMap.containsKey(vehicleId)) {
                            log.setVehicleName(mVehicleIdToNameMap.get(vehicleId).getName());
                        } else {
                            vehicleIds.add(vehicleId);
                        }
                    }

                    mView.setEventLogs(dutyStateLogs);
                    if (mLogSheetInteractor != null) mView.setLogHeader(mLogHeaderModel);
                    updateVehicleInfo(new ArrayList<>(vehicleIds), dutyStateLogs);
                    updateLogHeader();
                }, Timber::e);
        mDisposables.add(mGetEventsFromDBDisposable);
    }

    public void setLogHeaderForDay(Calendar calendar) {
        long startDayTime = DateUtils.getStartDate(mTimeZone, calendar);
        long logDay = DateUtils.convertTimeToLogDay(mTimeZone, startDayTime);
        LogSheetHeader logSheetHeader = mLogSheetHeadersMap.get(logDay);

        if (logSheetHeader == null) {
            //create logsheet if not exist
            mDisposables.add(mLogSheetInteractor.getLogSheet(logDay)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(logSheet -> {
                                mSelectedLogHeader = logSheet;
                                mLogSheetHeadersMap.put(logSheet.getLogDay(), logSheet);
                                mView.setLogSheetHeaders(new ArrayList<>(mLogSheetHeadersMap.values()));
                                updateLogHeader();
                            },
                            Timber::e
                    ));
        } else {
            mSelectedLogHeader = logSheetHeader;
            updateLogHeader();
        }
    }

    private void updateVehicleInfo(List<Integer> vehicleIds, List<EventLogModel> logs) {
        mDisposables.add(mVehiclesInteractor.getVehiclesFromDB(vehicleIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vehicles -> {
                            for (Vehicle vehicle : vehicles) {
                                mVehicleIdToNameMap.put(vehicle.getId(), vehicle);
                            }

                            for (EventLogModel log : logs) {
                                if (mVehicleIdToNameMap.containsKey(log.getEvent().getVehicleId())) {
                                    log.setVehicleName(mVehicleIdToNameMap.get(log.getEvent().getVehicleId()).getName());
                                }
                            }
                            mView.setEventLogs(logs);
                        }
                        , throwable -> Timber.e(throwable.getMessage())
                ));
    }

    private void updateLogHeader() {

        Disposable disposable = loadUserHeaderInfo()
                .zipWith(loadLogHeaderInfo(), this::mapUserAndHeader)
                .zipWith(loadOdometerValue(), this::mapOdometerValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logHeaderModel -> mView.setLogHeader(logHeaderModel));
        mDisposables.add(disposable);
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

    public void onAddEventClicked(CalendarItem day) {
        mView.goToAddEventScreen(day);
    }

    public void onEditLogHeaderClicked() {
        mView.goToEditLogHeaderScreen(mLogHeaderModel);
    }

    public void onEventAdded(List<ELDEvent> newEvents) {
        Disposable disposable = mELDEventsInteractor.postNewELDEvents(newEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mView.eventAdded(),
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_ADD_EVENT);
                        });
        mDisposables.add(disposable);
    }

    public void onEventChanged(List<ELDEvent> updatedEvents) {
        Disposable disposable = mELDEventsInteractor.updateELDEvents(updatedEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> mView.eventUpdated(),
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_UPDATE_EVENT);

                        });
        mDisposables.add(disposable);
    }

    public void onLogHeaderChanged(LogHeaderModel logHeaderModel) {
        mView.setLogHeader(logHeaderModel);

        mSelectedLogHeader = logHeaderModel.getLogSheetHeader();

        HomeTerminal homeTerminal = mSelectedLogHeader.getHomeTerminal();
        if (homeTerminal == null) {
            homeTerminal = new HomeTerminal();
            homeTerminal.setTimezone(mTimeZone);
        }

        homeTerminal.setName(logHeaderModel.getHomeTerminalName());
        homeTerminal.setAddress(logHeaderModel.getHomeTerminalAddress());
        mSelectedLogHeader.setHomeTerminal(homeTerminal);
        mSelectedLogHeader.setTrailerIds(logHeaderModel.getTrailers());
        mSelectedLogHeader.setShippingId(logHeaderModel.getShippingId());

        //TODO: remove after clean up incorrect data for logsheetheader
        if (mSelectedLogHeader.getBoxId() == null || mSelectedLogHeader.getBoxId() < 0) {
            mSelectedLogHeader.setBoxId(mVehiclesInteractor.getBoxId());
        }
        if (mSelectedLogHeader.getVehicleId() == null || mSelectedLogHeader.getVehicleId() < 0) {
            mSelectedLogHeader.setVehicleId(mVehiclesInteractor.getVehicleId());
        }
        if (mSelectedLogHeader.getDriverId() == null || mSelectedLogHeader.getDriverId() < 0) {
            mSelectedLogHeader.setDriverId(mUser.getId());
        }

        mDisposables.add(mLogSheetInteractor.updateLogSheetHeader(mSelectedLogHeader)
                .flatMapObservable(isLogSheetUpdated -> mUserInteractor.updateDriverRule(
                        logHeaderModel.getSelectedExemptions(), mUser.getDutyCycle()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mLogHeaderModel = logHeaderModel;
                            mView.setLogHeader(mLogHeaderModel);
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(LogsView.Error.ERROR_UPDATE_EVENT);
                        }));
    }

    public void onDestroy() {
        mAccountManager.removeListener(this);
        mDutyTypeManager.removeListener(mListener);
        mGetTimezoneDisposable.dispose();
        mGetEventsFromDBDisposable.dispose();
        mDisposables.dispose();
        Timber.d("DESTROYED");
    }

    private List<EventLogModel> preparingLogs(List<ELDEvent> events, long startDayTime, long endDayTime) {
        List<EventLogModel> logs = new ArrayList<>();

        if (!events.isEmpty()) {
            //convert to logs model
            long duration;
            for (int i = 0; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                EventLogModel log = new EventLogModel(event, mTimeZone);
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
                if (i < events.size() - 1) {
                    duration = events.get(i + 1).getEventTime() - events.get(i).getEventTime();
                    logs.get(i).setDuration(duration);
                }
            }

            //set duration for last event
            EventLogModel lastEvent = logs.get(logs.size() - 1);
            lastEvent.setDuration(endDayTime - lastEvent.getEventTime());
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
        mDisposables.dispose();
        onViewCreated();
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
