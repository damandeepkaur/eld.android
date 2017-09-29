package com.bsmwireless.screens.hoursofservice;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public final class HoursOfServicePresenter extends BaseMenuPresenter {

    private Disposable mResetTimeDisposable;
    private final HoursOfServiceView mView;

    @Inject
    public HoursOfServicePresenter(DutyTypeManager dutyTypeManager,
                                   ELDEventsInteractor eventsInteractor,
                                   UserInteractor userInteractor,
                                   AccountManager mAccountManager,
                                   HoursOfServiceView view) {
        super(dutyTypeManager, eventsInteractor, userInteractor, mAccountManager);
        mView = view;
        mResetTimeDisposable = Disposables.disposed();
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    @Override
    public void onUserChanged() {
        super.onUserChanged();
        onResetTime();
    }

    public void onResetTime() {
        //start and end time
        long[] time = new long[2];

        mResetTimeDisposable.dispose();
        mResetTimeDisposable = getUserInteractor().getTimezone()
                .flatMap(timeZone -> {
                    long current = System.currentTimeMillis();
                    time[0] = DateUtils.getStartDayTimeInMs(timeZone, current);
                    time[1] = DateUtils.getEndDayTimeInMs(timeZone, current);

                    mView.setResetTime(time[1]);

                    return getEventsInteractor()
                            .getDutyEventsFromDB(time[0], time[1])
                            .map(selectedDayEvents -> {
                                List<ELDEvent> prevDayLatestEvents = getEventsInteractor()
                                        .getLatestActiveDutyEventFromDBSync(time[0], getUserInteractor().getUserId());
                                if (!prevDayLatestEvents.isEmpty()) {
                                    selectedDayEvents.add(0, prevDayLatestEvents.get(prevDayLatestEvents.size() - 1));
                                }
                                return selectedDayEvents;
                            });
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        events -> resetTime(events, time[0]),
                        error -> {
                            getDutyTypeManager().setDutyTypeTime(0, 0, 0, DutyType.OFF_DUTY);
                            Timber.e("Get timezone error: %s", error);
                        }
                );
    }

    private void resetTime(List<ELDEvent> events, long startOfDay) {
        DutyType dutyType = DutyType.OFF_DUTY;
        DutyType eventDutyType;
        DutyType lastEventType = null;
        ELDEvent event;

        boolean isClear = false;

        for (int i = events.size() - 1; i >= 0; i--) {
            event = events.get(i);

            if (event.isDutyEvent() && event.isActive()) {
                eventDutyType = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());

                if (lastEventType == null) {
                    lastEventType = eventDutyType;
                }

                if (eventDutyType == DutyType.CLEAR) {
                    isClear = true;

                    //find previous duty event with actual status
                } else if (isClear) {
                    switch (eventDutyType) {
                        case PERSONAL_USE:
                            dutyType = DutyType.OFF_DUTY;
                            break;

                        case YARD_MOVES:
                            dutyType = DutyType.ON_DUTY;
                            break;

                        default:
                            dutyType = eventDutyType;
                            break;
                    }
                    break;

                } else {
                    dutyType = eventDutyType;
                    break;
                }
            }
        }

        long[] times = DutyTypeManager.getDutyTypeTimes(new ArrayList<>(events), startOfDay, System.currentTimeMillis());

        getDutyTypeManager().setDutyTypeTime(
                (int) (times[DutyType.ON_DUTY.ordinal()]),
                (int) (times[DutyType.DRIVING.ordinal()]),
                (int) (times[DutyType.SLEEPER_BERTH.ordinal()]), dutyType
        );

        getDutyTypeManager().setDutyType(lastEventType == null ? DutyType.OFF_DUTY : lastEventType, false);
    }
}
