package com.bsmwireless.screens.carrieredit.fragments.unassigned;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.DutyUtils;
import com.bsmwireless.data.network.ServiceApi;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_PU;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR_YM;

public final class UnassignedEventsPresenterImpl implements UnassignedEventsPresenter {

    private final ELDEventsInteractor mELDEventsInteractor;
    private final ServiceApi mServiceApi;
    private final CompositeDisposable mDisposable;
    private Disposable mUpdateEventDisposable = Disposables.disposed();
    private UnassignedEventsView mView;

    @Inject
    public UnassignedEventsPresenterImpl(ELDEventsInteractor ELDEventsInteractor, ServiceApi serviceApi) {
        Timber.v("UnassignedEventsPresenterImpl: ");
        mServiceApi = serviceApi;
        mELDEventsInteractor = ELDEventsInteractor;
        mDisposable = new CompositeDisposable();
    }

    public void setView(UnassignedEventsView view) {
        Timber.v("setView: ");
        mView = view;
    }

    public void fetchEldEvents() {
        Timber.v("fetchEldEvents: ");
        mDisposable.add(mELDEventsInteractor.getUnassignedEvents()
                .subscribeOn(Schedulers.io())
                .map(this::preparingLogs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> mView.setEvents(res), Timber::e));
    }

    public void acceptEvent(EventLogModel event, int driverId, int position) {
        if (!mUpdateEventDisposable.isDisposed()) {
            return;
        }
        Timber.v("acceptEvent: ");
        List<ELDEvent> eldEvents = new ArrayList<>();
        event.getEvent().setDriverId(driverId);
        eldEvents.add(event.getEvent());
        mUpdateEventDisposable = mServiceApi.updateELDEvents(eldEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseMessage -> {
                    updateDb(eldEvents, position);
                    mUpdateEventDisposable.dispose();
                }, throwable -> {
                    Timber.e(throwable);
                    mDisposable.dispose();
                });
    }

    public void rejectEvent(EventLogModel event, int position) {
        //TODO: remove from db?
        Timber.v("rejectEvent: ");
        mView.removeEvent(position);
    }

    private void updateDb(List<ELDEvent> eldEvents, int position) {
        Timber.v("updateDb: ");
        mDisposable.add(mELDEventsInteractor.updateELDEvents(eldEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> mView.removeEvent(position), Timber::e));
    }

    public void dispose() {
        Timber.v("dispose: ");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (!mUpdateEventDisposable.isDisposed()) {
            mUpdateEventDisposable.dispose();
        }
    }

    private List<EventLogModel> preparingLogs(List<ELDEvent> events) {
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
                    log.setType(DutyType.CLEAR);
                    //get code of indication ON event for indication OFF event
                    for (int j = i - 1; j >= 0; j--) {
                        ELDEvent dutyEvent = events.get(j);

                        if (dutyEvent.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
                            if (dutyEvent.getEventCode() == DutyType.PERSONAL_USE.getCode()) {
                                log.setType(CLEAR_PU);
                                break;
                            } else if (dutyEvent.getEventCode() == DutyType.YARD_MOVES.getCode()) {
                                log.setType(CLEAR_YM);
                                break;
                            }
                        }
                    }
                } else {
                    log.setType(DutyUtils.getTypeByCode(log.getEventType(), log.getEventCode()));
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
    }
}
