package com.bsmwireless.screens.logs;


import com.bsmwireless.common.Constants;
import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
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

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.screens.logs.TripInfoModel.UnitType.KM;

@ActivityScope
public class LogsPresenter {
    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private UserInteractor mUserInteractor;
    private DutyTypeManager mDutyTypeManager;
    private CompositeDisposable mDisposables;
    private String mTimeZone;
    private TripInfoModel mTripInfo;
    private Map<Integer, String> mVehicleIdToNameMap = new HashMap<>();
    private List<LogSheetHeader> mLogSheetHeaders;
    private Disposable mGetEventsFromDBDisposable;
    private Calendar mSelectedDayCalendar;

    private DutyTypeManager.DutyTypeListener mListener = dutyType -> mView.dutyUpdated();

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor eventsInteractor, LogSheetInteractor logSheetInteractor,
                         VehiclesInteractor vehiclesInteractor, UserInteractor userInteractor, DutyTypeManager dutyTypeManager) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mDutyTypeManager = dutyTypeManager;
        mDisposables = new CompositeDisposable();
        mTripInfo = new TripInfoModel();
        Timber.d("CREATED");

        mDutyTypeManager.addListener(mListener);
    }

    public void onViewCreated() {
        mDisposables.add(
                mUserInteractor.getTimezone()
                        .subscribeOn(Schedulers.io())
                        .flatMap(timeZone -> {
                            mTimeZone = timeZone;
                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            long todayDateLong = DateUtils.convertTimeToDayNumber(mTimeZone, currentTime);
                            long monthAgoLong = DateUtils.convertTimeToDayNumber(mTimeZone, currentTime
                                    - MS_IN_DAY * Constants.DEFAULT_CALENDAR_DAYS_COUNT);
                            return mLogSheetInteractor.getLogSheetHeaders(monthAgoLong, todayDateLong);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                logSheetHeaders -> {
                                    mLogSheetHeaders = logSheetHeaders;
                                    mView.setLogSheetHeaders(logSheetHeaders);
                                    if (mSelectedDayCalendar == null) {
                                        mSelectedDayCalendar = Calendar.getInstance(TimeZone.getTimeZone(mTimeZone));
                                    }
                                    setEventsForDay(mSelectedDayCalendar);
                                }, error -> Timber.e("LoginUser error: %s", error)));
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        setEventsForDay(calendarItem.getCalendar());
    }

    public void setEventsForDay(Calendar calendar) {
        //not yet initialized
        if (mTimeZone == null) {
            return;
        }

        mSelectedDayCalendar = calendar;

        long startDayTime = DateUtils.getStartDate(mTimeZone, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        long endDayTime = startDayTime + MS_IN_DAY;

        mELDEventsInteractor.syncELDEventsWithServer(startDayTime - MS_IN_DAY, endDayTime);

        if (mGetEventsFromDBDisposable != null) mGetEventsFromDBDisposable.dispose();
        mGetEventsFromDBDisposable = mELDEventsInteractor.getDutyEventsFromDB(startDayTime, endDayTime)
                .map(selectedDayEvents -> {
                    List<ELDEvent> prevDayLatestEvents = mELDEventsInteractor.getLatestActiveDutyEventFromDB(startDayTime);
                    if (!prevDayLatestEvents.isEmpty()) {
                        prevDayLatestEvents.get(prevDayLatestEvents.size() - 1).setEventTime(startDayTime);
                        selectedDayEvents.add(0, prevDayLatestEvents.get(prevDayLatestEvents.size() - 1));
                    }
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
                            log.setVehicleName(mVehicleIdToNameMap.get(vehicleId));
                        } else {
                            vehicleIds.add(vehicleId);
                        }
                    }

                    mView.setTripInfo(mTripInfo);
                    mView.setEventLogs(dutyStateLogs);
                    updateTripInfo(dutyStateLogs, startDayTime, endDayTime);
                    updateVehicleInfo(new ArrayList<>(vehicleIds), dutyStateLogs);
                }, throwable -> Timber.e(throwable.getMessage()));
        mDisposables.add(mGetEventsFromDBDisposable);
    }

    public void updateVehicleInfo(List<Integer> vehicleIds, List<EventLogModel> logs) {
        mDisposables.add(mVehiclesInteractor.getVehiclesFromDB(vehicleIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vehicles -> {
                            for (Vehicle vehicle : vehicles) {
                                mVehicleIdToNameMap.put(vehicle.getId(), vehicle.getName());
                            }

                            for (EventLogModel log : logs) {
                                if (mVehicleIdToNameMap.containsKey(log.getEvent().getVehicleId())) {
                                    log.setVehicleName(mVehicleIdToNameMap.get(log.getEvent().getVehicleId()));
                                }
                            }
                            mView.setEventLogs(logs);
                        }
                        , throwable -> Timber.e(throwable.getMessage())
                ));
    }

    public void updateTripInfo(final List<EventLogModel> events, final long startDayTime, final long endDayTime) {
        TripInfoModel tripInfo = new TripInfoModel();

        Disposable disposable = Observable.create((ObservableOnSubscribe<TripInfoModel>) e -> {
            int odometer = 0;
            EventLogModel log;
            for (int i = 0; i < events.size(); i++) {
                log = events.get(i);

                if (log.getEvent().getOdometer() != null && odometer < log.getEvent().getOdometer()) {
                    odometer = log.getEvent().getOdometer();
                }
            }
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long[] times = DutyTypeManager.getDutyTypeTimes(new ArrayList<>(events), startDayTime, endDayTime < currentTime ? endDayTime : currentTime);

            tripInfo.setSleeperBerthTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.SLEEPER_BERTH.ordinal()]));
            tripInfo.setDrivingTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.DRIVING.ordinal()]));
            tripInfo.setOffDutyTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.OFF_DUTY.ordinal()]));
            tripInfo.setOnDutyTime(DateUtils.convertTotalTimeInMsToStringTime(times[DutyType.ON_DUTY.ordinal()]));

            //TODO: convert odometer value from meters to appropriate unit
            tripInfo.setUnitType(KM);
            tripInfo.setOdometerValue(odometer / 1000);

            e.onNext(tripInfo);
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(times -> {
                            mTripInfo = tripInfo;
                            mView.setTripInfo(mTripInfo);
                        },
                        throwable -> Timber.e(throwable.getMessage()));

        mDisposables.add(disposable);
    }


    public void onSignLogsheetButtonClicked(CalendarItem calendarItem) {
        LogSheetHeader logSheetHeader = calendarItem.getAssociatedLogSheet();
        if (logSheetHeader == null) {
            //TODO: move creation log sheet if not exist in event creation operation
            //create log sheet header if not exist
            long logday = DateUtils.convertTimeToDayNumber(mTimeZone, calendarItem.getCalendar().getTimeInMillis());
            mDisposables.add(mLogSheetInteractor.createLogSheetHeader(logday)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(logSheet -> onSignLogsheet(logSheet),
                            throwable -> {
                                Timber.e(throwable.getMessage());
                                if (throwable instanceof RetrofitException) {
                                    mView.showError((RetrofitException) throwable);
                                }
                            }
                    ));
        } else {
            onSignLogsheet(logSheetHeader);
        }
    }

    private void onSignLogsheet(LogSheetHeader logSheetHeader) {
        logSheetHeader.setSigned(true);
        ELDEvent event = createCertEvent(logSheetHeader);
        mDisposables.add(mELDEventsInteractor.postNewELDEvent(event)
                .subscribeOn(Schedulers.io())
                .flatMap(isCreated -> mLogSheetInteractor.updateLogSheetHeader(logSheetHeader))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            mLogSheetHeaders.add(logSheetHeader);
                            mView.setLogSheetHeaders(mLogSheetHeaders);
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            logSheetHeader.setSigned(false);
                            if (throwable instanceof RetrofitException) {
                                mView.showError((RetrofitException) throwable);
                            }
                        }
                ));
    }

    public void onEditEventClicked(EventLogModel event) {
        mView.goToEditEventScreen(event);
    }

    public void onRemovedEventClicked(EventLogModel event) {
    }

    public void onAddEventClicked(CalendarItem day) {
        mView.goToAddEventScreen(day);
    }

    public void onEditTripInfoClicked() {
        mView.goToEditTripInfoScreen();
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

    public void onDestroy() {
        mDutyTypeManager.removeListener(mListener);
        mDisposables.dispose();
        Timber.d("DESTROYED");
    }

    private ELDEvent createCertEvent(LogSheetHeader logSheetHeader) {
        long certDay = DateUtils.convertDayNumberToUnixMs(logSheetHeader.getLogDay());
        ELDEvent event = new ELDEvent();
        event.setStatus(ELDEvent.StatusCode.ACTIVE.getValue());
        event.setOrigin(ELDEvent.EventOrigin.DRIVER.getValue());
        event.setEventType(ELDEvent.EventType.CERTIFICATION_OF_RECORDS.getValue());
        event.setEventCode(1);
        event.setDriverId(logSheetHeader.getDriverId());
        event.setVehicleId(logSheetHeader.getVehicleId());
        event.setEventTime(certDay);
        event.setMobileTime(Calendar.getInstance().getTimeInMillis());
        event.setTimezone(mTimeZone);
        event.setBoxId(logSheetHeader.getBoxId());
        event.setMobileTime(Calendar.getInstance().getTimeInMillis());
        return event;
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
                    //get code of indication ON event for indication OFF event
                    for (int j = i - 1; j >= 0; j--) {
                        ELDEvent dutyEvent = events.get(j);

                        if (dutyEvent.getEventType() == ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()) {
                            log.setOnIndicationCode(dutyEvent.getEventCode());
                            break;
                        } else if (dutyEvent.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
                            if (dutyEvent.getEventCode() == DutyType.PERSONAL_USE.getCode()) {
                                log.setOnIndicationCode(DutyType.OFF_DUTY.getCode());
                                break;
                            } else if (dutyEvent.getEventCode() == DutyType.YARD_MOVES.getCode()) {
                                log.setOnIndicationCode(DutyType.ON_DUTY.getCode());
                                break;
                            }
                        }
                    }
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

    public void onWeekChanged(CalendarItem startWeekDay) {
        Calendar calendar = startWeekDay.getCalendar();
        long startWeekTime = DateUtils.getStartDate(mTimeZone, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        long endWeekTime = startWeekTime + MS_IN_DAY * 7;
        mELDEventsInteractor.syncELDEventsWithServer(startWeekTime, endWeekTime);
    }
}
