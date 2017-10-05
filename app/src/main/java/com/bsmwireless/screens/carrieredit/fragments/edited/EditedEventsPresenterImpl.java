package com.bsmwireless.screens.carrieredit.fragments.edited;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.DutyUtils;
import com.bsmwireless.common.utils.LogHeaderUtils;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_PU;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_YM;
import static java.util.stream.Collectors.toList;

public final class EditedEventsPresenterImpl implements EditedEventsPresenter {

    private final ELDEventsInteractor mELDEventsInteractor;
    private final LogSheetInteractor mLogSheetInteractor;
    private final UserInteractor mUserInteractor;
    private final VehiclesInteractor mVehiclesInteractor;
    private final ServiceApi mServiceApi;
    private volatile CompositeDisposable mDisposable;
    private Disposable mUpdateEventsDisposable = Disposables.disposed();;
    private Disposable mSendUpdatedDisposable = Disposables.disposed();;
    private EditedEventsView mView;
    private String mTimeZone;
    private final LogHeaderUtils mLogHeaderUtils;
    private Map<Integer, Vehicle> mVehicleIdToNameMap = new HashMap<>();
    private Context mContext;

    @Inject
    public EditedEventsPresenterImpl(ELDEventsInteractor eldEventsInteractor,
                                     LogSheetInteractor logSheetInteractor, UserInteractor userInteractor,
                                     VehiclesInteractor vehiclesInteractor, ServiceApi serviceApi, LogHeaderUtils logHeaderUtils, Context context) {
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

        if (mUpdateEventsDisposable.isDisposed()) {
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
    }

    @Override
    public void approveEdits(List<EventLogModel> events, long logDay) {
        if (events != null && !events.isEmpty()) {
            Timber.v("approveEdits: ");
            sendUpdatedEvents(events, logDay, ELDEvent.StatusCode.ACTIVE);
        }
    }

    @Override
    public void disapproveEdits(List<EventLogModel> events, long logDay) {
        if (events != null && !events.isEmpty()) {
            Timber.v("disapproveEdits: ");
            sendUpdatedEvents(events, logDay, ELDEvent.StatusCode.INACTIVE_CHANGE_REJECTED);
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

    private void sendUpdatedEvents(List<EventLogModel> events, long logDay, ELDEvent.StatusCode code) {
        if (mSendUpdatedDisposable.isDisposed()) {
            Timber.v("sendUpdatedEvents: ");
            final List<ELDEvent> cachedEvents = new ArrayList<>();
            mSendUpdatedDisposable = Observable.fromIterable(events)
                    .subscribeOn(Schedulers.io())
                    .map(logModel -> logModel.getEvent().setStatus(code.getValue()))
                    .toList()
                    .doOnSuccess(eldEvents -> cachedEvents.addAll(eldEvents))
                    .flatMap(eldEvents -> mServiceApi.updateELDEvents(eldEvents))
                    .doOnSuccess(resp -> updateDbEldEvents(cachedEvents, logDay))
                    .subscribe(responseMessage -> {
                        mSendUpdatedDisposable.dispose();
                    }, Timber::e);
        }
    }

    private void updateDbEldEvents(List<ELDEvent> events, long logDay) {
        Timber.v("updateDbEldEvents: ");
        mDisposable.add(mELDEventsInteractor.updateELDEvents(events)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> updateDataForDay(logDay), Timber::e));
    }

    private void setLogHeaderData(long logDay) {
        Timber.v("setLogHeaderData: ");
        LogHeaderModel model = new LogHeaderModel();
        mDisposable.add(mUserInteractor.getFullUser()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(user -> updateLogHeaderModelByUser(model, user))
                .flatMap(user -> mLogSheetInteractor.getLogSheet(logDay))
                .doOnSuccess(logSheetHeader -> updateLogHeaderModelByLogSheet(model, logSheetHeader))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventLogModels -> mView.setLogHeader(model), Timber::e));
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

    private void updateLogHeaderModelByUser(LogHeaderModel logHeaderModel, User user) {
        Timber.v("updateLogHeaderModelByUser: ");
        logHeaderModel.setTimezone(user.getTimezone());
        logHeaderModel.setDriverName(user.getFirstName() + " " + user.getLastName());
        logHeaderModel.setSelectedExemptions(mLogHeaderUtils.getAllExemptions(user,
                SyncConfiguration.Type.EXCEPT));

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
        String carrierName = mLogHeaderUtils.makeCarrierName(user);
        logHeaderModel.setCarrierName(carrierName);
        logHeaderModel.setSelectedExemptions(user.getRuleException());
    }

    private void updateLogHeaderModelByLogSheet(LogHeaderModel logHeaderModel, LogSheetHeader logSheetHeader) {
        Timber.v("updateLogHeaderModelByLogSheet: ");
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
}
