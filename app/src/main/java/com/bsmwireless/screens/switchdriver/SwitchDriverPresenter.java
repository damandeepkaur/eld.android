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
import com.bsmwireless.models.BlackBoxSensorState;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.MAX_CODRIVERS;

@ActivityScope
public final class SwitchDriverPresenter {

    public static final int GETTING_BLACKBOX_MODEL_TIMEOUT = 2;
    private SwitchDriverView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private UserInteractor mUserInteractor;
    private AccountManager mAccountManager;
    private BlackBoxConnectionManager mBlackBox;

    private Disposable mGetUsernameDisposable;
    private Disposable mGetCoDriversDisposable;
    private Disposable mLoginDisposable;
    private Disposable mLogoutDisposable;
    private Disposable mReassignEventDisposable;
    private final CompositeDisposable mCommonDisposables;

    @Inject
    public SwitchDriverPresenter(SwitchDriverView view, ELDEventsInteractor eventsInteractor,
                                 UserInteractor userInteractor, AccountManager accountManager,
                                 BlackBoxConnectionManager blackBox) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mUserInteractor = userInteractor;
        mAccountManager = accountManager;
        mBlackBox = blackBox;
        mGetUsernameDisposable = Disposables.disposed();
        mGetCoDriversDisposable = Disposables.disposed();
        mLoginDisposable = Disposables.disposed();
        mLogoutDisposable = Disposables.disposed();
        mReassignEventDisposable = Disposables.disposed();
        mCommonDisposables = new CompositeDisposable();
        Timber.d("CREATED");
    }

    public void onDestroy() {
        mGetUsernameDisposable.dispose();
        mGetCoDriversDisposable.dispose();
        mLoginDisposable.dispose();
        mLogoutDisposable.dispose();
        mReassignEventDisposable.dispose();
        mCommonDisposables.clear();
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

    public void onReassignEventDialogCreated() {
        mGetCoDriversDisposable.dispose();
        mGetCoDriversDisposable = mUserInteractor.getDriver()
                    .zipWith(mUserInteractor.getCoDriversFromDB(),
                    (driver, coDrivers) -> {
                        mAccountManager.getCurrentUserId();
                        List<SwitchDriverDialog.UserModel> users = SwitchDriverDialog.UserModel.fromEntity(coDrivers);
                        SwitchDriverDialog.UserModel driverModel = new SwitchDriverDialog.UserModel(driver);
                        users.add(0, driverModel);
                        return users;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(users -> mView.setUsersForReassignDialog(users))
                    .observeOn(Schedulers.io())
                    .map(this::updateStatus)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(users -> mView.setUsersForReassignDialog(users))
                    .subscribe();
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
                    mView.hideProgress();
                    if (throwable instanceof RetrofitException) {
                        mView.showError((RetrofitException) throwable);
                    } else {
                        mView.showError(SwitchDriverView.Error.ERROR_LOGIN_CO_DRIVER);
                    }
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
                    } else {
                        mView.showError(SwitchDriverView.Error.ERROR_LOGOUT_CO_DRIVER);
                    }
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

        BlackBoxModel defaultModel = new BlackBoxModel();
        defaultModel.setResponseType(BlackBoxResponseModel.ResponseType.NONE);

        mView.createLoadingDialog();
        Disposable disposable = mBlackBox.getDataObservable()
                .firstOrError()
                .timeout(GETTING_BLACKBOX_MODEL_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseType -> {
                            if (responseType.getSensorState(BlackBoxSensorState.MOVING)) {
                                mView.createSwitchOnlyDialog();
                            } else {
                                mView.createSwitchDriverDialog();
                            }
                        }, throwable -> {
                            Timber.e(throwable, "Error getting current blackbox status");
                            mView.createSwitchDriverDialog();
                        });
        mCommonDisposables.add(disposable);
    }

    public void onReassignDialog() {
        mView.createReassignDialog();
    }

    public void reassignEvent(ELDEvent event, UserEntity user) {
        ELDEvent reassignedEvent = event.clone();
        reassignedEvent.setDriverId(user.getId());
        event.setStatus(ELDEvent.StatusCode.INACTIVE_CHANGED.getValue());
        event.setId(null);
        ArrayList<ELDEvent> events = new ArrayList<ELDEvent>() {{
            add(event);
            add(reassignedEvent);
        }};
        mReassignEventDisposable.dispose();
        mView.showProgress();
        mReassignEventDisposable = mELDEventsInteractor.updateELDEvents(events)
                                   .subscribeOn(Schedulers.io())
                                   .observeOn(AndroidSchedulers.mainThread())
                                   .doOnEach(notification -> mView.hideProgress())
                                   .subscribe(result -> mView.eventReassigned(),
                                           throwable -> {
                                               Timber.e(throwable);
                                               if (throwable instanceof RetrofitException) {
                                                   mView.showError((RetrofitException) throwable);
                                               } else {
                                                   mView.showError(SwitchDriverView.Error.ERROR_REASSIGN_EVENT);
                                               }
                                           });
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
        for (SwitchDriverDialog.UserModel user : userEntities) {
            List<ELDEvent> events = mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(System.currentTimeMillis(), user.getUser().getId());
            if (events != null && !events.isEmpty()) {
                ELDEvent event = events.get(events.size() - 1);
                user.setDutyType(DutyType.getTypeByCode(event.getEventType(), event.getEventCode()));
            }
        }
        return userEntities;
    }
}
