package com.bsmwireless.screens.logs;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

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

    private LogsView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public LogsPresenter(LogsView view, ELDEventsInteractor interactor) {
        mView = view;
        mELDEventsInteractor = interactor;
        mDisposables = new CompositeDisposable();
        Timber.d("CREATED");
    }

    public void onViewCreated() {
        setupViewForDay(null);
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
            ArrayList<Long>[] times = new ArrayList[DutyType.values().length];
            for (int i = 0; i < times.length; i++) {
                times[i] = new ArrayList<>();
            }
            for (ELDEvent event:
                    events) {
                if (!event.getEventType().equals(ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue())) {
                    continue;
                }
                Long eventTime = event.getEventTime();
                DutyType type = DutyType.getTypeById(event.getEventCode());
                times[type.getId() - 1].add(eventTime);
            }

            long[] result = new long[DutyType.values().length];
            for (int i = 0; i < times.length; i++) {
                List<Long> time = times[i];
                Collections.sort(time);
                if (time.size() > 0) {
                    result[i] = Math.abs(time.get(0) - times[i].get(time.size() - 1));
                }
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
}
