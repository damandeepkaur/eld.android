package com.bsmwireless.screens.carrieredit.fragments.edited;

import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_PU;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_YM;

/**
 * Created by osminin on 22.09.2017.
 */

public final class EditedEventsPresenter {

    private final ELDEventsInteractor mELDEventsInteractor;
    private final LogSheetInteractor mLogSheetInteractor;
    private final UserInteractor mUserInteractor;
    private final ServiceApi mServiceApi;
    private final CompositeDisposable mDisposable;
    private EditedEventsView mView;
    private String mTimeZone;
    private User mUser;

    @Inject
    public EditedEventsPresenter(ELDEventsInteractor eldEventsInteractor,
                                 LogSheetInteractor logSheetInteractor, UserInteractor userInteractor,
                                 ServiceApi serviceApi) {
        mELDEventsInteractor = eldEventsInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mUserInteractor = userInteractor;
        mServiceApi = serviceApi;
        mDisposable = new CompositeDisposable();
    }

    public void setView(EditedEventsView view) {
        mView = view;
    }

    public void fetchEldEvents() {
        Timber.v("fetchEldEvents: ");
        mDisposable.add(Observable.zip(mELDEventsInteractor.getUnidentifiedEvents(),
                mUserInteractor.getFullUser().toObservable(), (events, user) -> {
                    mUser = user;
                    mTimeZone = user.getTimezone();
                    return events;
                })
                .subscribeOn(Schedulers.io())
                .map(this::preparingLogs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> mView.setEvents(res), Timber::e));
    }

    private List<EventLogModel> preparingLogs(List<ELDEvent> events) {
        List<EventLogModel> logs = new ArrayList<>();

        if (!events.isEmpty()) {
            //convert to logs model
            long duration;
            long startDayTime = Long.MAX_VALUE;
            long endDayTime = -1;
            for (int i = 0; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                EventLogModel log = new EventLogModel(event, mTimeZone);
                if (event.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()
                        && event.getEventCode() == DutyType.CLEAR.getCode()) {
                    log.setDutyType(DutyType.CLEAR);
                    //get code of indication ON event for indication OFF event
                    for (int j = i - 1; j >= 0; j--) {
                        ELDEvent dutyEvent = events.get(j);
                        long time = event.getEventTime();
                        if (time < startDayTime) {
                            startDayTime = time;
                        }
                        if (time > endDayTime) {
                            endDayTime = time;
                        }

                        if (dutyEvent.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
                            if (dutyEvent.getEventCode() == DutyType.PERSONAL_USE.getCode()) {
                                log.setDutyType(CLEAR_PU);
                                break;
                            } else if (dutyEvent.getEventCode() == DutyType.YARD_MOVES.getCode()) {
                                log.setDutyType(CLEAR_YM);
                                break;
                            }
                        }
                    }
                } else {
                    log.setDutyType(DutyType.getTypeByCode(log.getEventType(), log.getEventCode()));
                }
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

    /*private List<EventLogModel> preparingLogs(List<ELDEvent> events) {
        Timber.v("preparingLogs: ");
        List<EventLogModel> logs = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        if (!events.isEmpty()) {
            //convert to logs model
            long duration;
            for (int i = 0; i < events.size(); i++) {
                ELDEvent event = events.get(i);
                long startDayTime = DateUtils.getStartDate(event.getTimezone(), calendar);
                long endDayTime = startDayTime + MS_IN_DAY;
                EventLogModel log = new EventLogModel(event, event.getTimezone());
                if (event.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()
                        && event.getEventCode() == DutyType.CLEAR.getCode()) {
                    log.setDutyType(DutyType.CLEAR);
                    //get code of indication ON event for indication OFF event
                    for (int j = i - 1; j >= 0; j--) {
                        ELDEvent dutyEvent = events.get(j);

                        if (dutyEvent.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
                            if (dutyEvent.getEventCode() == DutyType.PERSONAL_USE.getCode()) {
                                log.setDutyType(CLEAR_PU);
                                break;
                            } else if (dutyEvent.getEventCode() == DutyType.YARD_MOVES.getCode()) {
                                log.setDutyType(CLEAR_YM);
                                break;
                            }
                        }
                    }
                } else {
                    log.setDutyType(DutyType.getTypeByCode(log.getEventType(), log.getEventCode()));
                }
                logs.add(log);
                if (logs.get(0).getEventTime() < startDayTime) {
                    logs.get(0).setEventTime(startDayTime);
                }
                if (i < events.size() - 1) {
                    duration = events.get(i + 1).getEventTime() - events.get(i).getEventTime();
                    logs.get(i).setDuration(duration);
                }
            }
        }
        return logs;
    }*/
}
