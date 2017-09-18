package com.bsmwireless.screens.driverprofile;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.screens.driverprofile.DriverProfileView.Error.PASSWORD_FIELD_EMPTY;
import static com.bsmwireless.screens.driverprofile.DriverProfileView.Error.PASSWORD_NOT_MATCH;
import static com.bsmwireless.screens.driverprofile.DriverProfileView.Error.VALID_PASSWORD;

@ActivityScope
public class DriverProfilePresenter extends BaseMenuPresenter {

    private static final int MAX_SIGNATURE_LENGTH = 50000;

    private DriverProfileView mView;
    private UserInteractor mUserInteractor;

    private FullUserEntity mFullUserEntity;
    private List<HomeTerminalEntity> mHomeTerminals;
    private CarrierEntity mCarrier;
    private List<String> mHOSCycles;

    private CompositeDisposable mDisposables;

    @Inject
    public DriverProfilePresenter(DriverProfileView view,
                                  UserInteractor userInteractor,
                                  DutyTypeManager dutyTypeManager,
                                  ELDEventsInteractor eventsInteractor,
                                  AccountManager accountManager) {
        super(dutyTypeManager, eventsInteractor, userInteractor, accountManager);
        mView = view;
        mUserInteractor = userInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onNeedUpdateUserInfo() {
        Disposable disposable = Single.fromCallable(() -> getUserInteractor().getFullUserSync())
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .doOnSuccess(userEntity -> {
                                          mFullUserEntity = userEntity;
                                          mView.setUserInfo(mFullUserEntity.getUserEntity());
                                      })
                                      .doOnSuccess(this::updateHomeTerminalsList)
                                      .doOnSuccess(this::updateCarrierInfo)
                                      .doOnSuccess(this::updateHOSCycles)
                                      .subscribe(
                                              userEntity -> {}, throwable ->
                                              Timber.e(throwable.getMessage())
                                      );
        getDisposables().add(disposable);
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

            Disposable disposable = getUserInteractor().updateDriverSignature(signature)
                                                   .subscribeOn(Schedulers.io())
                                                   .flatMap(wasUpdated -> {
                                                       if (wasUpdated) {
                                                           return mUserInteractor.syncDriverProfile(mFullUserEntity.getUserEntity());
                                                       }
                                                       return Observable.just(wasUpdated);
                                                   })
                                                   .observeOn(AndroidSchedulers.mainThread())
                                                   .subscribe(wasUpdated -> {
                                                               Timber.d("Update signature: " + wasUpdated);
                                                               if (!wasUpdated) {
                                                                   mView.showError(DriverProfileView.Error.ERROR_SAVE_SIGNATURE);
                                                               } else {
                                                                   mView.showSignatureChanged();
                                                               }
                                                           },
                                                           throwable -> {
                                                               Timber.e(throwable.getMessage());
                                                               if (throwable instanceof RetrofitException) {
                                                                   mView.showError((RetrofitException) throwable);
                                                               }
                                                           });
            getDisposables().add(disposable);
        } else {
            mView.showError(DriverProfileView.Error.ERROR_INVALID_USER);
        }
    }

    public void onChangePasswordClick(String oldPwd, String newPwd, String confirmPwd) {
        DriverProfileView.Error validationError = validatePassword(oldPwd, newPwd, confirmPwd);
        if (validationError.equals(VALID_PASSWORD)) {
            Disposable disposable = getUserInteractor().updateDriverPassword(oldPwd, newPwd)
                                                   .subscribeOn(Schedulers.io())
                                                   .observeOn(AndroidSchedulers.mainThread())
                                                   .subscribe(passwordUpdated -> {
                                                               if (passwordUpdated) {
                                                                   mView.showPasswordChanged();
                                                               } else {
                                                                   mView.showError(DriverProfileView.Error.ERROR_CHANGE_PASSWORD);
                                                               }
                                                           },
                                                           throwable -> {
                                                               Timber.e(throwable.getMessage());
                                                               if (throwable instanceof RetrofitException) {
                                                                   mView.showError((RetrofitException) throwable);
                                                               }
                                                           });
            getDisposables().add(disposable);
        } else {
            mView.showError(validationError);
        }
    }

    public void onChooseHomeTerminal(int position) {
        if (mHomeTerminals != null && mHomeTerminals.size() > position) {
            HomeTerminalEntity homeTerminal = mHomeTerminals.get(position);

            mFullUserEntity.getUserEntity().setHomeTermId(homeTerminal.getId());

            Disposable disposable = getUserInteractor().updateDriverHomeTerminal(homeTerminal.getId())
                                                   .subscribeOn(Schedulers.io())
                                                   .flatMap(wasUpdated -> {
                                                       if (wasUpdated) {
                                                           return mUserInteractor.syncDriverProfile(mFullUserEntity.getUserEntity());
                                                       }
                                                       return Observable.just(wasUpdated);
                                                   })
                                                   .observeOn(AndroidSchedulers.mainThread())
                                                   .subscribe(wasUpdated -> {
                                                       if (!wasUpdated) {
                                                           mView.showError(DriverProfileView.Error.ERROR_TERMINAL_UPDATE);
                                                       }
                                                   }, throwable -> {
                                                       Timber.e(throwable.getMessage());
                                                       if (throwable instanceof RetrofitException) {
                                                           mView.showError((RetrofitException) throwable);
                                                       }
                                                   });
            getDisposables().add(disposable);

            mView.setHomeTerminalInfo(homeTerminal);
        } else {
            mView.showError(DriverProfileView.Error.ERROR_TERMINAL_UPDATE);
        }
    }

    public void onChooseHOSCycle(int position) {
        if (mHOSCycles != null && mHOSCycles.size() > position) {
            String cycle = mHOSCycles.get(position);

            mFullUserEntity.getUserEntity().setDutyCycle(cycle);

            Disposable disposable = mUserInteractor.updateDriverRule(null, cycle)
                                                   .subscribeOn(Schedulers.io())
                                                   .flatMapObservable(wasUpdated -> {
                                                       if (wasUpdated) {
                                                           return mUserInteractor.syncDriverProfile(mFullUserEntity.getUserEntity());
                                                       }
                                                       return Observable.just(wasUpdated);
                                                   })
                                                   .observeOn(AndroidSchedulers.mainThread())
                                                   .subscribe(wasUpdated -> {
                                                       if (!wasUpdated) {
                                                           mView.showError(DriverProfileView.Error.ERROR_HOS_CYCLE_UPDATE);
                                                       }
                                                   }, throwable -> {
                                                       Timber.e(throwable.getMessage());
                                                       if (throwable instanceof RetrofitException) {
                                                           mView.showError((RetrofitException) throwable);
                                                       }
                                                   });
            mDisposables.add(disposable);
        } else {
            mView.showError(DriverProfileView.Error.ERROR_HOS_CYCLE_UPDATE);
        }
    }

    private void updateHomeTerminalsList(FullUserEntity userEntity) {
        mHomeTerminals = userEntity.getHomeTerminalEntities();
        if (mHomeTerminals != null && !mHomeTerminals.isEmpty()) {
            int position = findHomeTerminalById(mHomeTerminals, userEntity.getUserEntity().getHomeTermId());
            mView.setHomeTerminalsSpinner(getHomeTerminalNames(mHomeTerminals), position);
        } else {
            mView.setHomeTerminalsSpinner(Collections.emptyList(), 0);
        }
    }

    private void updateCarrierInfo(FullUserEntity userEntity) {
        List<CarrierEntity> carriers = userEntity.getCarriers();
        if (carriers != null && !carriers.isEmpty()) {
            mCarrier = carriers.get(0);
            mView.setCarrierInfo(mCarrier);
        } else {
            mView.setCarrierInfo(new CarrierEntity());
        }
    }

    private void updateHOSCycles(FullUserEntity userEntity) {
        mHOSCycles = userEntity.getCyclesList();
        if (mHOSCycles != null && !mHOSCycles.isEmpty()) {
            String selectedCycle = userEntity.getUserEntity().getDutyCycle();
            int selectedCycleIndex = mHOSCycles.indexOf(selectedCycle);
            mView.setCycleInfo(mHOSCycles, selectedCycleIndex);
        } else {
            mView.setCycleInfo(Collections.emptyList(), 0);
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

    private DriverProfileView.Error validatePassword(String oldPwd, String newPwd, String confirmPwd) {
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

    @Override
    public void onUserChanged() {
        super.onUserChanged();
        onNeedUpdateUserInfo();
    }
}
