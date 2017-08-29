package com.bsmwireless.screens.navigation;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
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

import static com.bsmwireless.common.utils.DateUtils.MS_IN_DAY;

public class NavigationPresenter extends BaseMenuPresenter {

    private NavigateView mView;
    private UserInteractor mUserInteractor;
    private VehiclesInteractor mVehiclesInteractor;

    @Inject
    public NavigationPresenter(NavigateView view, UserInteractor userInteractor, VehiclesInteractor vehiclesInteractor, ELDEventsInteractor eventsInteractor, DutyManager dutyManager) {
        mView = view;
        mUserInteractor = userInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mEventsInteractor = eventsInteractor;
        mDutyManager = dutyManager;
        mDisposables = new CompositeDisposable();
    }

    public void onLogoutItemSelected() {
        Disposable disposable = mUserInteractor.logoutUser()
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

                    //TODO: get HOS from server instead of checking events manually
                    return mEventsInteractor.getELDEvents(time[0] - MS_IN_DAY, time[1]);
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        events -> resetTime(events, time[0]),
                        error -> {
                            mDutyManager.setDutyTypeTime(0, 0, 0, DutyType.OFF_DUTY);
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

        long[] times = DutyManager.getDutyTypeTimes(new ArrayList<>(events), startOfDay, System.currentTimeMillis());

        mDutyManager.setDutyTypeTime(
                (int) (times[DutyType.ON_DUTY.ordinal()]),
                (int) (times[DutyType.DRIVING.ordinal()]),
                (int) (times[DutyType.SLEEPER_BERTH.ordinal()]), dutyType);
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
