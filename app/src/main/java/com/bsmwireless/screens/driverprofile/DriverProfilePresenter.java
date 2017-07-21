package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.models.User;

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

    private UserEntity mUserEntity;

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

    public void onNeedUpdateUserInfo() {
        mDisposables.add(mLoginUserInteractor.getUser()
                                             .subscribeOn(Schedulers.io())
                                             .observeOn(AndroidSchedulers.mainThread())
                                             .subscribe(userEntity -> {
                                                             mUserEntity = userEntity;
                                                             mView.setUserInfo(mUserEntity);
                                                        },
                                                        throwable -> {
                                                            Timber.e(throwable.getMessage());
                                                            mView.showError(throwable);
                                                        }));
    }

    public void onSaveSignatureClicked(String signature) {
        if (mUserEntity != null) {
            mUserEntity.setSignature(signature);
            updateUserInDB();
        } else {
            mView.showError(new Exception());
        }

        mView.hideControlButtons();
    }

    public void onSaveUserInfo(String address) {
        if (mUserEntity != null) {
            mUserEntity.setAddress(address);
            updateUserInDB();
        } else {
            mView.showError(new Exception());
        }
    }

    private void updateUserInDB() {
        mLoginUserInteractor.updateUser(mUserEntity)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
    }
}
