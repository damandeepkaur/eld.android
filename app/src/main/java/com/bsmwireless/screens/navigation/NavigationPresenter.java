package com.bsmwireless.screens.navigation;

import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.models.User;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NavigationPresenter {

    private NavigateView mView;
    private LoginUserInteractor mLoginUserInteractor;
    private VehiclesInteractor mVehiclesInteractor;
    private CompositeDisposable mDisposables;

    @Inject
    public NavigationPresenter(NavigateView view, LoginUserInteractor loginUserInteractor, VehiclesInteractor vehiclesInteractor) {
        mView = view;
        mLoginUserInteractor = loginUserInteractor;
        mVehiclesInteractor = vehiclesInteractor;
        mDisposables = new CompositeDisposable();
    }

    public void onLogoutItemSelected() {
        Disposable disposable = mLoginUserInteractor.logoutUser()
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

    public void onDestroy() {
        mDisposables.dispose();
    }

    public void onViewCreated() {
        mDisposables.add(mLoginUserInteractor.getFullName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(name -> mView.setDriverName(name)));
        mView.setCoDriversNumber(mLoginUserInteractor.getCoDriversNumber());
        mView.setBoxId(mVehiclesInteractor.getBoxId());
        mView.setAssetsNumber(mVehiclesInteractor.getAssetsNumber());
    }

    public void onUserUpdated() {
        Disposable disposable = mLoginUserInteractor.getUser()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        userEntity -> mLoginUserInteractor.updateUserOnServer(getUpdatedUser(userEntity))
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(),
                        throwable -> Timber.e("LoginUser error: %s", throwable.toString())
                );
        mDisposables.add(disposable);
    }

    // TODO: change server logic
    private User getUpdatedUser(UserEntity userEntity) {
        User user = new User();

        user.setId(userEntity.getId());
        user.setTimezone(userEntity.getTimezone());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setCycleCountry(userEntity.getCycleCountry());
        user.setSignature(userEntity.getSignature());
        user.setAddress(userEntity.getAddress());

        return user;
    }
}
