package com.bsmwireless.screens.switchdriver;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionManager;
import com.bsmwireless.data.network.blackbox.models.BlackBoxResponseModel;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
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
    private BlackBoxConnectionManager blackBox;

    private Disposable mGetUsernameDisposable;
    private Disposable mGetCoDriversDisposable;
    private Disposable mLoginDisposable;
    private Disposable mLogoutDisposable;
    private final CompositeDisposable commonDisposables;

    @Inject
    public SwitchDriverPresenter(SwitchDriverView view, ELDEventsInteractor eventsInteractor,
                                 UserInteractor userInteractor, AccountManager accountManager,
                                 BlackBoxConnectionManager blackBox) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mUserInteractor = userInteractor;
        mAccountManager = accountManager;
        this.blackBox = blackBox;
        mGetUsernameDisposable = Disposables.disposed();
        mGetCoDriversDisposable = Disposables.disposed();
        mLoginDisposable = Disposables.disposed();
        mLogoutDisposable = Disposables.disposed();
        commonDisposables = new CompositeDisposable();
        Timber.d("CREATED");
    }

    public void onDestroy() {
        mGetUsernameDisposable.dispose();
        mGetCoDriversDisposable.dispose();
        mLoginDisposable.dispose();
        mLogoutDisposable.dispose();
        commonDisposables.clear();
    }

    public void onSwitchDriverCreated() {
        mGetCoDriversDisposable.dispose();
        mGetCoDriversDisposable = mUserInteractor.getCoDriversFromDB()
                                                 .subscribeOn(Schedulers.io())
                                                 .map(SwitchDriverDialog.UserModel::fromEntity)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .doOnNext(userModels -> mView.setCoDriversForSwitchDialog(userModels))
                                                 .observeOn(Schedulers.io())
                                                 .map(this::updateStatus)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .subscribe(coDrivers -> mView.setCoDriversForSwitchDialog(coDrivers));
        getDriverInfo();
    }

    public void onLogOutCoDriverCreated() {
        mGetCoDriversDisposable.dispose();
        mGetCoDriversDisposable = mUserInteractor.getCoDriversFromDB()
                                                 .subscribeOn(Schedulers.io())
                                                 .map(SwitchDriverDialog.UserModel::fromEntity)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .doOnNext(userModels -> mView.setCoDriversForSwitchDialog(userModels))
                                                 .observeOn(Schedulers.io())
                                                 .map(this::updateStatus)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .subscribe(coDrivers -> mView.setCoDriversForLogOutDialog(coDrivers));
        getDriverInfo();
    }

    public void onDriverSeatDialogCreated() {
        mGetCoDriversDisposable.dispose();
        mGetCoDriversDisposable = mUserInteractor.getCoDriversFromDB()
                                                 .subscribeOn(Schedulers.io())
                                                 .map(SwitchDriverDialog.UserModel::fromEntity)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .doOnNext(userModels -> mView.setCoDriversForDriverSeatDialog(userModels))
                                                 .observeOn(Schedulers.io())
                                                 .map(this::updateStatus)
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                 .subscribe(coDrivers -> mView.setCoDriversForDriverSeatDialog(coDrivers));
        getDriverInfo();
    }

    public void onAddCoDriverCreated() {
    }

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

    public int getCurrentUserId() {
        return mAccountManager.getCurrentUserId();
    }

    public void setCurrentDriver(UserEntity user) {
        mAccountManager.setCurrentDriver(user.getId(), user.getAccountName());
        mAccountManager.resetUserToDriver();
    }

    public void onAddCoDriverDialog() {
        mView.createAddCoDriverDialog();
    }

    public void onLogoutDialog() {
        mView.createLogOutCoDriverDialog();
    }

    public void onDriverSeatDialog() {
        mView.createDriverSeatDialog();
    }

    public void onSwitchDriverDialog() {

        mView.createLoadingDialog();
        Disposable disposable = blackBox.getDataObservable()
                .map(BlackBoxModel::getResponseType)
                .first(BlackBoxResponseModel.ResponseType.NONE)
                .timeout(10, TimeUnit.SECONDS)
                .onErrorReturn(throwable -> {
                    Timber.e(throwable, "Error getting black box state");
                    return BlackBoxResponseModel.ResponseType.NONE;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseType -> {
                    if (responseType == BlackBoxResponseModel.ResponseType.MOVING) {
                        mView.createSwitchOnlyDialog();
                    } else {
                        mView.createSwitchDriverDialog();
                    }
                });
        commonDisposables.add(disposable);
    }

    private void getDriverInfo() {
        mGetUsernameDisposable.dispose();
        mGetUsernameDisposable = mUserInteractor.getDriver()
                                                .subscribeOn(Schedulers.io())
                                                .map(SwitchDriverDialog.UserModel::new)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .doOnNext(model -> mView.setDriverInfo(model))
                                                .observeOn(Schedulers.io())
                                                .map(model -> updateStatus(Collections.singletonList(model)))
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(driverModel -> mView.setDriverInfo(driverModel.get(0)));
    }

    private List<SwitchDriverDialog.UserModel> updateStatus(List<SwitchDriverDialog.UserModel> userEntities) {
        for (SwitchDriverDialog.UserModel user: userEntities) {
            List<ELDEvent> events = mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(System.currentTimeMillis(), user.getUser().getId());
            if (events != null && !events.isEmpty()) {
                ELDEvent event = events.get(events.size() - 1);
                user.setDutyType(DutyType.getTypeByCode(event.getEventType(), event.getEventCode()));
            }
        }
        return userEntities;
    }
}
