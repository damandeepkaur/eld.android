package com.bsmwireless.screens.carrieredit;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.data.storage.PreferencesManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class CarrierEditPresenterImpl extends BaseMenuPresenter implements CarrierEditPresenter {

    private final PreferencesManager mPreferencesManager;
    private final VehiclesInteractor mVehiclesInteractor;

    private CarrierEditView mView;
    private CompositeDisposable mDisposable;
    private Disposable mDriverDisposable;
    private Disposable mVehicleDisposable;

    @Inject
    public CarrierEditPresenterImpl(DutyTypeManager dutyTypeManager,
                                    ELDEventsInteractor eventsInteractor, UserInteractor userInteractor,
                                    AccountManager accountManager,
                                    PreferencesManager preferencesManager, VehiclesInteractor vehiclesInteractor) {
        super(dutyTypeManager, eventsInteractor, userInteractor, accountManager);
        mPreferencesManager = preferencesManager;
        mVehiclesInteractor = vehiclesInteractor;
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    @Override
    public void bind(CarrierEditView view) {
        mView = view;
    }

    @Override
    public void requestDriverName() {
        if (mDriverDisposable == null) {
            mDriverDisposable = mUserInteractor.getDriver()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(driver -> {
                                mView.setDriverName(driver.getFirstName() + " " + driver.getLastName());
                                mView.setDriverId(driver.getId());
                            },
                            Timber::e,
                            () -> mDriverDisposable = null);
        }
    }

    @Override
    public void requestVehicleId() {
        mVehicleDisposable = Observable.fromCallable(() -> mVehiclesInteractor.getVehicle(mPreferencesManager.getVehicleId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vehicle -> mView.setVehicleName(vehicle.getName()));

    }
}
