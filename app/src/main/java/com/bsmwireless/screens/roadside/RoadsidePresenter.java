package com.bsmwireless.screens.roadside;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.LogSheetInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.logs.GraphModel;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;

@ActivityScope
public final class RoadsidePresenter {

    private RoadsideView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private LogSheetInteractor mLogSheetInteractor;
    private UserInteractor mUserInteractor;
    private ELDEventsInteractor mEventsInteractor;
    private Disposable mDateDisposable;

    @Inject
    public RoadsidePresenter(RoadsideView view,
                             UserInteractor userInteractor,
                             ELDEventsInteractor eventsInteractor,
                             VehiclesInteractor vehiclesInteractor,
                             LogSheetInteractor logSheetInteractor) {
        mUserInteractor = userInteractor;
        mEventsInteractor = eventsInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mLogSheetInteractor = logSheetInteractor;
        mDateDisposable = Disposables.disposed();
        mView = view;

        Timber.d("CREATED");
    }

    public void onDestroy() {
        mDateDisposable.dispose();
    }

    void onViewCreated() {
        onDateChanged(Calendar.getInstance());
    }

    public void onDateChanged(Calendar calendar) {
        mDateDisposable.dispose();
        mDateDisposable = mUserInteractor.getTimezoneOnce()
                .flatMap(timezone -> {
                    long startTime = DateUtils.getStartDate(timezone, calendar);
                    long endTime = startTime + MS_IN_DAY;

                    long logDay = DateUtils.convertTimeToLogDay(timezone, startTime);

                    int driverId = mUserInteractor.getDriverId();

                    return Single.zip(mEventsInteractor.getEventsFromDBOnce(startTime, endTime),
                            mLogSheetInteractor.getLogSheetHeadersFromDBOnce(logDay),
                            mEventsInteractor.getLatestActiveDutyEventFromDBOnce(startTime, driverId),
                            mEventsInteractor.getLatestActiveDutyEventFromDBOnce(endTime, driverId),
                            (events, header, prevEvents, lastEvents) -> {
                                ELDEvent prevDayEvent = null;
                                if (!prevEvents.isEmpty()) {
                                    prevDayEvent = prevEvents.get(prevEvents.size() - 1);
                                    prevDayEvent.setEventTime(startTime);
                                }

                                ELDEvent lastEvent = lastEvents.isEmpty() ? null : lastEvents.get(lastEvents.size() - 1);
                                Vehicle vehicle = lastEvent == null ? null : mVehiclesInteractor.getVehicle(lastEvent.getVehicleId());

                                RoadsideResult result = new RoadsideResult();
                                result.setHeadersData(mView.getHeadersData(header, lastEvent, vehicle));
                                result.setEventsData(mView.getEventsData(events));
                                GraphModel graphModel = new GraphModel();
                                graphModel.setPrevDayEvent(prevDayEvent);
                                graphModel.setStartDayTime(startTime);
                                graphModel.setEventLogModels(preparingLogs(events, startTime,
                                        Math.min(endTime, System.currentTimeMillis()), timezone));
                                result.setGraphData(graphModel);
                                result.setPreviousEvent(prevDayEvent);

                                return result;
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.showHeaders(result.getHeadersData());
                            mView.showEvents(result.getEventsData());
                            mView.showGraph(result.getGraphData());
                        },
                        throwable -> Timber.e(throwable)
                );
    }

    public User getUser(int driverId) {
        return UserConverter.toFullUser(mUserInteractor.getFullUserSync(driverId));
    }

    public List<User> getCoDrivers(String ids) {
        return UserConverter.toUserList(mUserInteractor.getUsersFromDBSync(ids));
    }

    public int getMalfunctionsCount(int driverId, long startDate, long endDate) {
        return mEventsInteractor.getMalfunctionCountSync(driverId, startDate, endDate);
    }

    public int getDiagnosticsCount(int driverId, long startDate, long endDate) {
        return mEventsInteractor.getDiagnosticCountSync(driverId, startDate, endDate);
    }

    private List<EventLogModel> preparingLogs(List<ELDEvent> events, long startDayTime, long endDayTime, String timezone) {
        List<EventLogModel> logs = new ArrayList<>();

        long lastTime = 0;
        for (int i = 0; i < events.size(); i++) {
            ELDEvent event = events.get(i);

            if (!(event.isActive() && event.isDutyEvent())) {
                continue;
            }

            EventLogModel log = new EventLogModel(event, timezone);
            log.setDutyType(DutyType.getTypeByCode(log.getEventType(), log.getEventCode()));

            if (logs.size() == 0) {
                if ((log.getEventTime() < startDayTime)) {
                    log.setEventTime(startDayTime);
                }
            } else {
                logs.get(logs.size() - 1).setDuration(log.getEventTime() - lastTime);
            }

            logs.add(log);
            lastTime = log.getEventTime();
        }

        if (!logs.isEmpty()) {
            //set duration for last event
            EventLogModel lastEvent = logs.get(logs.size() - 1);
            lastEvent.setDuration(endDayTime - lastEvent.getEventTime());
        }

        return logs;
    }

    private static final class RoadsideResult {
        private List<String> mHeadersData;
        private List<String> mEventsData;
        private GraphModel mGraphData;
        private ELDEvent mPreviousEvent;

        List<String> getHeadersData() {
            return mHeadersData;
        }

        void setHeadersData(List<String> headersData) {
            mHeadersData = headersData;
        }

        List<String> getEventsData() {
            return mEventsData;
        }

        void setEventsData(List<String> eventsData) {
            mEventsData = eventsData;
        }

        GraphModel getGraphData() {
            return mGraphData;
        }

        void setGraphData(GraphModel graphData) {
            mGraphData = graphData;
        }

        ELDEvent getPreviousEvent() {
            return mPreviousEvent;
        }

        void setPreviousEvent(ELDEvent previousEvent) {
            mPreviousEvent = previousEvent;
        }
    }
}
