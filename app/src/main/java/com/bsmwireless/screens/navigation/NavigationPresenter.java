package com.bsmwireless.screens.navigation;

import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.AutoDutyTypeManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.SyncInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.domain.interactors.VehiclesInteractor;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.common.menu.BaseMenuView;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class NavigationPresenter extends BaseMenuPresenter {

    private NavigateView mView;
    private VehiclesInteractor mVehiclesInteractor;
    private SyncInteractor mSyncInteractor;
    private AutoDutyTypeManager mAutoDutyTypeManager;

    private AutoDutyTypeManager.AutoDutyTypeListener mListener = new AutoDutyTypeManager.AutoDutyTypeListener() {
        @Override
        public void onAutoOnDuty(long stoppedTime) {
            mView.setAutoOnDuty(stoppedTime);
        }

        @Override
        public void onAutoDriving() {
            mView.setAutoDriving();
        }

        @Override
        public void onAutoDrivingWithoutConfirm() {
            mView.setAutoDrivingWithoutConfirm();
        }
    };

    @Inject
    public NavigationPresenter(NavigateView view,
                               UserInteractor userInteractor,
                               VehiclesInteractor vehiclesInteractor,
                               ELDEventsInteractor eventsInteractor,
                               DutyTypeManager dutyTypeManager,
                               AutoDutyTypeManager autoDutyTypeManager,
                               SyncInteractor syncInteractor,
                               AccountManager accountManager) {
        super(dutyTypeManager, eventsInteractor, userInteractor, accountManager);
        mView = view;
        mVehiclesInteractor = vehiclesInteractor;
        mAutoDutyTypeManager = autoDutyTypeManager;
        mSyncInteractor = syncInteractor;

        mAutoDutyTypeManager.setListener(mListener);
    }

    @Override
    public void onDestroy() {
        mSyncInteractor.stopSync();
        mAutoDutyTypeManager.removeListener();
        super.onDestroy();
    }

    public void onLogoutItemSelected() {
        Disposable disposable = getEventsInteractor().postLogoutEvent()
                .doOnSuccess(isSuccess -> getUserInteractor().deleteDriver())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            Timber.i("LoginUser status = %b", status);
                            if (status) {
                                SchedulerUtils.cancel();
                                mView.goToLoginScreen();
                            } else {
                                mView.showErrorMessage("Logout failed");
                            }
                        },
                        error -> {
                            Timber.e("LoginUser error: %s", error);
                            mView.showErrorMessage("Exception:" + error.toString());
                        }
                );
        add(disposable);
    }

    public void onViewCreated() {
        add(getUserInteractor().getFullDriverName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(name -> mView.setDriverName(name)));

        mView.setBoxId(mVehiclesInteractor.getBoxId());
        mView.setAssetsNumber(mVehiclesInteractor.getAssetsNumber());

        add(getUserInteractor().getCoDriversNumber()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> mView.setCoDriversNumber(count)));
        mAutoDutyTypeManager.validateBlackBoxState();
        mSyncInteractor.startSync();
        checkForUnassignedEvents();

        getEventsInteractor().resetTime();
    }

    @Override
    protected BaseMenuView getView() {
        return mView;
    }

    @Override
    public void onDriverChanged() {
        super.onDriverChanged();
        Disposable disposable = Single.fromCallable(() -> getUserInteractor().getFullDriverNameSync())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(name -> mView.setDriverName(name));
        add(disposable);
    }

    private void checkForUnassignedEvents() {
        add(Observable.zip(mEventsInteractor.getUnidentifiedEvents(), mEventsInteractor.getUnidentifiedEvents(),
                (eldEvents, eldEvents2) -> {
                    eldEvents.addAll(eldEvents2);
                    return eldEvents;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    if (!res.isEmpty()) {
                        mView.showUnassignedDialog();
                    }
                }, Timber::e));
    }

    @Override
    public void onUserChanged() {
        super.onUserChanged();
        mEventsInteractor.resetTime();
    }
}
