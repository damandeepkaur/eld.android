package com.bsmwireless.screens.common.menu;

import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.OccupancyType;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

public abstract class BaseMenuPresenter implements AccountManager.AccountListener{
    private final DutyTypeManager mDutyTypeManager;
    private final ELDEventsInteractor mEventsInteractor;
    private final UserInteractor mUserInteractor;
    private final AccountManager mAccountManager;
    private CompositeDisposable mDisposables;
    private Disposable mDiagnosticEventsDisposable;
    private Disposable mMalfunctionEventsDisposable;
    private final Subject<Integer> mMenuCreatedSubject;

    public BaseMenuPresenter(DutyTypeManager dutyTypeManager,
                             ELDEventsInteractor eventsInteractor,
                             UserInteractor userInteractor, AccountManager mAccountManager) {
        this.mDutyTypeManager = dutyTypeManager;
        this.mEventsInteractor = eventsInteractor;
        this.mUserInteractor = userInteractor;
        this.mAccountManager = mAccountManager;
        this.mDisposables = new CompositeDisposable();
        mDiagnosticEventsDisposable = Disposables.disposed();
        mMalfunctionEventsDisposable = Disposables.disposed();
        mMenuCreatedSubject = BehaviorSubject.create();
    }

    private DutyTypeManager.DutyTypeListener mListener = dutyType -> getView().setDutyType(dutyType);

    protected abstract BaseMenuView getView();

    public void onStart() {
        startMonitoringEvents();
    }

    public void onStop() {
        stopEventsMonitoring();
    }

    public void onMalfunctionEventsClick() {
        getView().showMalfunctionDialog();
    }

    public void onDiagnosticEventsClick() {
        getView().showDiagnosticEvents();
    }

    void onMenuCreated() {
        mMenuCreatedSubject.onNext(0);
        mDutyTypeManager.addListener(mListener);
        mDisposables.add(mUserInteractor.getCoDriversNumber()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> getView().setOccupancyType(OccupancyType.getTypeById(count))));
        mAccountManager.addListener(this);
        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(mUserInteractor::getFullUserNameSync)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(name -> getView().showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            getView().hideCoDriverView();
        }
    }

    void onDutyChanged(DutyType dutyType, String comment) {
        // don't set the same type
        if (dutyType != mDutyTypeManager.getDutyType()) {
            mDisposables.add(mEventsInteractor.postNewDutyTypeEvent(dutyType, comment)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            responseMessage -> {
                            },
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
        mAccountManager.removeListener(this);
        mDutyTypeManager.removeListener(mListener);
        mDisposables.dispose();
        mMenuCreatedSubject.onComplete();

        Timber.d("DESTROYED");
    }

    public boolean isUserDriver() {
        return mUserInteractor.isUserDriver();
    }

    private void startMonitoringEvents() {

        mDiagnosticEventsDisposable = Flowable
                .combineLatest(mEventsInteractor.hasDiagnosticEvents(),
                        mMenuCreatedSubject.toFlowable(BackpressureStrategy.LATEST),
                        (result, integer) -> result)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    Timber.e(throwable);
                    return false;
                })
                .subscribe(hasEvents -> getView().changeDiagnosticStatus(hasEvents));

        mMalfunctionEventsDisposable = Flowable
                .combineLatest(mEventsInteractor.hasMalfunctionEvents(),
                        mMenuCreatedSubject.toFlowable(BackpressureStrategy.LATEST),
                        (result, integer) -> result)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    Timber.e(throwable);
                    return false;
                })
                .subscribe(hasEvents -> getView().changeMalfunctionStatus(hasEvents));
    }

    private void stopEventsMonitoring() {
        mDiagnosticEventsDisposable.dispose();
        mMalfunctionEventsDisposable.dispose();
    }

    protected void add(Disposable disposable) {
        mDisposables.add(disposable);
    }

    protected CompositeDisposable getDisposables() {
        return mDisposables;
    }

    protected UserInteractor getUserInteractor() {
        return mUserInteractor;
    }

    protected ELDEventsInteractor getEventsInteractor() {
        return mEventsInteractor;
    }

    protected DutyTypeManager getDutyTypeManager() {
        return mDutyTypeManager;
    }

    @Override
    public void onUserChanged() {
        if (!mAccountManager.isCurrentUserDriver()) {
            Disposable disposable = Single.fromCallable(mUserInteractor::getFullUserNameSync)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(name -> getView().showCoDriverView(name));
            mDisposables.add(disposable);
        } else {
            getView().hideCoDriverView();
        }
    }

    @Override
    public void onDriverChanged() {}
}
