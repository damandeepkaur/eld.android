package com.bsmwireless.screens.logs;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.screens.logs.TripInfo.UnitType.KM;

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
