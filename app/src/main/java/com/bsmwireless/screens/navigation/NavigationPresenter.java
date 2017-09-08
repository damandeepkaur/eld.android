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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NavigationPresenter extends BaseMenuPresenter implements AccountManager.AccountListener {

    private NavigateView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private Disposable mResetTimeDisposable;
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
                               DutyTypeManager dutyTypeManager, AutoDutyTypeManager autoDutyTypeManager, SyncEventsInteractor syncEventsInteractor, AccountManager accountManager) {
        mView = view;
        mUserInteractor = userInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mEventsInteractor = eventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mAutoDutyTypeManager = autoDutyTypeManager;
        mSyncEventsInteractor = syncEventsInteractor;
        mAccountManager = accountManager;
        mDisposables = new CompositeDisposable();
        mResetTimeDisposable = Disposables.disposed();

        mAutoDutyTypeManager.setListener(mListener);
    }

    @Override
    public void onDestroy() {
        mResetTimeDisposable.dispose();
        mAccountManager.removeListener(this);
        mSyncEventsInteractor.stopSync();
        mAutoDutyTypeManager.removeListener();
        super.onDestroy();
    }

    public void onLogoutItemSelected() {
        Disposable disposable = mEventsInteractor.postLogoutEvent()
                .doOnNext(isSuccess -> mUserInteractor.deleteDriver())
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
        mAccountManager.addListener(this);
        mDisposables.add(mUserInteractor.getFullDriverName()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(name -> mView.setDriverName(name)));

        mView.setBoxId(mVehiclesInteractor.getBoxId());
        mView.setAssetsNumber(mVehiclesInteractor.getAssetsNumber());

        mDisposables.add(mUserInteractor.getCoDriversNumber()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(count -> mView.setCoDriversNumber(count)));
        mAutoDutyTypeManager.validateBlackBoxState();
        mSyncEventsInteractor.startSync();

        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(() -> mUserInteractor.getFullUserNameSync())
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(name -> mView.showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            mView.hideCoDriverView();
        }
    }

    public void onResetTime() {
        //start and end time
        long[] time = new long[2];

        mResetTimeDisposable.dispose();
        mResetTimeDisposable = mUserInteractor.getTimezone()
              .flatMap(timeZone -> {
                  long current = System.currentTimeMillis();
                  time[0] = DateUtils.getStartDayTimeInMs(timeZone, current);
                  time[1] = DateUtils.getEndDayTimeInMs(timeZone, current);

                  mView.setResetTime(time[1]);

                  return mEventsInteractor
                          .getDutyEventsFromDB(time[0], time[1])
                          .map(selectedDayEvents -> {
                              List<ELDEvent> prevDayLatestEvents = mEventsInteractor.getLatestActiveDutyEventFromDBSync(time[0], mUserInteractor.getUserId());
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

        mDutyTypeManager.setDutyTypeTime(
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
            Disposable disposable = Observable.fromCallable(() -> mUserInteractor.getUserFromDBSync(user.getId()))
                                              .subscribeOn(Schedulers.io())
                                              .map(userEntity -> {
                                                  UserEntity updatedUser = UserConverter.toEntity(user);
                                                  updatedUser.setAccountName(userEntity.getAccountName());
                                                  updatedUser.setLastVehicleIds(userEntity.getLastVehicleIds());
                                                  updatedUser.setCoDriversIds(userEntity.getCoDriversIds());
                                                  return updatedUser;
                                              })
                                              .flatMap(userEntity -> mUserInteractor.syncDriverProfile(userEntity))
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(userUpdated -> {},
                                                         throwable -> mView.showErrorMessage(throwable.getMessage()));
            mDisposables.add(disposable);
        }
    }

    @Override
    public void onUserChanged() {
        onResetTime();
        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(() -> mUserInteractor.getFullUserNameSync())
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(name -> mView.showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            mView.hideCoDriverView();
        }
    }

    @Override
    public void onDriverChanged() {
        Disposable disposable = Single.fromCallable(() -> mUserInteractor.getFullDriverNameSync())
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(name -> mView.setDriverName(name));
        mDisposables.add(disposable);
    }
}
