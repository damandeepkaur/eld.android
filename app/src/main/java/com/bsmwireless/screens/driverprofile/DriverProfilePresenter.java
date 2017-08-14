package com.bsmwireless.screens.driverprofile;

import android.content.Context;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import java.util.Calendar;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class DriverProfilePresenter extends BaseMenuPresenter {

    private static final int MAX_SIGNATURE_LENGTH = 50000;

    private Context mContext;

    private LoginUserInteractor mLoginUserInteractor;
    private DriverProfileView mView;
    private CompositeDisposable mDisposables;

    private UserEntity mUserEntity;

    @Inject
    public DriverProfilePresenter(Context context, DriverProfileView view, LoginUserInteractor loginUserInteractor, DutyManager dutyManager) {
        mView = view;
        mLoginUserInteractor = loginUserInteractor;
        mDutyManager = dutyManager;
        mDisposables = new CompositeDisposable();
        mContext = context;

        Timber.d("CREATED");
    }

    public void onDestroy() {
        super.onDestroy();
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

    @Override
    protected BaseMenuView getView() {
        return mView;
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
        if (validationError.equals(mContext.getString(R.string.driver_profile_valid_password))) {
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
            return mContext.getString(R.string.driver_profile_password_field_empty);
        } else if (confirmPwd == null || !confirmPwd.equals(newPwd)) {
            return mContext.getString(R.string.driver_profile_password_not_match);
        }
        return mContext.getString(R.string.driver_profile_valid_password);
    }
}
