package com.bsmwireless.screens.autologout;


import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.widgets.alerts.DutyType;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AutoDutyDialogPresenter {

    private UserInteractor mUserInteractor;
    private ELDEventsInteractor mEventsInteractor;
    private CompositeDisposable mDisposables;
    private AutoDutyDialogView mView;

    @Inject
    public AutoDutyDialogPresenter(AutoDutyDialogView view, UserInteractor interactor, ELDEventsInteractor eventsInteractor) {
        mView = view;
        mUserInteractor = interactor;
        mEventsInteractor = eventsInteractor;
        mDisposables = new CompositeDisposable();

        Timber.d("CREATED");
    }

    public void onRescheduleAutoLogoutClick() {
        SchedulerUtils.cancel();
        SchedulerUtils.schedule();

        mView.onActionDone();
    }

    public void onAutoLogoutClick() {
        mDisposables.add(mEventsInteractor.postLogoutEvent()
                .doOnNext(isSuccess -> mUserInteractor.deleteDriver())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> {
                    SchedulerUtils.cancel();
                    mView.onActionDone();
                })
                .subscribe(
                        status -> {
                            Timber.i("Auto Logout status = %b", status);
                            if (status) {
                                mView.goToLoginScreen();
                            }
                        },
                        error -> Timber.e("Auto Logout error: %s", error)
                ));
    }

    public void onOnDutyClick() {
        mDisposables.add(mEventsInteractor.postNewDutyTypeEvent(DutyType.ON_DUTY)
                .subscribeOn(Schedulers.io())
                .doOnTerminate(() -> {
                    SchedulerUtils.cancel();
                    mView.onActionDone();
                })
                .subscribe(
                        status -> Timber.i("Auto OnDuty status = %b", status),
                        error -> Timber.e("Auto OnDuty error %s", error)
                ));
    }

    public void onDrivingClick() {
        mDisposables.add(mEventsInteractor.postNewDutyTypeEvent(DutyType.DRIVING)
                .subscribeOn(Schedulers.io())
                .doOnTerminate(() -> {
                    SchedulerUtils.cancel();
                    mView.onActionDone();
                })
                .subscribe(
                        status -> Timber.i("Auto Driving status = %b", status),
                        error -> Timber.e("Auto Driving error %s", error)
                ));
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
