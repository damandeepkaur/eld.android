package com.bsmwireless.screens.autologout;


import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class AutoDutyDialogPresenter {

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
                .doOnSuccess(isSuccess -> mUserInteractor.deleteDriver())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(() -> {
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
                        error -> {
                            Timber.e("Auto Logout error: %s", error);
                            mView.goToLoginScreen();
                        }
                ));
    }

    public void onOnDutyClick(long time) {
        mDisposables.add(mEventsInteractor.postNewDutyTypeEvent(DutyType.ON_DUTY, null, time)
                .subscribeOn(Schedulers.io())
                .doOnTerminate(() -> {
                    SchedulerUtils.cancel();
                    mView.onActionDone();
                })
                .subscribe(
                        status -> Timber.i("Auto OnDuty status"),
                        error -> Timber.e("Auto OnDuty error %s", error)
                ));
    }

    public void onSwitchStatusClick(boolean autoDriving) {

        Single
                .fromCallable(() -> {
                    ArrayList<ELDEvent> events = new ArrayList<>(2);
                    events.add(mEventsInteractor.getEvent(DutyType.CLEAR, null, autoDriving));
                    if (autoDriving) {
                        events.add(mEventsInteractor.getEvent(DutyType.DRIVING, null, true));
                    }
                    return events;
                })
                .flatMapCompletable(eldEvents -> mEventsInteractor.postNewELDEvents(eldEvents)
                        .firstOrError()
                        .toCompletable())
                .subscribeOn(Schedulers.io())
                .doOnTerminate(() -> {
                    SchedulerUtils.cancel();
                    mView.onActionDone();
                })
                .subscribe(
                        () -> Timber.i("Switching status done: isAutoDriving = %1$b", autoDriving),
                        error -> Timber.e("Auto Driving error %s", error)
                );
    }

    public void onDestroy() {
        mDisposables.dispose();

        Timber.d("DESTROYED");
    }
}
