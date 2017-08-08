package com.bsmwireless.screens.driverprofile;

import android.content.Context;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class DriverProfilePresenter {

    private static final int MAX_SIGNATURE_LENGTH = 50000;

    private Context mContext;

    private LoginUserInteractor mLoginUserInteractor;
    private DriverProfileView mView;
    private CompositeDisposable mDisposables;

    private FullUserEntity mFullUserEntity;
    private HomeTerminalEntity mHomeTerminal;
    private CarrierEntity mCarrier;

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
        mDisposables.add(mLoginUserInteractor.getFullUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntity -> {
                            mFullUserEntity = userEntity;
                            mView.setUserInfo(mFullUserEntity.getUserEntity());

                            HomeTerminalEntity homeTerminal = findHomeTerminalById(mFullUserEntity.getHomeTerminalEntities(), mFullUserEntity
                                    .getUserEntity().getHomeTermId());
                            if (homeTerminal != null) {
                                mHomeTerminal = homeTerminal;
                                mView.setHomeTerminalInfo(mHomeTerminal);
                            }

                            List<CarrierEntity> carriers = mFullUserEntity.getCarriers();
                            if (carriers != null && carriers.size() > 0) {
                                mCarrier = carriers.get(0);
                                mView.setCarrierInfo(mCarrier);
                            }
                        },
                        throwable -> {
                            Timber.e(throwable.getMessage());
                            mView.showError(throwable);
                        }));
    }

    public void onSaveSignatureClicked(String signature) {
        if (mFullUserEntity != null) {
            if (signature.length() > MAX_SIGNATURE_LENGTH) {
                signature = cropSignature(signature);
                mView.showError(new Exception(mContext.getResources().getString(R.string.driver_profile_signature_error)));
            }

            mFullUserEntity.getUserEntity().setSignature(signature);

            Disposable disposable = mLoginUserInteractor.updateDriverSignature(signature)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(wasUpdated -> Timber.d("Update signature: " + wasUpdated),
                                                                throwable -> {
                                                                    Timber.e(throwable.getMessage());
                                                                    mView.showError(throwable);
                                                                });
            mDisposables.add(disposable);
        } else {
            mView.showError(new Exception(mContext.getResources().getString(R.string.driver_profile_user_error)));
        }

        mView.hideControlButtons();
    }

    public void onSaveUserInfo() {
        if (mFullUserEntity != null) {
            mView.setResults(UserConverter.toUser(mFullUserEntity.getUserEntity()));
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

    private HomeTerminalEntity findHomeTerminalById(List<HomeTerminalEntity> homeTerminalEntities, Integer homeTerminalId) {
        if (homeTerminalEntities == null || homeTerminalId == null) {
            return null;
        }

        for (HomeTerminalEntity homeTerminalEntity:
             homeTerminalEntities) {
            if (homeTerminalEntity.getId().equals(homeTerminalId)) {
                return homeTerminalEntity;
            }
        }

        return null;
    }
}
