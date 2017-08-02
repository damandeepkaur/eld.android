package com.bsmwireless.screens.driverprofile;

import android.content.Context;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import java.util.Calendar;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class DriverProfilePresenter {

    public static final String PASSWORD_NOT_MATCH_ERROR = "password not match";
    public static final String FIELD_PASSWORD_EMPTY = "password field empty";
    public static final String VALID_PASSWORD = "valid password";

    private static final int MAX_SIGNATURE_LENGTH = 50000;

    private Context mContext;

    private LoginUserInteractor mLoginUserInteractor;
    private DriverProfileView mView;
    private CompositeDisposable mDisposables;

    private UserEntity mUserEntity;

    @Inject
    public DriverProfilePresenter(Context context, DriverProfileView view, LoginUserInteractor loginUserInteractor) {
        mView = view;
        mLoginUserInteractor = loginUserInteractor;
        mDisposables = new CompositeDisposable();
        mContext = context;

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
            if (signature.length() > MAX_SIGNATURE_LENGTH) {
                signature = cropSignature(signature);
                mView.showError(new Exception(mContext.getResources().getString(R.string.driver_profile_signature_error)));
            }
            mUserEntity.setSignature(signature);
        } else {
            mView.showError(new Exception(mContext.getResources().getString(R.string.driver_profile_user_error)));
        }

        mView.hideControlButtons();
    }

    public void onSaveUserInfo() {
        if (mUserEntity != null) {
            mView.setResults(UserConverter.toUser(mUserEntity));
        } else {
            mView.showError(new Exception(mContext.getResources().getString(R.string.driver_profile_user_error)));
        }
    }

    public void onChangePasswordClick(String oldPwd, String newPwd, String confirmPwd) {
        String validationError = validatePassword(oldPwd, newPwd, confirmPwd);
        if (validationError.equals(VALID_PASSWORD)) {
            mLoginUserInteractor.updateDriverPassword(oldPwd, newPwd)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(passwordUpdated -> {
                                            if (passwordUpdated) {
                                                mView.showPasswordChanged();
                                            } else {
                                                mView.showError(new Exception(mContext.getString(R.string.driver_profile_password_not_changed)));
                                            }
                                        },
                                        throwable -> mView.showError(throwable));
        } else {
            mView.showChangePasswordError(validationError);
        }
    }

    private String cropSignature(String signature) {
        if (signature.length() > MAX_SIGNATURE_LENGTH) {
            String croppedSignature = signature.substring(0, MAX_SIGNATURE_LENGTH);
            int lastIndex = croppedSignature.lastIndexOf(";");
            croppedSignature = croppedSignature.substring(0, lastIndex);
            return croppedSignature;
        }
        return signature;
    }

    private String validatePassword(String oldPwd, String newPwd, String confirmPwd) {
        if ((newPwd == null || newPwd.isEmpty()) ||
                (oldPwd == null || oldPwd.isEmpty())) {
            return FIELD_PASSWORD_EMPTY;
        } else if (confirmPwd == null || !confirmPwd.equals(newPwd)) {
            return PASSWORD_NOT_MATCH_ERROR;
        }
        return VALID_PASSWORD;
    }
}
