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
public final class DiagnosticPresenter {

    public enum EventType {DIAGNOSTIC, MALFUNCTION}

    private final ELDEventsInteractor mEldEventsInteractor;
    private final DiagnosticView mView;
    private final EventType mEventType;
    private final AccountManager mAccountManager;
    private final UserInteractor mUserInteractor;
    private Disposable mLoadingEventsDisposable;

    @Inject
    public DiagnosticPresenter(ELDEventsInteractor eldEventsInteractor,
                               DiagnosticView view,
                               EventType eventType,
                               AccountManager accountManager,
                               UserInteractor userInteractor) {
        mEldEventsInteractor = eldEventsInteractor;
        mView = view;
        mEventType = eventType;
        mAccountManager = accountManager;
        mUserInteractor = userInteractor;
        mLoadingEventsDisposable = Disposables.disposed();
    }

    public void onDestroyed() {
        mLoadingEventsDisposable.dispose();
    }

    public void onCreated() {
        mLoadingEventsDisposable.dispose();
        mLoadingEventsDisposable = Flowable
                .defer(() -> {
                    switch (mEventType) {
                        case DIAGNOSTIC:
                            return mEldEventsInteractor.getDiagnosticEvents();
                        case MALFUNCTION:
                            return mEldEventsInteractor.getMalfunctionEvents();
                        default:
                            return Flowable.error(new Exception("Unknown event type: " + mEventType));
                    }
                })
                .zipWith(getUser().toFlowable(), Result::new)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.mEvents.isEmpty()) {
                        mView.showNoEvents();
                    } else {
                        mView.showEvents(result.mEvents, result.mUser.getTimezone());
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error loading mEvents");
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
        final List<ELDEvent> mEvents;
        final UserEntity mUser;

        private Result(List<ELDEvent> events, UserEntity user) {
            this.mEvents = events;
            this.mUser = user;
        }
    }
}
