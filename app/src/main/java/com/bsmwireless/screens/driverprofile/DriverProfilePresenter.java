package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class DriverProfilePresenter {

    private LoginUserInteractor mLoginUserInteractor;
    private DriverProfileView mView;
    private CompositeDisposable mDisposables;

    @Inject
    public DriverProfilePresenter(DriverProfileView view, LoginUserInteractor loginUserInteractor) {
        mView = view;
        mLoginUserInteractor = loginUserInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }

    public void getUserInfo() {
        mDisposables.add(mLoginUserInteractor.getUser()
                                             .subscribeOn(Schedulers.io())
                                             .observeOn(AndroidSchedulers.mainThread())
                                             .subscribe(userEntity -> mView.setUserInfo(userEntity),
                                                        throwable -> Timber.e(throwable.getMessage())));
    }

    public void updateSignature(String signature) {
        mDisposables.add(
                mLoginUserInteractor.getUser()
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(userEntity -> {
                                                    userEntity.setSignature(signature);
                                                    updateUser(userEntity);
                                                },
                                                throwable -> Timber.e(throwable.getMessage()))
        );
    }

    public void updateName(String name) {
        mDisposables.add(
                mLoginUserInteractor.getUser()
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(userEntity -> {
                                                userEntity.setAccountName(name);
                                                updateUser(userEntity);
                                            },
                                            throwable -> Timber.e(throwable.getMessage()))
        );
    }

    public void updateHomeAddress(String address) {
        mDisposables.add(
                mLoginUserInteractor.getUser()
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(userEntity -> {
                                                userEntity.setAddress(address);
                                                updateUser(userEntity);
                                            },
                                            throwable -> Timber.e(throwable.getMessage()))
        );
    }

    private void updateUser(UserEntity user) {
        mDisposables.add(
                mLoginUserInteractor.updateUser(UserConverter.toUser(user))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(responseMessage -> mView.userUpdated(),
                                            throwable -> {
                                                Timber.e(throwable.getMessage());
                                                mView.userUpdateError(throwable);
                                            })
        );
    }
}
