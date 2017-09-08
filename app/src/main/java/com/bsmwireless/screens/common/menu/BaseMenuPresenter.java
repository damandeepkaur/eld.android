package com.bsmwireless.screens.common.menu;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.OccupancyType;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public abstract class BaseMenuPresenter {
    protected DutyTypeManager mDutyTypeManager;
    protected AccountManager mAccountManager;
    protected ELDEventsInteractor mEventsInteractor;
    protected UserInteractor mUserInteractor;
    protected CompositeDisposable mDisposables;

    private DutyTypeManager.DutyTypeListener mListener = dutyType -> getView().setDutyType(dutyType);

    protected abstract BaseMenuView getView();

    public void showMalfunctionEvent() {
        getView().showMalfunctionDialog();
    }

    public void showDiagnosticEvents() {
        getView().showDiagnosticEvents();
    }

    void onMenuCreated() {
        mDutyTypeManager.addListener(mListener);
        mDisposables.add(mUserInteractor.getCoDriversNumber()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(count -> getView().setOccupancyType(OccupancyType.getTypeById(count))));
    }

    void onDutyChanged(DutyType dutyType) {
        // don't set the same type
        if (dutyType != mDutyTypeManager.getDutyType()) {
            mDisposables.add(mEventsInteractor.postNewDutyTypeEvent(dutyType)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            responseMessage -> {},
                            error -> Timber.e(error.getMessage())
                    )
            );
        }
    }

    public void onChangeDutyClick() {
        if (mEventsInteractor.isConnected()) {
            getView().showDutyTypeDialog(mDutyTypeManager.getDutyType());
        } else {
            getView().showNotInVehicleDialog();
        }
    }

    public void onDestroy() {
        mDutyTypeManager.removeListener(mListener);
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }

    public boolean isUserDriver() {
        return mUserInteractor.isUserDriver();
    }
}
