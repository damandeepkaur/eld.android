package com.bsmwireless.screens.multiday;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;

@ActivityScope
public class MultidayPresenter {
    private MultidayView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private UserInteractor mUserInteractor;
    private CompositeDisposable mDisposables;
    private Disposable mGetEventDisposable;

    private String mTimeZone;

    @Inject
    public MultidayPresenter(MultidayView view, ELDEventsInteractor eventsInteractor, UserInteractor userInteractor) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mUserInteractor = userInteractor;
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
        mDisposables.add(mUserInteractor.getTimezone()
                .subscribeOn(Schedulers.io())
                .subscribe(timezone -> {
                    if (!mTimeZone.equals(timezone)) {
                        mTimeZone = timezone;
                        getItems(mView.getDayCount());
                    }
                }, Timber::e));
    }

    public void getItems(int dayCount) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(mTimeZone));

        long endDayTime = calendar.getTimeInMillis();
        calendar.setTimeInMillis(endDayTime - (dayCount - 1) * MS_IN_DAY);
        long startDayTime = DateUtils.getStartDate(mTimeZone, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));


        mELDEventsInteractor.syncELDEventsWithServer(startDayTime, endDayTime);

        if (mGetEventDisposable != null) {
            mGetEventDisposable.dispose();
        }

        mGetEventDisposable = Observable.fromCallable(() -> getMultidayItems(dayCount, startDayTime))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(items -> mView.setItems(items))
                .map(this::calculateTotalDuration)
                .subscribe(totalDurations -> {
                    mView.setTotalOnDuty(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.ON_DUTY.ordinal()]));
                    mView.setTotalSleeping(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.SLEEPER_BERTH.ordinal()]));
                    mView.setTotalDriving(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.DRIVING.ordinal()]));
                    mView.setTotalOffDuty(DateUtils.convertTotalTimeInMsToStringTime(totalDurations[DutyType.OFF_DUTY.ordinal()]));
                }, Timber::e);
    }

    private List<MultidayItemModel> getMultidayItems(int dayCount, long startTime) {
        List<MultidayItemModel> items = new ArrayList<>();
        for (int i = dayCount - 1; i >= 0; i--) {
            long startDay = startTime + i * MS_IN_DAY;
            long endDay = Math.min(startDay + MS_IN_DAY, System.currentTimeMillis());

            List<ELDEvent> dayEvents = mELDEventsInteractor.getActiveEventsFromDBSync(startDay, endDay);

            List<ELDEvent> prevEvents = mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(startDay);
            if (!prevEvents.isEmpty()) {
                ELDEvent prevEvent = prevEvents.get(prevEvents.size() - 1);
                prevEvent.setEventTime(startDay);
                dayEvents.add(0, prevEvent);
            }

            MultidayItemModel item = new MultidayItemModel(startDay);

            long[] durations = DutyManager.getDutyTypeTimes(new ArrayList<>(dayEvents), startDay, endDay);

            item.setTotalOffDuty(durations[DutyType.OFF_DUTY.ordinal()]);
            item.setTotalSleeping(durations[DutyType.SLEEPER_BERTH.ordinal()]);
            item.setTotalDriving(durations[DutyType.DRIVING.ordinal()]);
            item.setTotalOnDuty(durations[DutyType.ON_DUTY.ordinal()]);
            item.setDay(DateUtils.convertTimeInMsToDate(mTimeZone, startDay));

            items.add(item);
        }
        return items;
    }

    private long[] calculateTotalDuration(List<MultidayItemModel> items) {
        long[] result = new long[4];
        for (MultidayItemModel item : items) {
            result[DutyType.OFF_DUTY.ordinal()] += item.getTotalOffDutyTime();
            result[DutyType.SLEEPER_BERTH.ordinal()] += item.getTotalSleepingTime();
            result[DutyType.DRIVING.ordinal()] += item.getTotalDrivingTime();
            result[DutyType.ON_DUTY.ordinal()] += item.getTotalOnDutyTime();
        }
        return result;
    }
}
