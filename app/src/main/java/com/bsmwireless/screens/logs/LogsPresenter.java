package com.bsmwireless.screens.logs;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.DutyUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
                .doOnSuccess(eventLogModels -> graphModel.setEventLogModels(eventLogModels))
                .doOnSuccess(eventLogModels -> graphModel.setPrevDayEvent(
                        mELDEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eldEvents -> mView.updateGraph(graphModel)));
    }

    private void setEventListData(long startDayTime, String timezone) {
        mUpdateDayDataDisposables.add(mELDEventsInteractor.getEventsForDayOnce(startDayTime)
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
}
