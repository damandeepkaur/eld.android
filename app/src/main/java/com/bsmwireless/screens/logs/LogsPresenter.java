package com.bsmwireless.screens.logs;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.screens.logs.TripInfo.UnitType.KM;

@ActivityScope
public class LogsPresenter {
    public static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;

    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private LoginUserInteractor mUserInteractor;
    private CompositeDisposable mDisposables;
    private String mTimeZone;
    private TripInfo mTripInfo;

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor eventsInteractor, LogSheetInteractor logSheetInteractor,
                         VehiclesInteractor vehiclesInteractor, LoginUserInteractor userInteractor) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mUserInteractor = userInteractor;
        mDisposables = new CompositeDisposable();
        mTripInfo = new TripInfo();
        Timber.d("CREATED");
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
                        error -> Timber.e("LoginUser error: %s", error)
                ));
    }

    private void getLogSheet() {
        long todayDateLong = DateUtils.convertTimeToDayNumber(mTimeZone, System.currentTimeMillis());
        long monthAgoLong = DateUtils.convertTimeToDayNumber(mTimeZone, System.currentTimeMillis()
                - ONE_DAY_MS * Constants.DEFAULT_CALENDAR_DAYS_COUNT);
        mDisposables.add(mLogSheetInteractor.syncLogSheetHeader(monthAgoLong, todayDateLong)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        logSheetHeaders -> {
                            for (LogSheetHeader header : logSheetHeaders) {
                                header.setLogDay(DateUtils.convertDayNumberToUnixMs(header.getLogDay()));
                            }
                            mView.setLogSheetHeaders(logSheetHeaders);
                        },
                        error -> Timber.e("LoginUser error: %s", error)
                ));
    }

    public void onCalendarDaySelected(CalendarItem calendarItem) {
        LogSheetHeader log = calendarItem.getAssociatedLog();
        if (log != null) {
            long startDate = log.getLogDay();
            long endDate = startDate + ONE_DAY_MS;
            mELDEventsInteractor.getELDEvents(startDate, endDate)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(eldEvents -> {
                                //TODO: delete this filter after fix problem with incorrect data from server
                                eldEvents = filterEventByStartTime(eldEvents, startDate, endDate);

                                eldEvents = filterEventByType(eldEvents, ELDEvent.EventType.DUTY_STATUS_CHANGING);
                                mTripInfo.setStartDayTime(DateUtils.getStartDayTimeInMs(eldEvents.get(0).getEventTime()));
                                mView.setTripInfo(mTripInfo);
                                mView.setELDEvents(eldEvents);
                                updateTripInfo(eldEvents);
                            },
                            throwable -> Timber.e(throwable.getMessage()));
        } else {
            mView.setELDEvents(Collections.EMPTY_LIST);
            mTripInfo = new TripInfo();
            mView.setTripInfo(mTripInfo);
        }
    }

    public void updateTripInfo(final List<ELDEvent> events) {
        TripInfo tripInfo = new TripInfo();

        Disposable disposable = Observable.create((ObservableOnSubscribe<TripInfo>) e -> {
            long[] result = new long[DutyType.values().length];
            int odometer = 0;
            for (int i = 1; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                ELDEvent prevEvent = events.get(i - 1);
                long logDate = event.getEventTime();
                long prevLogDate = prevEvent.getEventTime();
                long timeStamp = (logDate - prevLogDate);
                result[prevEvent.getEventCode() - 1] += timeStamp;

                if (event.getOdometer() != null && odometer < event.getOdometer()) {
                    odometer = event.getOdometer();
                }
            }

            tripInfo.setSleeperBerthTime(DateUtils.convertTimeInMsToStringTime(
                    result[DutyType.SLEEPER_BERTH.getId() - 1]));
            tripInfo.setDrivingTime(DateUtils.convertTimeInMsToStringTime(
                    result[DutyType.DRIVING.getId() - 1]));
            tripInfo.setOffDutyTime(DateUtils.convertTimeInMsToStringTime(
                    result[DutyType.OFF_DUTY.getId() - 1]));
            tripInfo.setOnDutyTime(DateUtils.convertTimeInMsToStringTime(
                    result[DutyType.ON_DUTY.getId() - 1]));

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

    //TODO: should be remove after fix problem with eld request.
    private List<ELDEvent> filterEventByStartTime(List<ELDEvent> events, long startTime, long endTime) {
        List<ELDEvent> result = new ArrayList<>();
        for (ELDEvent event : events) {
            if (event.getEventTime() > startTime && event.getEventTime() < endTime) {
                result.add(event);
            }
        }
        return result;
    }

    private List<ELDEvent> filterEventByType(List<ELDEvent> events, ELDEvent.EventType eventType) {
        List<ELDEvent> result = new ArrayList<>();
        for (ELDEvent event : events) {
            if (event.getEventType().equals(eventType.getValue())) {
                result.add(event);
            }
        }
        return result;
    }


    public void onSignLogsheetButtonClicked() {
    }

    public void onEditEventClicked(ELDEvent event) {
        mView.goToEditEventScreen(event);
    }

    public void onDeleteEventClicked(ELDEvent event) {
    }

    public void onAddEventClicked() {
        mView.goToAddEventScreen();
    }

    public void onEditTripInfoClicked() {
        mView.goToEditTripInfoScreen();
    }

    public void onDestroy() {
        mDisposables.dispose();
        Timber.d("DESTROYED");
    }
}
