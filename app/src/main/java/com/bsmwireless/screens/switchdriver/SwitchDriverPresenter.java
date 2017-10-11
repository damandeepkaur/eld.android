package com.bsmwireless.screens.switchdriver;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

import static com.bsmwireless.common.Constants.MAX_CODRIVERS;

@ActivityScope
public final class SwitchDriverPresenter {

    private SwitchDriverView mView;
    private ELDEventsInteractor mELDEventsInteractor;
    private UserInteractor mUserInteractor;
    private AccountManager mAccountManager;
    private BlackBoxInteractor mBlackBoxInteractor;
    private BlackBoxStateChecker mBlackBoxStateChecker;

    private Disposable mGetUsernameDisposable;
    private Disposable mGetCoDriversDisposable;
    private Disposable mLoginDisposable;
    private Disposable mLogoutDisposable;
    private Disposable mReassignEventDisposable;
    private Disposable mCheckMaxCoDriversNumberDisposable;
    private Disposable mCheckMinCoDriversNumberDisposable;
    private final CompositeDisposable mCommonDisposables;

    @Inject
    public SwitchDriverPresenter(SwitchDriverView view, ELDEventsInteractor eventsInteractor,
                                 UserInteractor userInteractor, AccountManager accountManager,
                                 BlackBoxInteractor blackBoxInteractor,
                                 BlackBoxStateChecker blackBoxStateChecker) {
        mView = view;
        mELDEventsInteractor = eventsInteractor;
        mUserInteractor = userInteractor;
        mAccountManager = accountManager;
        mBlackBoxInteractor = blackBoxInteractor;
        mBlackBoxStateChecker = blackBoxStateChecker;
        mGetUsernameDisposable = Disposables.disposed();
        mGetCoDriversDisposable = Disposables.disposed();
        mLoginDisposable = Disposables.disposed();
        mLogoutDisposable = Disposables.disposed();
        mReassignEventDisposable = Disposables.disposed();
        mCheckMaxCoDriversNumberDisposable = Disposables.disposed();
        mCheckMinCoDriversNumberDisposable = Disposables.disposed();
        mCommonDisposables = new CompositeDisposable();
        Timber.d("CREATED");
    }

    public void onDestroy() {
        mGetUsernameDisposable.dispose();
        mGetCoDriversDisposable.dispose();
        mLoginDisposable.dispose();
        mLogoutDisposable.dispose();
        mReassignEventDisposable.dispose();
        mCheckMaxCoDriversNumberDisposable.dispose();
        mCheckMinCoDriversNumberDisposable.dispose();
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
                    } else if (throwable instanceof HttpException) {
                        mView.showError(SwitchDriverView.Error.ERROR_INVALID_CREDENTIALS);
                    } else {
                        mView.showError(SwitchDriverView.Error.ERROR_LOGIN_CO_DRIVER);
                    }
                });
    }

    public void logout(UserEntity user) {
        mView.showProgress();
        mLogoutDisposable.dispose();
        mLogoutDisposable = mELDEventsInteractor.postLogoutEvent(user.getId())
                .doOnSuccess(isSuccess -> mUserInteractor.deleteCoDriver(user))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(booleanNotification -> mView.hideProgress())
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
        if (mBlackBoxStateChecker.isMoving(mBlackBoxInteractor.getLastData())) {
            mView.createSwitchOnlyDialog();
        } else {
            mView.createSwitchDriverDialog();
        }
    }

    public void onReassignDialog() {
        mView.createReassignDialog();
    }

    public void reassignEvent(ELDEvent event, UserEntity user) {
        // Prepare reassigned event
        ELDEvent reassignedEvent = event.clone();
        reassignedEvent.setDriverId(user.getId());
        reassignedEvent.setOrigin(ELDEvent.EventOrigin.NON_DRIVER.getValue());
        reassignedEvent.setStatus(ELDEvent.StatusCode.INACTIVE_CHANGE_REQUESTED.getValue());
        // Change original event status
        event.setStatus(ELDEvent.StatusCode.INACTIVE_CHANGED.getValue());
        event.setId(null);

        List<ELDEvent> events = Arrays.asList(event, reassignedEvent);

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
            List<ELDEvent> events = mELDEventsInteractor.getLatestActiveDutyEventFromDBSync(DateUtils.currentTimeMillis(), user.getUser().getId());
            if (events != null && !events.isEmpty()) {
                ELDEvent event = events.get(events.size() - 1);
                user.setDutyType(DutyType.getDutyTypeByCode(event.getEventType(), event.getEventCode()));
            }
        }
        return userEntities;
    }

    public void enableAddCoDrivers() {
        mCheckMaxCoDriversNumberDisposable.dispose();
        mCheckMaxCoDriversNumberDisposable = isMaxCoDriversReached()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    if (status) {
                        mView.setAddCoDriverButtonEnabled(true);
                    } else {
                        mView.setAddCoDriverButtonEnabled(false);
                    }
                });
    }

    private Single<Boolean> isMaxCoDriversReached() {
        return Single.fromCallable(() -> mUserInteractor.getCoDriversNumberSync())
                .map(count -> count < MAX_CODRIVERS);
    }

    public void enableLogoutButton() {
        mCheckMinCoDriversNumberDisposable.dispose();
        mCheckMinCoDriversNumberDisposable = isCoDriverAdded()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    if (status) {
                        mView.setLogOutButtonEnabled(false);
                    } else {
                        mView.setLogOutButtonEnabled(true);
                    }
                });
    }

    private Single<Boolean> isCoDriverAdded() {
        return Single.fromCallable(() -> mUserInteractor.getCoDriversNumberSync())
                .map(count -> count == 0);
    }
}
