package com.bsmwireless.screens.logs;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
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
                            getLogSheet();
                        },
                        error -> Timber.e("Get timezone error: %s", error)
                ));
    }

    private void getLogSheet() {
        long todayDateLong = DateUtils.convertTimeToDayNumber(mTimeZone, System.currentTimeMillis());
        long monthAgoLong = DateUtils.convertTimeToDayNumber(mTimeZone, System.currentTimeMillis()
                - MS_IN_DAY * Constants.DEFAULT_CALENDAR_DAYS_COUNT);
        mDisposables.add(mLogSheetInteractor.syncLogSheetHeader(monthAgoLong, todayDateLong)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        logSheetHeaders -> {
                            mView.setLogSheetHeaders(logSheetHeaders);
                            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(mTimeZone));
                            updateEventForDay(calendar);
                        },
                        error -> Timber.e("LoginUser error: %s", error)
                ));
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        //TODO: check should we show events for day without logsheet or not
        // logSheetHeader logSheet = calendarItem.getAssociatedLogSheet();
        // long startDayTime = DateUtils.getStartDayInUnixMsFromLogday(mTimeZone, logSheet.getLogDay());

        updateEventForDay(calendarItem.getCalendar());
    }

    public void updateEventForDay(Calendar calendar) {
        long startDayTime = DateUtils.getStartDate(mTimeZone, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        long endDayTime = startDayTime + MS_IN_DAY;

        mELDEventsInteractor.syncELDEvents(startDayTime, endDayTime);
        if (mGetEventDisposable != null) mGetEventDisposable.dispose();
        mGetEventDisposable = mELDEventsInteractor.getELDEventsFromDB(startDayTime, endDayTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eldEvents -> {
                    List<EventLogModel> logs = new ArrayList<>(eldEvents.size());
                    List<EventLogModel> dutyStateLogs = Collections.EMPTY_LIST;
                    HashSet<Integer> vehicleIds = new HashSet<>();

                    if(!eldEvents.isEmpty()) {
                        ELDEvent prevEvent = eldEvents.get(0);
                        long duration = prevEvent.getEventTime() - startDayTime;
                        logs.add(new EventLogModel(prevEvent, mTimeZone));


                        for (int i = 1; i < eldEvents.size(); i++) {
                            ELDEvent event = eldEvents.get(i);

                            EventLogModel log = new EventLogModel(event, mTimeZone);

                            if (mVehicleIdToNameMap.containsKey(event.getVehicleId())) {
                                log.setVehicleName(mVehicleIdToNameMap.get(event.getVehicleId()));
                            } else {
                                vehicleIds.add(event.getVehicleId());
                            }

                            logs.add(log);
                        }

                        dutyStateLogs = filterEventByType(logs, ELDEvent.EventType.DUTY_STATUS_CHANGING);
                        for (int i = 1; i < dutyStateLogs.size(); i++) {
                            duration = dutyStateLogs.get(i).getEventTime() - dutyStateLogs.get(i - 1).getEventTime();
                            dutyStateLogs.get(i).setDuration(duration);
                        }
                    }

                    mTripInfo.setStartDayTime(startDayTime);
                    mView.setTripInfo(mTripInfo);
                    mView.setEventLogs(logs);
                    updateTripInfo(dutyStateLogs, endDayTime);
                    updateVehicleInfo(new ArrayList<>(vehicleIds), logs);
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
                        }
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

            tripInfo.setSleeperBerthTime(DateUtils.convertTimeInMsToStringTime(
                    result[DutyType.SLEEPER_BERTH.getValue() - 1]));
            tripInfo.setDrivingTime(DateUtils.convertTimeInMsToStringTime(
                    result[DutyType.DRIVING.getValue() - 1]));
            tripInfo.setOffDutyTime(DateUtils.convertTimeInMsToStringTime(
                    result[DutyType.OFF_DUTY.getValue() - 1]));
            tripInfo.setOnDutyTime(DateUtils.convertTimeInMsToStringTime(
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

    private List<EventLogModel> filterEventByType(List<EventLogModel> events, ELDEvent.EventType eventType) {
        List<EventLogModel> result = new ArrayList<>();
        for (EventLogModel event : events) {
            if (event.getEventType().equals(eventType.getValue())) {
                result.add(event);
            }
        }
        return result;
    }


    public void onSignLogsheetButtonClicked() {
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
        Disposable disposable = mELDEventsInteractor.postNewELDEvents(new ArrayList<ELDEvent>() {{ add(newEvent); }})
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(isUpdated -> {
                                                        if (isUpdated) {
                                                            mView.eventAdded();
                                                        } else {
                                                            mView.showError(LogsView.Error.ERROR_ADD_EVENT);
                                                        }
                                                    }, throwable -> mView.showError(throwable));
        mDisposables.add(disposable);
    }

    public void onEventChanged(ELDEvent updatedEvent) {
        Disposable disposable = mELDEventsInteractor.updateELDEvents(new ArrayList<ELDEvent>() {{ add(updatedEvent); }})
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(isUpdated -> {
                                                        if (isUpdated) {
                                                            mView.eventUpdated();
                                                        } else {
                                                            mView.showError(LogsView.Error.ERROR_UPDATE_EVENT);
                                                        }
                                                    }, throwable -> mView.showError(throwable));
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
}
