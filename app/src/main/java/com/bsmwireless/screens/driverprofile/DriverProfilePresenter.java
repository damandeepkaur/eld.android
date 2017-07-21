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
            User user = getEmptyUser(mUserEntity);
            user.setSignature(signature);
            updateUserInDB(user);
        } else {
            mView.showError(new Exception());
        }

        mView.hideControlButtons();
    }

    public void onSaveUserInfo(String address) {
        if (mUserEntity != null) {
            User user = getEmptyUser(mUserEntity);
            user.setAddress(address);
            updateUserInDB(user);
        } else {
            mView.showError(new Exception());
        }
    }

    private void updateUserInDB(User user) {
        mLoginUserInteractor.updateUser(user)
                            .subscribeOn(Schedulers.io())
                            .subscribe();
    }

    private User getEmptyUser(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setUsername(mLoginUserInteractor.getUserName());
        user.setTimezone(userEntity.getTimezone());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setCycleCountry(userEntity.getCycleCountry());
        user.setSignature(userEntity.getSignature());
        user.setAddress(userEntity.getAddress());
        user.setOrganization(userEntity.getOrganization());
        user.setLicense(userEntity.getLicense());
        return user;
    }
}
