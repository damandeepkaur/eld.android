package com.bsmwireless.screens.switchdriver;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.MAX_CODRIVERS;

@ActivityScope
public class SwitchDriverPresenter {

    private SwitchDriverView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private UserInteractor mUserInteractor;
    private AccountManager mAccountManager;

    private Disposable mGetUsernameDisposable;
    private Disposable mGetCoDriversDisposable;
    private Disposable mLoginDisposable;
    private Disposable mLogoutDisposable;

    @Inject
    public SwitchDriverPresenter(SwitchDriverView view, ELDEventsInteractor eventsInteractor,
                                 UserInteractor userInteractor, AccountManager accountManager) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mUserInteractor = userInteractor;
        mAccountManager = accountManager;
        mGetUsernameDisposable = Disposables.disposed();
        mGetCoDriversDisposable = Disposables.disposed();
        mLoginDisposable = Disposables.disposed();
        mLogoutDisposable = Disposables.disposed();

        Timber.d("CREATED");
    }

    public void onDestroy() {
        mGetUsernameDisposable.dispose();
        mGetCoDriversDisposable.dispose();
        mLoginDisposable.dispose();
        mLogoutDisposable.dispose();
    }

    public void onSwitchDriverCreated() {
        mGetCoDriversDisposable.dispose();
        mGetCoDriversDisposable = mUserInteractor.getCoDriversFromDB()
                                                 .subscribeOn(Schedulers.io())
                                                 .map(this::getModelList)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .subscribe(coDrivers -> mView.setCoDriversForSwitchDialog(coDrivers));
        getDriverInfo();
    }

    public void onLogOutCoDriverCreated() {
        mGetCoDriversDisposable.dispose();
        mGetCoDriversDisposable = mUserInteractor.getCoDriversFromDB()
                                                 .subscribeOn(Schedulers.io())
                                                 .map(this::getModelList)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .subscribe(coDrivers -> mView.setCoDriversForLogOutDialog(coDrivers));
        getDriverInfo();
    }

    public void onDriverSeatDialogCreated() {
        mGetCoDriversDisposable.dispose();
        mGetCoDriversDisposable = mUserInteractor.getCoDriversFromDB()
                                                 .subscribeOn(Schedulers.io())
                                                 .map(this::getModelList)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .subscribe(coDrivers -> mView.setCoDriversForDriverSeatDialog(coDrivers));
        getDriverInfo();
    }

    public void onAddCoDriverCreated() {}

    public void login(String username, String password) {
        if (username == null || username.isEmpty()) {
            mView.showError(SwitchDriverView.Error.ERROR_INVALID_CREDENTIALS);
            return;
        } else if (password == null || password.isEmpty()) {
            mView.showError(SwitchDriverView.Error.ERROR_INVALID_CREDENTIALS);
            return;
        }
        mView.showProgress();
        mLoginDisposable.dispose();
        mLoginDisposable = Single.fromCallable(() -> mUserInteractor.getCoDriversNumberSync())
                                 .subscribeOn(Schedulers.io())
                                 .flatMapCompletable(count -> {
                                              if (count < MAX_CODRIVERS) {
                                                  return mUserInteractor.loginCoDriver(username, password, User.DriverType.CO_DRIVER);
                                              }
                                              return Completable.error(new Exception("Invalid co-drivers count"));
                                          })
                                 .observeOn(AndroidSchedulers.mainThread())
                                 .subscribe(() -> {
                                     mView.coDriverLoggedIn();
                                     mView.hideProgress();
                                 }, throwable -> {
                                     Timber.e(throwable);
                                     if (throwable instanceof RetrofitException) {
                                         mView.showError((RetrofitException) throwable);
                                     }
                                     mView.loginError();
                                     mView.hideProgress();
                                 });
    }

    public void logout(UserEntity user) {
        mView.showProgress();
        mLogoutDisposable.dispose();
        mLogoutDisposable = mELDEventsInteractor.postLogoutEvent(user.getId())
                                        .doOnEach(isSuccess -> mUserInteractor.deleteCoDriver(user))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnEach(booleanNotification -> mView.hideProgress())
                                        .subscribe(status -> mView.coDriverLoggedOut(), throwable -> {
                                            Timber.e(throwable);
                                            if (throwable instanceof RetrofitException) {
                                                mView.showError((RetrofitException) throwable);
                                            }
                                            mView.logoutError();
                                        });
    }

    public void setCurrentUser(UserEntity user) {
        if (user != null) {
            mAccountManager.setCurrentUser(user.getId(), user.getAccountName());
        } else {
            mAccountManager.resetUserToDriver();
        }
    }

    public void setCurrentDriver(UserEntity user) {
        mAccountManager.setCurrentDriver(user.getId(), user.getAccountName());
        mAccountManager.resetUserToDriver();
    }

    private void getDriverInfo() {
        if (mGetUsernameDisposable != null) {
            mGetUsernameDisposable.dispose();
        }
        mGetUsernameDisposable = mUserInteractor.getDriver()
                                                .subscribeOn(Schedulers.io())
                                                .map(userEntity -> {
                                                    List<ELDEvent> events = mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(System.currentTimeMillis(), userEntity.getId());
                                                    SwitchDriverDialog.UserModel user = new SwitchDriverDialog.UserModel(userEntity);
                                                    if (events != null && !events.isEmpty()) {
                                                        ELDEvent event = events.get(events.size() - 1);
                                                        user.setDutyType(DutyType.getTypeByCode(event.getEventType(), event.getEventCode()));
                                                    } else {
                                                        user.setDutyType(DutyType.OFF_DUTY);
                                                    }
                                                    return user;
                                                })
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(driver -> mView.setDriverInfo(driver));
    }

    private List<SwitchDriverDialog.UserModel> getModelList(List<UserEntity> userEntities) {
        List<SwitchDriverDialog.UserModel> users = new ArrayList<>(userEntities.size());
        for (UserEntity userEntity: userEntities) {
            SwitchDriverDialog.UserModel user = new SwitchDriverDialog.UserModel(userEntity);
            List<ELDEvent> events = mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(System.currentTimeMillis(), userEntity.getId());
            if (events != null && !events.isEmpty()) {
                ELDEvent event = events.get(events.size() - 1);
                user.setDutyType(DutyType.getTypeByCode(event.getEventType(), event.getEventCode()));
            } else {
                user.setDutyType(DutyType.OFF_DUTY);
            }
            users.add(user);
        }
        return users;
    }
}
