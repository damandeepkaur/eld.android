package com.bsmwireless.screens.multiday;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.DutyUtils;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class MultidayPresenter {

    public static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;

    private MultidayView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private LoginUserInteractor mLoginUserInteractor;
    private CompositeDisposable mDisposables;
    private Disposable mGetEventDisposable;

    private String mTimeZone;

    @Inject
    public MultidayPresenter(MultidayView view, ELDEventsInteractor eventsInteractor, LoginUserInteractor userInteractor) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mLoginUserInteractor = userInteractor;
        mDisposables = new CompositeDisposable();
        mTimeZone = TimeZone.getDefault().getID();

        Timber.d("CREATED");
    }

    public void onDestroy() {
        if (mGetEventDisposable != null) {
            mGetEventDisposable.dispose();
        }
        mDisposables.dispose();
    }

    public void onViewCreated() {
        mDisposables.add(mLoginUserInteractor.getTimezone()
                                             .subscribeOn(Schedulers.io())
                                             .subscribe(timezone -> {
                                                 mTimeZone = timezone;
                                                 getItems(mView.getDayCount());
                                             }, Timber::e));
    }

    public void getItems(int dayCount) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(mTimeZone));

        long endDayTime = calendar.getTimeInMillis();
        calendar.setTimeInMillis(endDayTime - (dayCount - 1) * ONE_DAY_MS);
        long startDayTime = DateUtils.getStartDate(mTimeZone, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));


        mELDEventsInteractor.syncELDEvents(startDayTime, endDayTime);
        if (mGetEventDisposable != null) {
            mGetEventDisposable.dispose();
        }

        mGetEventDisposable = mELDEventsInteractor.getELDEventsFromDB(startDayTime, endDayTime)
                                                  .subscribeOn(Schedulers.io())
                                                  .map(events -> DutyUtils.filterELDEventsByTypeAndStatus(events, ELDEvent.EventType.DUTY_STATUS_CHANGING, ELDEvent.StatusCode.ACTIVE))
                                                  .flatMap(eldEvents -> Flowable.fromCallable(() -> getMultidayItems(dayCount, startDayTime, eldEvents)))
                                                  .observeOn(AndroidSchedulers.mainThread())
                                                  .subscribe(items -> {
                                                      mView.setItems(items);

                                                      long[] totalDurations = calculateTotalDuration(items);

                                                      mView.setTotalOnDuty(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.ON_DUTY.ordinal()]));
                                                      mView.setTotalSleeping(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.SLEEPER_BERTH.ordinal()]));
                                                      mView.setTotalDriving(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.DRIVING.ordinal()]));
                                                      mView.setTotalOffDuty(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.OFF_DUTY.ordinal()]));
                                                  }, Timber::e);
    }

    private List<MultidayItemModel> getMultidayItems(int dayCount, long startTime, List<ELDEvent> dutyEvents) {
        List<MultidayItemModel> items = new ArrayList<>();
        for (int i = dayCount - 1; i >= 0; i--) {
            long startDay = startTime + i * ONE_DAY_MS;
            long endDay = startDay + ONE_DAY_MS;

            List<ELDEvent> dayEvents = DutyUtils.filterEventsByTime(dutyEvents, startDay, endDay);

            MultidayItemModel item = new MultidayItemModel(startDay);

            long[] durations = calculateDurations(dayEvents, endDay);

            item.setTotalOffDuty(durations[DutyType.OFF_DUTY.ordinal()]);
            item.setTotalSleeping(durations[DutyType.SLEEPER_BERTH.ordinal()]);
            item.setTotalDriving(durations[DutyType.DRIVING.ordinal()]);
            item.setTotalOnDuty(durations[DutyType.ON_DUTY.ordinal()]);
            item.setDay(DateUtils.convertTimeInMsToDate(mTimeZone, startDay));

            items.add(item);
        }
        return items;
    }

    private long[] calculateDurations(List<ELDEvent> events, long endTime) {
        long[] result = new long[DutyType.values().length];

        if (events.isEmpty()) {
            return result;
        }

        for (int i = 1; i < events.size(); i++) {
            ELDEvent log = events.get(i);
            ELDEvent prevLog = events.get(i - 1);

            long logDate = log.getEventTime();
            long prevLogDate = prevLog.getEventTime();
            long timeStamp = logDate - prevLogDate;

            result[prevLog.getEventCode() - 1] += timeStamp;
        }

        ELDEvent log = events.get(events.size() - 1);

        long logDate = log.getEventTime();
        long timeStamp = endTime - logDate;

        result[log.getEventCode() - 1] += timeStamp;

        return result;
    }

    private long[] calculateTotalDuration(List<MultidayItemModel> items) {
        long[] result = new long[DutyType.values().length];
        for (MultidayItemModel item: items) {
            result[DutyType.OFF_DUTY.ordinal()] += item.getTotalOffDutyTime();
            result[DutyType.SLEEPER_BERTH.ordinal()] += item.getTotalSleepingTime();
            result[DutyType.DRIVING.ordinal()] += item.getTotalDrivingTime();
            result[DutyType.ON_DUTY.ordinal()] += item.getTotalOnDutyTime();
        }
        return result;
    }
}
