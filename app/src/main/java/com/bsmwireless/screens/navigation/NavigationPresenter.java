package com.bsmwireless.screens.navigation;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.SyncEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NavigationPresenter extends BaseMenuPresenter {

    private NavigateView mView;
    private UserInteractor mUserInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private SyncEventsInteractor mSyncEventsInteractor;
    private AutoDutyTypeManager mAutoDutyTypeManager;

    private AutoDutyTypeManager.AutoDutyTypeListener mListener = new AutoDutyTypeManager.AutoDutyTypeListener() {
        @Override
        public void onAutoOnDuty() {
            mView.setAutoOnDuty();
        }

        @Override
        public void onAutoDriving() {
            mView.setAutoDriving();
        }

        @Override
        public void onAutoDrivingWithoutConfirm() {
            mView.setAutoDrivingWithoutConfirm();
        }
    };

    @Inject
    public NavigationPresenter(NavigateView view, UserInteractor userInteractor, VehiclesInteractor vehiclesInteractor, ELDEventsInteractor eventsInteractor,
                               DutyTypeManager dutyTypeManager, AutoDutyTypeManager autoDutyTypeManager, SyncEventsInteractor syncEventsInteractor) {
        mView = view;
        mUserInteractor = userInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mEventsInteractor = eventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mAutoDutyTypeManager = autoDutyTypeManager;
        mSyncEventsInteractor = syncEventsInteractor;
        mDisposables = new CompositeDisposable();

        mAutoDutyTypeManager.setListener(mListener);
    }

    public void onLogoutItemSelected() {
        Disposable disposable = mEventsInteractor.postLogoutEvent()
                .doOnNext(isSuccess -> mUserInteractor.deleteUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                mView.goToLoginScreen();
                            } else {
                                mView.showErrorMessage("Logout failed");
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            mView.showErrorMessage("Exception:" + error.toString());
                        }
                );
        mDisposables.add(disposable);

    }

    public void onViewCreated() {
        mDisposables.add(mUserInteractor.getFullName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(name -> mView.setDriverName(name)));
        mView.setCoDriversNumber(mUserInteractor.getCoDriversNumber());
        mView.setBoxId(mVehiclesInteractor.getBoxId());
        mView.setAssetsNumber(mVehiclesInteractor.getAssetsNumber());
        mAutoDutyTypeManager.validateBlackBoxState();
        mSyncEventsInteractor.startSync();
    }

    public void onResetTime() {
        //start and end time
        long[] time = new long[2];

        mDisposables.add(mUserInteractor.getTimezone()
                .flatMap(timeZone -> {
                    long current = System.currentTimeMillis();
                    time[0] = DateUtils.getStartDayTimeInMs(timeZone, current);
                    time[1] = DateUtils.getEndDayTimeInMs(timeZone, current);

                    mView.setResetTime(time[1]);

                    return mEventsInteractor.getDutyEventsFromDB(time[0], time[1])
                            .map(selectedDayEvents -> {
                                List<ELDEvent> prevDayLatestEvents = mEventsInteractor.getLatestActiveDutyEventFromDB(time[0]);
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
                            mDutyTypeManager.setDutyTypeTime(0, 0, 0, DutyType.OFF_DUTY);
                            Timber.e("Get timezone error: %s", error);
                        }
                ));
    }

    private void resetTime(List<ELDEvent> events, long startOfDay) {
        DutyType dutyType = DutyType.OFF_DUTY;
        DutyType eventDutyType;
        ELDEvent event;

        boolean isClear = false;

        for (int i = events.size() - 1; i >= 0; i--) {
            event = events.get(i);

            //TODO: remove if when request is updated
            if (event.getEventType() == ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue() || event.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) {
                eventDutyType = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());

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

        mDutyTypeManager.setDutyTypeTime(
                (int) (times[DutyType.ON_DUTY.ordinal()]),
                (int) (times[DutyType.DRIVING.ordinal()]),
                (int) (times[DutyType.SLEEPER_BERTH.ordinal()]), dutyType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSyncEventsInteractor.stopSync();
        mAutoDutyTypeManager.removeListener();
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    public void onUserUpdated(User user) {
        if (user != null) {
            mDisposables.add(mUserInteractor.syncDriverProfile(user)
                                                 .subscribeOn(Schedulers.io())
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .subscribe(userUpdated -> {},
                                                            throwable -> mView.showErrorMessage(throwable.getMessage())));
        }
    }
}
