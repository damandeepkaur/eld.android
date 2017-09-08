package com.bsmwireless.screens.diagnostic;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.data.storage.AccountManager;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.domain.interactors.UserInteractor;
import com.bsmwireless.models.ELDEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ActivityScope
public class DiagnosticPresenter {

    public enum EventType {DIAGNOSTIC, MALFUNCTION}

    private final ELDEventsInteractor mEldEventsInteractor;
    private final DiagnosticView mView;
    private final EventType eventType;
    private final AccountManager mAccountManager;
    private final UserInteractor mUserInteractor;
    private Disposable loadingEventsDisposable;

    @Inject
    public DiagnosticPresenter(ELDEventsInteractor eldEventsInteractor,
                               DiagnosticView view,
                               EventType eventType,
                               AccountManager accountManager,
                               UserInteractor userInteractor) {
        this.mEldEventsInteractor = eldEventsInteractor;
        this.mView = view;
        this.eventType = eventType;
        this.mAccountManager = accountManager;
        this.mUserInteractor = userInteractor;
        loadingEventsDisposable = Disposables.disposed();
    }

    public void onDestroyed() {
        loadingEventsDisposable.dispose();
    }

    public void onCreated() {
        loadingEventsDisposable.dispose();
        loadingEventsDisposable = Flowable
                .defer(() -> {
                    switch (eventType) {
                        case DIAGNOSTIC:
                            return mEldEventsInteractor.getDiagnosticEvents();
                        case MALFUNCTION:
                            return mEldEventsInteractor.getMalfunctionEvents();
                        default:
                            return Flowable.error(new Exception("Unknown event type: " + eventType));
                    }
                })
                .zipWith(getUser().toFlowable(), Result::new)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.events.isEmpty()) {
                        mView.showNoEvents();
                    } else {
                        mView.showEvents(result.events, result.user.getTimezone());
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error loading events");
                    mView.showNoEvents();
                });
    }

    private Single<UserEntity> getUser() {
        return Single.fromCallable(() -> {
            int currentUserId = mAccountManager.getCurrentUserId();
            return mUserInteractor.getUserFromDBSync(currentUserId);
        });
    }

    private final static class Result {
        final List<ELDEvent> events;
        final UserEntity user;

        private Result(List<ELDEvent> events, UserEntity user) {
            this.events = events;
            this.user = user;
        }
    }
}
