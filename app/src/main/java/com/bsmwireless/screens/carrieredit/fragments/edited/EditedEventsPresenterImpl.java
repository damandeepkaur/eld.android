package com.bsmwireless.screens.carrieredit.fragments.edited;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.Carrier;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.SyncConfiguration;
import com.bsmwireless.models.User;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.logs.GraphModel;
import com.bsmwireless.screens.logs.LogHeaderModel;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_PU;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_YM;

public final class EditedEventsPresenterImpl implements EditedEventsPresenter {

    private final ELDEventsInteractor mELDEventsInteractor;
    private final LogSheetInteractor mLogSheetInteractor;
    private final UserInteractor mUserInteractor;
    private final VehiclesInteractor mVehiclesInteractor;
    private final ServiceApi mServiceApi;
    private final CompositeDisposable mDisposable;
    private EditedEventsView mView;
    private String mTimeZone;
    private User mUser;
    private Map<Long, LogSheetHeader> mLogSheetHeadersMap = new HashMap<>();
    private Map<Integer, Vehicle> mVehicleIdToNameMap = new HashMap<>();

    @Inject
    public EditedEventsPresenterImpl(ELDEventsInteractor eldEventsInteractor,
                                     LogSheetInteractor logSheetInteractor, UserInteractor userInteractor,
                                     VehiclesInteractor vehiclesInteractor, ServiceApi serviceApi) {
        mELDEventsInteractor = eldEventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mUserInteractor = userInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mServiceApi = serviceApi;
        mDisposable = new CompositeDisposable();
    }

    public void setView(EditedEventsView view) {
        mView = view;
        mDisposable.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .subscribe(timezone -> {
                    long logDay = DateUtils.convertTimeToLogDay(timezone, System.currentTimeMillis());
                    updateDataForDay(logDay);
                }));
        updateCalendarData();
    }

    private void updateCalendarData() {
        mDisposable.add(mUserInteractor.getTimezone()
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

    private void setEventListData(long startDayTime, String timezone) {
        mDisposable.add(mELDEventsInteractor.getUnidentifiedEvents()
                .subscribeOn(Schedulers.io())
                .switchMap(list -> Observable.fromIterable(list))
                .filter(eldEvent -> eldEvent.getEventTime() >= startDayTime
                        && eldEvent.getEventTime() < startDayTime + MS_IN_DAY)
                .toList()
                .map(eldEvents -> convertToEventLogModels(eldEvents, startDayTime, timezone))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventLogModels -> mView.setEvents(eventLogModels)));
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        updateDataForDay(calendarItem.getLogDay());
    }

    public void updateDataForDay(long logDay) {
        mDisposable.clear();
        mDisposable.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .subscribe(timezone -> {
                    long startDayTime = DateUtils.getStartDayTimeInMs(logDay, timezone);
                    setGraphData(startDayTime, timezone);
                    setEventListData(startDayTime, timezone);
                    setLogHeaderData(logDay);
                }));
    }

    private void setLogHeaderData(long logDay) {
        LogHeaderModel model = new LogHeaderModel();
        mDisposable.add(mUserInteractor.getFullUser()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(user -> updateLogHeaderModelByUser(model, user))
                .flatMap(user -> mLogSheetInteractor.getLogSheet(logDay))
                .doOnSuccess(logSheetHeader -> updateLogHeaderModelByLogSheet(model, logSheetHeader))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventLogModels -> mView.setLogHeader(model)));
    }

    private void setGraphData(long startDayTime, String timezone) {
        GraphModel graphModel = new GraphModel();
        graphModel.setStartDayTime(startDayTime);
        mDisposable.add(mELDEventsInteractor.getActiveDutyEventsForDay(startDayTime)
                .subscribeOn(Schedulers.io())
                .map(eldEvents -> convertToEventLogModels(eldEvents, startDayTime, timezone))
                .doOnSuccess(eventLogModels -> graphModel.setEventLogModels(eventLogModels))
                .doOnSuccess(eventLogModels -> graphModel.setPrevDayEvent(
                        mELDEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eldEvents -> mView.updateGraph(graphModel)));
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

    private List<EventLogModel> preparingLogs(List<ELDEvent> events) {
        List<EventLogModel> logs = new ArrayList<>();

        if (!events.isEmpty()) {
            //convert to logs model
            long duration;
            long startDayTime = Long.MAX_VALUE;
            long endDayTime = -1;
            for (int i = 0; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                EventLogModel log = new EventLogModel(event, mTimeZone);
                if (event.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()
                        && event.getEventCode() == DutyType.CLEAR.getCode()) {
                    log.setDutyType(DutyType.CLEAR);
                    //get code of indication ON event for indication OFF event
                    for (int j = i - 1; j >= 0; j--) {
                        ELDEvent dutyEvent = events.get(j);
                        long time = event.getEventTime();
                        if (time < startDayTime) {
                            startDayTime = time;
                        }
                        if (time > endDayTime) {
                            endDayTime = time;
                        }

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
}
