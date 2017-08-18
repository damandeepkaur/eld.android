package com.bsmwireless.screens.logs;


import com.bsmwireless.common.Constants;
import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.DutyManager;
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
import java.util.ListIterator;
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
    Disposable mGetEventDisposable;
    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private UserInteractor mUserInteractor;
    private DutyManager mDutyManager;

    private CompositeDisposable mDisposables;
    private String mTimeZone;
    private TripInfoModel mTripInfo;
    private Map<Integer, String> mVehicleIdToNameMap = new HashMap<>();
    private List<LogSheetHeader> mLogSheetHeaders;

    private DutyManager.DutyTypeListener mListener = new DutyManager.DutyTypeListener() {
        @Override
        public void onDutyTypeChanged(DutyType dutyType) {
            mView.dutyUpdated();
        }
    };

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor eventsInteractor, LogSheetInteractor logSheetInteractor,
                         VehiclesInteractor vehiclesInteractor, UserInteractor userInteractor, DutyManager dutyManager) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mDutyManager = dutyManager;
        mDisposables = new CompositeDisposable();
        mTripInfo = new TripInfoModel();
        Timber.d("CREATED");

        mDutyManager.addListener(mListener);
    }

    public void onViewCreated() {
        mDisposables.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        timeZone -> {
                            mTimeZone = timeZone;
                            updateCalendar();
                        },
                        error -> Timber.e("Get timezone error: %s", error)
                ));
    }

    private void updateCalendar() {
        long todayDateLong = DateUtils.convertTimeToDayNumber(mTimeZone, System.currentTimeMillis());
        long monthAgoLong = DateUtils.convertTimeToDayNumber(mTimeZone, System.currentTimeMillis()
                - MS_IN_DAY * Constants.DEFAULT_CALENDAR_DAYS_COUNT);
        mDisposables.add(mLogSheetInteractor.getLogSheetHeaders(monthAgoLong, todayDateLong)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        logSheetHeaders -> {
                            mLogSheetHeaders = logSheetHeaders;
                            mView.setLogSheetHeaders(logSheetHeaders);
                            Calendar currentDayCalendar = Calendar.getInstance(TimeZone.getTimeZone(mTimeZone));
                            setEventsForDay(currentDayCalendar);
                        },
                        error -> Timber.e("LoginUser error: %s", error)
                ));
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        setEventsForDay(calendarItem.getCalendar());
    }

    public void setEventsForDay(Calendar calendar) {
        long startDayTime = DateUtils.getStartDate(mTimeZone, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        long endDayTime = startDayTime + MS_IN_DAY;

        mELDEventsInteractor.syncELDEvents(startDayTime - MS_IN_DAY, endDayTime);
        if (mGetEventDisposable != null) mGetEventDisposable.dispose();
        mGetEventDisposable = mELDEventsInteractor.getELDEventsFromDB(startDayTime - MS_IN_DAY, endDayTime)
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

                    mTripInfo.setStartDayTime(startDayTime);
                    mView.setTripInfo(mTripInfo);
                    mView.setEventLogs(dutyStateLogs);
                    updateTripInfo(dutyStateLogs, endDayTime);
                    updateVehicleInfo(new ArrayList<>(vehicleIds), dutyStateLogs);
                }, throwable -> Timber.e(throwable.getMessage()));
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
                        }, throwable -> Timber.e(throwable.getMessage())
                ));
    }

    public void updateTripInfo(final List<EventLogModel> events, final long endDayTime) {
        TripInfoModel tripInfo = new TripInfoModel();

        Disposable disposable = Observable.create((ObservableOnSubscribe<TripInfoModel>) e -> {
            long[] result = new long[DutyType.values().length];
            int odometer = 0;
            EventLogModel log = events.get(0);
            for (int i = 1; i < events.size(); i++) {
                log = events.get(i);
                EventLogModel prevLog = events.get(i - 1);
                long logDate = log.getEventTime();
                long prevLogDate = prevLog.getEventTime();
                long timeStamp = (logDate - prevLogDate);
                result[prevLog.getEventCode() - 1] += timeStamp;

                if (log.getEvent().getOdometer() != null && odometer < log.getEvent().getOdometer()) {
                    odometer = log.getEvent().getOdometer();
                }
            }
            result[log.getEventCode() - 1] += endDayTime - log.getEventTime();

            tripInfo.setSleeperBerthTime(DateUtils.convertTotalTimeInMsToStringTime(
                    result[DutyType.SLEEPER_BERTH.getValue() - 1]));
            tripInfo.setDrivingTime(DateUtils.convertTotalTimeInMsToStringTime(
                    result[DutyType.DRIVING.getValue() - 1]));
            tripInfo.setOffDutyTime(DateUtils.convertTotalTimeInMsToStringTime(
                    result[DutyType.OFF_DUTY.getValue() - 1]));
            tripInfo.setOnDutyTime(DateUtils.convertTotalTimeInMsToStringTime(
                    result[DutyType.ON_DUTY.getValue() - 1]));

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
                        response -> mView.setLogSheetHeaders(mLogSheetHeaders),
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

    public void onEventAdded(ELDEvent newEvent) {
        Disposable disposable = mELDEventsInteractor.postNewELDEvents(new ArrayList<ELDEvent>() {{
            add(newEvent);
        }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isUpdated -> {
                    if (isUpdated) {
                        mView.eventAdded();
                    } else {
                        mView.showError(LogsView.Error.ERROR_ADD_EVENT);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (throwable instanceof RetrofitException) {
                        mView.showError((RetrofitException) throwable);
                    }
                });
        mDisposables.add(disposable);
    }

    public void onEventChanged(ELDEvent updatedEvent) {
        Disposable disposable = mELDEventsInteractor.updateELDEvents(new ArrayList<ELDEvent>() {{
            add(updatedEvent);
        }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isUpdated -> {
                    if (isUpdated) {
                        mView.eventUpdated();
                    } else {
                        mView.showError(LogsView.Error.ERROR_UPDATE_EVENT);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (throwable instanceof RetrofitException) {
                        mView.showError((RetrofitException) throwable);
                    }
                });
        mDisposables.add(disposable);
    }

    public void onDestroy() {
        mDutyManager.removeListener(mListener);

        if (mGetEventDisposable != null) {
            mGetEventDisposable.dispose();
        }
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
        //filter events
        ListIterator<ELDEvent> iterator = events.listIterator();
        while (iterator.hasNext()) {
            ELDEvent event = iterator.next();
            if (!event.getEventType().equals(ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue())
                    && !event.getEventType().equals(ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue())) {
                iterator.remove();
            } else {
                int i = iterator.nextIndex();
                if (i < events.size() - 1) {
                    if (events.get(i).getEventTime() < startDayTime) {
                        iterator.remove();
                    }
                }
            }
        }

        if (!events.isEmpty()) {
            //convert to logs model
            long duration;
            for (int i = 0; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                EventLogModel log = new EventLogModel(event, mTimeZone);
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
