package com.bsmwireless.screens.logs;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    public static final int ONE_DAY_MS = 24 * 60 * 60 * 1000;

    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor eventsInteractor, LogSheetInteractor logSheetInteractor, VehiclesInteractor vehiclesInteractor) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mDisposables = new CompositeDisposable();
        Timber.d("CREATED");
    }

    public void onViewCreated() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Date monthAgo = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, calendar.get(Calendar.DATE));

        String todayDate = calendar.get(Calendar.YEAR) + dateFormat.format(today);
        String monthAgoDate = calendar.get(Calendar.YEAR) + dateFormat.format(monthAgo);

        long todayDateLong = Long.parseLong(todayDate);
        long monthAgoLong = Long.parseLong(monthAgoDate);

        mDisposables.add(mLogSheetInteractor.syncLogSheetHeader(monthAgoLong, todayDateLong)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        logSheetHeaders -> {
                            for (LogSheetHeader header : logSheetHeaders) {
                                header.setLogDay(convertTimeToUnixMs(header.getLogDay()));
                            }
                            mView.setLogSheetHeaders(logSheetHeaders);
                        },
                        error -> Timber.e("LoginUser error: %s", error)
                ));
    }

    private long convertTimeToUnixMs(long logday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(logday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
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
                                eldEvents = filterEventByStartTime(eldEvents, startDate);

                                eldEvents = filterEventByType(eldEvents, ELDEvent.EventType.DUTY_STATUS_CHANGING);
                                mView.setELDEvents(eldEvents);
                                setupTripInfo(eldEvents);
                            },
                            throwable -> Timber.e(throwable.getMessage()));
        } else {
            mView.setELDEvents(Collections.EMPTY_LIST);
        }
    }

    public void setupTripInfo(List<ELDEvent> events) {
        TripInfo tripInfo = new TripInfo();
        Disposable disposable = Observable.create((ObservableOnSubscribe<long[]>) e -> {
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

            tripInfo.setSleeperBerthTime(ViewUtils.convertTimeInMsToStringTime(
                    result[DutyType.SLEEPER_BERTH.getId() - 1]));
            tripInfo.setDrivingTime(ViewUtils.convertTimeInMsToStringTime(
                    result[DutyType.DRIVING.getId() - 1]));
            tripInfo.setOffDutyTime(ViewUtils.convertTimeInMsToStringTime(
                    result[DutyType.OFF_DUTY.getId() - 1]));
            tripInfo.setOnDutyTime(ViewUtils.convertTimeInMsToStringTime(
                    result[DutyType.ON_DUTY.getId() - 1]));

            //TODO: convert odometer value from meters to appropriate unit
            tripInfo.setUnitType(KM);
            tripInfo.setOdometerValue(odometer / 1000);

            e.onNext(result);
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(times -> mView.setTripInfo(tripInfo),
                        throwable -> Timber.e(throwable.getMessage()));

        mDisposables.add(disposable);
    }

    //TODO: should be remove after fix problem with eld request.
    private List<ELDEvent> filterEventByStartTime(List<ELDEvent> events, long startTime) {
        List<ELDEvent> result = new ArrayList<>();
        for (ELDEvent event : events) {
            if (event.getEventTime() > startTime) {
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
