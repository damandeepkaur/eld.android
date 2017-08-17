package com.bsmwireless.screens.common.menu;

import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.widgets.alerts.DutyType;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.NetworkUtils.SUCCESS;

public abstract class BaseMenuPresenter {
    protected UserInteractor mUserInteractor;
    protected DutyManager mDutyManager;
    protected ELDEventsInteractor mEventsInteractor;
    protected CompositeDisposable mDisposables;

    private DutyManager.DutyTypeListener mListener = new DutyManager.DutyTypeListener() {
        @Override
        public void onDutyTypeChanged(DutyType dutyType) {
            getView().setDutyType(dutyType);
        }
    };

    protected abstract BaseMenuView getView();

    void onMenuCreated() {
        mDutyManager.addListener(mListener);
    }

    void onDutyChanged(DutyType dutyType) {
        mDisposables.add(mEventsInteractor.postNewDutyTypeEvent(dutyType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        responseMessage -> responseMessage.getMessage().equals(SUCCESS),
                        error -> Timber.e(error.getMessage())
                )
        );

    }

    public void onDestroy() {
        mDutyManager.removeListener(mListener);
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
