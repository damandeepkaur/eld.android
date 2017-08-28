package com.bsmwireless.screens.common.menu;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.widgets.alerts.DutyType;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public abstract class BaseMenuPresenter {
    protected DutyTypeManager mDutyTypeManager;
    protected ELDEventsInteractor mEventsInteractor;
    protected CompositeDisposable mDisposables;

    private DutyTypeManager.DutyTypeListener mListener = dutyType -> getView().setDutyType(dutyType);

    protected abstract BaseMenuView getView();

    void onMenuCreated() {
        mDutyTypeManager.addListener(mListener);
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
}
