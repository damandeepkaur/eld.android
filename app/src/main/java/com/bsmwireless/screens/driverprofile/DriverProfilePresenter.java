package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.users.UserConverter;
import com.bsmwireless.domain.interactors.LoginUserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.screens.driverprofile.DriverProfileView.PasswordError.PASSWORD_FIELD_EMPTY;
import static com.bsmwireless.screens.driverprofile.DriverProfileView.PasswordError.PASSWORD_NOT_MATCH;
import static com.bsmwireless.screens.driverprofile.DriverProfileView.PasswordError.VALID_PASSWORD;

@ActivityScope
public class DriverProfilePresenter extends BaseMenuPresenter {

    private static final int MAX_SIGNATURE_LENGTH = 50000;

    private LoginUserInteractor mLoginUserInteractor;
    private DriverProfileView mView;
    private CompositeDisposable mDisposables;

    private FullUserEntity mFullUserEntity;
    private List<HomeTerminalEntity> mHomeTerminals;
    private CarrierEntity mCarrier;

    @Inject
    public DriverProfilePresenter(DriverProfileView view, LoginUserInteractor loginUserInteractor, DutyManager dutyManager) {
        mView = view;
        mLoginUserInteractor = loginUserInteractor;
        mDutyManager = dutyManager;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onDestroy() {
        super.onDestroy();
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

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    public void onSaveSignatureClicked(String signature) {
        if (mFullUserEntity != null) {
            if (signature.length() > MAX_SIGNATURE_LENGTH) {
                signature = cropSignature(signature);
                mView.showError(DriverProfileView.Error.ERROR_SIGNATURE_LENGTH);
            }

            mFullUserEntity.getUserEntity().setSignature(signature);

            Disposable disposable = mLoginUserInteractor.updateDriverSignature(signature)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(wasUpdated -> {
                                                                    Timber.d("Update signature: " + wasUpdated);
                                                                    if (!wasUpdated) {
                                                                        mView.showError(DriverProfileView.Error.ERROR_SAVE_SIGNATURE);
                                                                    }
                                                                },
                                                                throwable -> {
                                                                    Timber.e(throwable.getMessage());
                                                                    mView.showError(throwable);
                                                                });
            mDisposables.add(disposable);
        } else {
            mView.showError(DriverProfileView.Error.ERROR_INVALID_USER);
        }

        mView.hideControlButtons();
    }

    public void onSaveUserInfo() {
        if (mFullUserEntity != null) {
            mView.setResults(UserConverter.toUser(mFullUserEntity.getUserEntity()));
        } else {
            mView.showError(DriverProfileView.Error.ERROR_INVALID_USER);
        }
    }

    public void onChangePasswordClick(String oldPwd, String newPwd, String confirmPwd) {
        DriverProfileView.PasswordError validationError = validatePassword(oldPwd, newPwd, confirmPwd);
        if (validationError.equals(VALID_PASSWORD)) {
            Disposable disposable = mLoginUserInteractor.updateDriverPassword(oldPwd, newPwd)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(passwordUpdated -> {
                                                                    if (passwordUpdated) {
                                                                        mView.showPasswordChanged();
                                                                    } else {
                                                                        mView.showError(DriverProfileView.Error.ERROR_CHANGE_PASSWORD);
                                                                    }
                                                                },
                                                                throwable -> mView.showError(throwable));
            mDisposables.add(disposable);
        } else {
            mView.showError(validationError);
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
                                                                mView.showError(DriverProfileView.Error.ERROR_TERMINAL_UPDATE);
                                                            }
                                                        }, throwable -> mView.showError(throwable));
            mDisposables.add(disposable);

            mView.setHomeTerminalInfo(homeTerminal);
        } else {
            mView.showError(DriverProfileView.Error.ERROR_TERMINAL_UPDATE);
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

    private DriverProfileView.PasswordError validatePassword(String oldPwd, String newPwd, String confirmPwd) {
        if ((newPwd == null || newPwd.isEmpty()) ||
                (oldPwd == null || oldPwd.isEmpty())) {
            return PASSWORD_FIELD_EMPTY;
        } else if (confirmPwd == null || !confirmPwd.equals(newPwd)) {
            return PASSWORD_NOT_MATCH;
        }
        return VALID_PASSWORD;
    }

    private int findHomeTerminalById(List<HomeTerminalEntity> homeTerminals, Integer homeTerminalId) {
        if (homeTerminals == null || homeTerminalId == null) {
            return 0;
        }

        for (int i = 0; i < homeTerminals.size(); i++) {
            if (homeTerminals.get(i).getId().equals(homeTerminalId)) {
                return i;
            }
        }

        return 0;
    }

    private List<String> getHomeTerminalNames(List<HomeTerminalEntity> homeTerminals) {
        List<String> terminalNames = new ArrayList<>();
        if (homeTerminals != null) {
            for (HomeTerminalEntity homeTerminal : homeTerminals) {
                terminalNames.add(homeTerminal.getName());
            }
        }
        return terminalNames;
    }
}
