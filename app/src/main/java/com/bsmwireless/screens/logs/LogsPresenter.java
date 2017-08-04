package com.bsmwireless.screens.logs;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.inject.Inject;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.screens.logs.TripInfo.UnitType.KM;
import static dagger.internal.InstanceFactory.create;

@ActivityScope
public class LogsPresenter {
    public static final int ONE_DAY_MS = 24 * 60 * 60 * 1000;
    private final static int SEC_IN_MIN = 60;
    private final static int MS_IN_SEC = 1000;

    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor interactor, LogSheetInteractor logSheetInteractor) {
        mView = view;
        mELDEventsInteractor = interactor;
        mLogSheetInteractor = logSheetInteractor;
        mDisposables = new CompositeDisposable();
        Timber.d("CREATED");
    }

    public void onViewCreated() {
        setupViewForDay(null);

        SimpleDateFormat dateFormat = new SimpleDateFormat( "MMdd" );
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Date monthAgo = new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)-1, calendar.get(Calendar.DATE));

        String todayDate = calendar.get(Calendar.YEAR) + dateFormat.format(today);
        String monthAgoDate = calendar.get(Calendar.YEAR) + dateFormat.format(monthAgo);

        long todayDayLong = Long.parseLong(todayDate);
        long monthAgoLong = Long.parseLong(monthAgoDate);

        mDisposables.add(mLogSheetInteractor.syncLogSheetHeader(todayDayLong, monthAgoLong)
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
        //TODO update data for new day
        LogSheetHeader log = calendarItem.getAssociatedLog();
        if (log != null) {
            long startDate = log.getStartOfDay();
            long endDate = startDate + 24 * 60 * 60 * 1000;
            mDisposables.add(mELDEventsInteractor.getELDEvents(startDate, endDate)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(eldEvents -> mView.setELDEvents(eldEvents),
                                        throwable -> Timber.e(throwable.getMessage())));
        }
    }

    public void onSignLogsheetButtonClicked() {
    }

    public void onEditEventClicked(ELDEvent event) {
    }

    public void onDeleteEventClicked(ELDEvent event) {
    }

    public void onAddEventClicked() {
    }

    public void onEditTripInfoClicked() {
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }

    public void calculateTripTime(List<ELDEvent> events) {
        Disposable disposable = io.reactivex.Observable.create((ObservableOnSubscribe<long[]>) e -> {
            List<ELDEvent> filteredEvent = filterEventBytType(events, ELDEvent.EventType.DUTY_STATUS_CHANGING);

            long[] result = new long[DutyType.values().length];

            for (int i = 1; i < filteredEvent.size(); i++) {
                ELDEvent event = filteredEvent.get(i);
                ELDEvent prevEvent = filteredEvent.get(i - 1);

                long logDate = event.getEventTime();
                long prevLogDate = prevEvent.getEventTime();

                long timeStamp = (logDate - prevLogDate);

                result[prevEvent.getEventCode() - 1] += timeStamp;
            }

            e.onNext(result);
        })
                                                       .subscribeOn(Schedulers.io())
                                                       .observeOn(AndroidSchedulers.mainThread())
                                                       .subscribe(times -> mView.setTime(times), throwable -> Timber.e(throwable.getMessage()));
        mDisposables.add(disposable);
    }

    private void setupViewForDay(CalendarItem item) {
        //TODO: request data from DB instead of mock
        mView.setELDEvents(makeELDEvents());
        mView.setTripInfo(makeTripInfo());
        mView.setLogSheetHeaders(makeLogSheetHeaders());
    }

    private List<ELDEvent> makeELDEvents() {
        List<ELDEvent> list = new ArrayList<>();
        int length = 24 * 60;

        for (int i = 0; i < length; i++) {
            ELDEvent t = new ELDEvent();
            t.setId(i);
            if (i < length / 4) {
                t.setEventType(101);
            } else if (i >= length / 4 && i < length / 2) {
                t.setEventType(103);
            } else if (i >= length / 2 && i < 3 * length / 4) {
                t.setEventType(102);
            } else if (i >= 3 * length / 4) {
                t.setEventType(104);
            }
            t.setEventTime(Calendar.getInstance().getTimeInMillis() + i * 60_000);
            list.add(t);
        }
        return list;
    }

    private List<LogSheetHeader> makeLogSheetHeaders() {
        Calendar calendar = Calendar.getInstance();
        List<LogSheetHeader> logs = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LogSheetHeader log = new LogSheetHeader();
            Long time = calendar.getTime().getTime();
            log.setLogDay(time);
            log.setStartOfDay(ViewUtils.getStartDate("America/Los_Angeles", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)));
            if (i % 7 != 0) {
                logs.add(log);
            }
            time -= 24 * 60 * 60 * 1000;
            calendar.setTime(new Date(time));
        }
        return logs;
    }

    private TripInfo makeTripInfo() {
        TripInfo tripInfo = new TripInfo();
        tripInfo.setCoDriverValue("-");
        tripInfo.setOnDutyLeftValue("02:30");
        tripInfo.setDriveValue("04:00");
        tripInfo.setOdometerValue(666);
        tripInfo.setUnitType(KM);
        return tripInfo;
    }

    private List<ELDEvent> filterEventBytType(List<ELDEvent> events, ELDEvent.EventType eventType) {
        List<ELDEvent> result = new ArrayList<>();
        for (ELDEvent event:
                events) {
            if (event.getEventType().equals(eventType.getValue())) {
                result.add(event);
            }
        }
        return result;
    }
}
