package com.bsmwireless.screens.logs;


import com.bsmwireless.common.Constants;
import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
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
        Single.fromCallable(() -> {
            LogHeaderModel model = new LogHeaderModel();
            model.setTimezone(mTimeZone);

            if (mUser != null) {
                model.setDriverName(mUser.getFirstName() + " " + mUser.getLastName());
                model.setSelectedExemptions(mUser.getRuleException());

                List<SyncConfiguration> configurations = mUser.getConfigurations();
                if (configurations != null) {
                    for (SyncConfiguration configuration : configurations) {
                        if (SyncConfiguration.Type.EXCEPT.getName().equals(configuration.getName())) {
                            model.setAllExemptions(configuration.getValue());
                            break;
                        }
                    }
                } else {
                    model.setAllExemptions("");
                }

                //set carrier name
                List<Carrier> carriers = mUser.getCarriers();
                if (carriers != null && !carriers.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Carrier carrier : carriers) {
                        sb.append(carrier.getName());
                        sb.append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    model.setCarrierName(sb.toString());
                }
                model.setSelectedExemptions(mUser.getRuleException());
            }

            if (mSelectedLogHeader != null) {
                int vehicleId = mSelectedLogHeader.getVehicleId();
                Vehicle vehicle;
                if (mVehicleIdToNameMap.containsKey(vehicleId)) {
                    vehicle = mVehicleIdToNameMap.get(vehicleId);
                } else {
                    vehicle = mVehiclesInteractor.getVehicle(vehicleId);
                }
                if (vehicle != null) {
                    model.setVehicleName(vehicle.getName());
                    model.setVehicleLicense(vehicle.getLicense());
                }
                model.setTrailers(mSelectedLogHeader.getTrailerIds());

                if (mSelectedLogHeader.getHomeTerminal() != null) {
                    model.setHomeTerminalAddress(mSelectedLogHeader.getHomeTerminal().getAddress());
                    model.setHomeTerminalName(mSelectedLogHeader.getHomeTerminal().getName());
                }

                model.setShippingId(mSelectedLogHeader.getShippingId());

                String codriverIds = mSelectedLogHeader.getCoDriverIds();
                //TODO: get codriver names by ids
                model.setCoDriversName(codriverIds);
            }
            //TODO: init by data from black box
            model.setStartOdometer("0");
            model.setEndOdometer("0");
            model.setDistanceDriven("-");

            mLogHeaderModel = model;
            mLogHeaderModel.setLogSheetHeader(mSelectedLogHeader);
            return mLogHeaderModel;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logHeaderModel -> mView.setLogHeader(logHeaderModel));
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
                .flatMap(isLogSheetUpdated -> mUserInteractor.updateDriverRule(
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
        int lastActiveIndex = -1;

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

    @Override
    public void onUserChanged() {
        mDisposables.dispose();
        onViewCreated();
    }

    @Override
    public void onDriverChanged() {
    }
}
