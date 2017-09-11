package com.bsmwireless.screens.navigation;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
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

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NavigationPresenter extends BaseMenuPresenter {

    private NavigateView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private Disposable mResetTimeDisposable;
    private SyncEventsInteractor mSyncEventsInteractor;
    private AutoDutyTypeManager mAutoDutyTypeManager;
    private AccountManager mAccountManager;

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
    public NavigationPresenter(NavigateView view,
                               UserInteractor userInteractor,
                               VehiclesInteractor vehiclesInteractor,
                               ELDEventsInteractor eventsInteractor,
                               DutyTypeManager dutyTypeManager,
                               AutoDutyTypeManager autoDutyTypeManager,
                               SyncEventsInteractor syncEventsInteractor,
                               AccountManager accountManager) {
        super(dutyTypeManager, eventsInteractor, userInteractor);
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mAutoDutyTypeManager = autoDutyTypeManager;
        mSyncEventsInteractor = syncEventsInteractor;
        mResetTimeDisposable = Disposables.disposed();
        mAccountManager = accountManager;

        mAutoDutyTypeManager.setListener(mListener);
    }

    @Override
    public void onDestroy() {
        mResetTimeDisposable.dispose();
        mSyncEventsInteractor.stopSync();
        mAutoDutyTypeManager.removeListener();
        super.onDestroy();
    }

    public void onLogoutItemSelected() {
        Disposable disposable = getEventsInteractor().postLogoutEvent()
                .doOnNext(isSuccess -> getUserInteractor().deleteDriver())
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
        add(disposable);
    }

    public void onViewCreated() {
        add(getUserInteractor().getFullDriverName()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(name -> mView.setDriverName(name)));

        mView.setBoxId(mVehiclesInteractor.getBoxId());
        mView.setAssetsNumber(mVehiclesInteractor.getAssetsNumber());

        add(getUserInteractor().getCoDriversNumber()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(count -> mView.setCoDriversNumber(count)));
        mAutoDutyTypeManager.validateBlackBoxState();
        mSyncEventsInteractor.startSync();
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
                              List<ELDEvent> prevDayLatestEvents = getEventsInteractor().getLatestActiveDutyEventFromDBSync(time[0], getUserInteractor().getUserId());
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

        getDutyTypeManager().setDutyTypeTime(
                (int) (times[DutyType.ON_DUTY.ordinal()]),
                (int) (times[DutyType.DRIVING.ordinal()]),
                (int) (times[DutyType.SLEEPER_BERTH.ordinal()]), dutyType
        );
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    public void onUserUpdated(User user) {
        if (user != null) {
            Disposable disposable = Observable.fromCallable(() -> getUserInteractor().getUserFromDBSync(user.getId()))
                                              .subscribeOn(Schedulers.io())
                                              .map(userEntity -> {
                                                  UserEntity updatedUser = UserConverter.toEntity(user);
                                                  updatedUser.setAccountName(userEntity.getAccountName());
                                                  updatedUser.setLastVehicleIds(userEntity.getLastVehicleIds());
                                                  updatedUser.setCoDriversIds(userEntity.getCoDriversIds());
                                                  return updatedUser;
                                              })
                                              .flatMap(userEntity -> getUserInteractor().syncDriverProfile(userEntity))
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(userUpdated -> {},
                                                         throwable -> mView.showErrorMessage(throwable.getMessage()));
            add(disposable);
        }
    }

    @Override
    public void onUserChanged() {
        super.onUserChanged();
        onResetTime();
    }

    @Override
    public void onDriverChanged() {
        super.onDriverChanged();
        Disposable disposable = Single.fromCallable(() -> getUserInteractor().getFullDriverNameSync())
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(name -> mView.setDriverName(name));
        add(disposable);
    }
}
