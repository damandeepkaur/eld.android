package com.bsmwireless.screens.driverprofile;

import android.content.Context;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.domain.interactors.LoginUserInteractor;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    private List<HomeTerminalEntity> mHomeTerminals;
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
        Disposable disposable = mLoginUserInteractor.getFullUser()
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(userEntity -> {
                                                                mFullUserEntity = userEntity;
                                                                mView.setUserInfo(mFullUserEntity.getUserEntity());

                                                                mHomeTerminals = mFullUserEntity.getHomeTerminalEntities();
                                                                if (mHomeTerminals != null && mHomeTerminals.size() > 0) {
                                                                    Integer selectedHomeTerminalId = mFullUserEntity.getUserEntity().getHomeTermId();
                                                                    int position = findHomeTerminalById(mHomeTerminals, selectedHomeTerminalId);
                                                                    mView.setHomeTerminalsSpinner(getHomeTerminalNames(mHomeTerminals), position);
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
                                                            });
        mDisposables.add(disposable);
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
                                                        .subscribe(wasUpdated -> {
                                                                    Timber.d("Update signature: " + wasUpdated);
                                                                    if (!wasUpdated) {
                                                                        mView.showError(new Exception(mContext.getString(R.string.driver_profile_signature_changing_error)));
                                                                    }
                                                                },
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
            Disposable disposable = mLoginUserInteractor.updateDriverPassword(oldPwd, newPwd)
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
            mDisposables.add(disposable);
        } else {
            mView.showChangePasswordError(validationError);
        }
    }

    public void onChooseHomeTerminal(int position) {
        if (mHomeTerminals != null && mHomeTerminals.size() > position) {
            HomeTerminalEntity homeTerminal = mHomeTerminals.get(position);

            mFullUserEntity.getUserEntity().setHomeTermId(homeTerminal.getId());

            Disposable disposable = mLoginUserInteractor.updateDriverHomeTerminal(homeTerminal.getId())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(wasUpdated -> {
                                                            if (!wasUpdated) {
                                                                mView.showError(new Exception(mContext.getString(R.string.driver_profile_home_terminal_updating_error)));
                                                            }
                                                        }, throwable -> mView.showError(throwable));
            mDisposables.add(disposable);

            mView.setHomeTerminalInfo(homeTerminal);
        } else {
            mView.showError(new Exception(mContext.getString(R.string.driver_profile_home_terminal_updating_error)));
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

    private int findHomeTerminalById(List<HomeTerminalEntity> homeTerminalEntities, Integer homeTerminalId) {
        if (homeTerminalEntities == null || homeTerminalId == null) {
            return 0;
        }

        for (int i = 0; i < homeTerminalEntities.size(); i++) {
            if (homeTerminalEntities.get(i).getId().equals(homeTerminalId)) {
                return i;
            }
        }

        return 0;
    }

    private List<String> getHomeTerminalNames(List<HomeTerminalEntity> homeTerminals) {
        List<String> terminalNames = new ArrayList<>();
        for (HomeTerminalEntity homeTerminal: homeTerminals) {
            terminalNames.add(homeTerminal.getName());
        }
        return terminalNames;
    }
}
