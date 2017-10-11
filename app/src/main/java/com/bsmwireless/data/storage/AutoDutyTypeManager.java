package com.bsmwireless.data.storage;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.bsmwireless.common.utils.AppSettings;
import com.bsmwireless.common.utils.BlackBoxStateChecker;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.data.network.blackbox.BlackBoxConnectionException;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class AutoDutyTypeManager implements DutyTypeManager.DutyTypeListener {

    private BlackBoxInteractor mBlackBoxInteractor;
    private ELDEventsInteractor mEventsInteractor;
    private DutyTypeManager mDutyTypeManager;
    private PreferencesManager mPreferencesManager;
    private AppSettings mAppSettings;
    private BlackBoxStateChecker mBlackBoxStateChecker;

    private Disposable mBlackBoxDisposable;

    private AutoDutyTypeListener mListener = null;
    private OnIgnitionOffListener mIgnitionOffListener;
    private OnStoppedListener mOnStoppedListener;
    private OnMovingListener mOnMovingListener;
    private OnDisconnectListener mOnDisconnectListener;

    private volatile DutyType mDutyType;
    private volatile BlackBoxModel mBlackBoxModel;

    private Handler mHandler = new Handler();
    private Runnable mAutoOnDutyTask = new Runnable() {
        @Override
        public void run() {
            if (mBlackBoxModel == null) {
                Timber.w("AutoOnDutyTask is triggered but blackbox model is empty");
                return;
            }

            if (mListener != null) {
                mListener.onAutoOnDuty(mBlackBoxModel.getEventTimeUTC().getTime());
                mBlackBoxModel.setEventTimeUTC(new Date(DateUtils.currentTimeMillis()));
                mHandler.postDelayed(this, mAppSettings.lockScreenIdlingTimeout());
            }
        }
    };

    private Runnable mStoppedInNotDrivingDutyTask = () -> {
        if (mOnStoppedListener != null) {
            mOnStoppedListener.onStopped();
        }
    };

    private Runnable mAutoDrivingTask = () -> {
        if (mListener != null) {
            mListener.onAutoDriving();
        }
    };

    public AutoDutyTypeManager(BlackBoxInteractor blackBoxInteractor,
                               PreferencesManager preferencesManager,
                               ELDEventsInteractor eventsInteractor,
                               DutyTypeManager dutyTypeManager,
                               AppSettings appSettings,
                               BlackBoxStateChecker blackBoxStateChecker) {
        mBlackBoxInteractor = blackBoxInteractor;
        mEventsInteractor = eventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mPreferencesManager = preferencesManager;
        mAppSettings = appSettings;
        mBlackBoxStateChecker = blackBoxStateChecker;
        mBlackBoxModel = blackBoxInteractor.getLastData();
        SchedulerUtils.schedule();
    }

    public void doSubscribe() {
        mDutyTypeManager.addListener(this);
    }

    public void validateBlackBoxState() {
        int boxId = mPreferencesManager.getBoxId();
        if (boxId != PreferencesManager.NOT_FOUND_VALUE) {
            validateBlackBoxState(boxId);
        }
    }

    public void validateBlackBoxState(int boxId) {
        if (mBlackBoxDisposable != null) {
            mBlackBoxDisposable.dispose();
        }

        mBlackBoxDisposable = mBlackBoxInteractor.getData(boxId)
                .subscribeOn(Schedulers.io())
                .subscribe(this::processBlackBoxState, error -> {
                    Timber.e("BlackBox error: %s", error);
                    if (error instanceof BlackBoxConnectionException) {
                        // disconnect detected
                        handleDisconnect();
                    }
                });
    }

    private void processBlackBoxState(BlackBoxModel blackBoxState) {
        List<ELDEvent> events = new ArrayList<>();

        switch (blackBoxState.getResponseType()) {
            //4.3.2.2.2 Driver's indication of situations impacting drive time recording
            case IGNITION_OFF:
                events = handleIgnitionOff(blackBoxState);
                break;

            //4.3.2.2.2 Driver's indication of situations impacting drive time recording
            case IGNITION_ON:
                events = handleIgnitionOn(blackBoxState);
                break;

            //4.4.1.1 Automatic Setting of Duty Status to Driving
            case MOVING:
                events = handleMoving(blackBoxState);
                break;

            // 4.4.1.2
            // When the duty status is set to driving, and the CMV has not been in-motion for 5 consecutive minutes,
            // the ELD must prompt the driver to confirm continued driving status or enter the proper duty status.
            case STOPPED:
                handleStopped(blackBoxState);
                break;

            case STATUS_UPDATE:
                break;
        }

        saveEvents(events);
    }

    public void setListener(@NonNull AutoDutyTypeListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;

        clearStoppedTasks();
        mHandler.removeCallbacks(mAutoDrivingTask);
    }

    @Override
    public void onDutyTypeChanged(DutyType dutyType) {
        if (mDutyType != dutyType) {
            mDutyType = dutyType;

            clearStoppedTasks();

            if (dutyType == DutyType.DRIVING && mBlackBoxInteractor.getLastData().getSpeed() == 0) {
                mHandler.removeCallbacks(mAutoDrivingTask);
                if (mBlackBoxModel != null) {
                    mBlackBoxModel.setEventTimeUTC(new Date(DateUtils.currentTimeMillis()));
                }
                mHandler.postDelayed(mAutoOnDutyTask, mAppSettings.lockScreenIdlingTimeout());
            }
        }
    }

    private List<ELDEvent> handleIgnitionOff(BlackBoxModel blackBoxModel) {
        mBlackBoxModel = blackBoxModel;

        List<ELDEvent> events = new ArrayList<>();

        DutyType dutyType = mDutyTypeManager.getDutyType();
        events.add(mEventsInteractor.getEvent(dutyType == DutyType.PERSONAL_USE ?
                ELDEvent.EnginePowerCode.SHUT_DOWN_REDUCE_DECISION :
                ELDEvent.EnginePowerCode.SHUT_DOWN));

        switch (dutyType) {

            case YARD_MOVES:
                events.add(mEventsInteractor.getEvent(DutyType.CLEAR, null, true));
                break;

            case DRIVING:
                events.add(mEventsInteractor.getEvent(DutyType.ON_DUTY));
                break;

            default:
        }

        if (mIgnitionOffListener != null) {
            mIgnitionOffListener.onIgnitionOff(dutyType);
        }

        return events;
    }

    private void handleStopped(BlackBoxModel blackBoxModel) {
        mBlackBoxModel = blackBoxModel;
        clearStoppedTasks();
        mHandler.postDelayed(mStoppedInNotDrivingDutyTask, mAppSettings.lockScreenIdlingTimeout());
        if (mDutyTypeManager.getDutyType() == DutyType.DRIVING) {
            mHandler.postDelayed(mAutoOnDutyTask, mAppSettings.lockScreenIdlingTimeout());
        } else {
            SchedulerUtils.schedule();
        }
    }

    private List<ELDEvent> handleMoving(BlackBoxModel blackBoxState) {
        mBlackBoxModel = blackBoxState;
        SchedulerUtils.cancel();

        clearStoppedTasks();

        DutyType dutyTypeCurr = mDutyTypeManager.getDutyType();
        List<ELDEvent> eldEvents = new ArrayList<>();
        if (dutyTypeCurr != DutyType.PERSONAL_USE && dutyTypeCurr != DutyType.YARD_MOVES && dutyTypeCurr != DutyType.DRIVING) {
            eldEvents.add(mEventsInteractor.getEvent(DutyType.DRIVING, null, true));
        }

        if (mListener != null) {
            mListener.onAutoDrivingWithoutConfirm();
        }

        if (mOnMovingListener != null) {
            mOnMovingListener.onMoving();
        }
        return eldEvents;
    }

    private List<ELDEvent> handleIgnitionOn(BlackBoxModel blackBoxModel) {
        mBlackBoxModel = blackBoxModel;
        List<ELDEvent> events = new ArrayList<>(1);
        events.add(mEventsInteractor.getEvent(mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE ?
                ELDEvent.EnginePowerCode.POWER_UP_REDUCE_DECISION :
                ELDEvent.EnginePowerCode.POWER_UP));

        mHandler.removeCallbacks(mAutoDrivingTask);

        if (mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE) {
            mHandler.post(mAutoDrivingTask);
        }
        return events;
    }

    private void handleDisconnect() {

        // Create on-duty event when disconnect is detected
        Single.fromCallable(() -> mEventsInteractor.getEvent(DutyType.ON_DUTY))
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::startReconnect)
                .subscribe(this::validateBlackBoxState, throwable -> Timber.e(throwable, "Reconnection error"));
    }

    private Completable startReconnect(ELDEvent eldEvent) {
        return mBlackBoxInteractor.getData(mPreferencesManager.getBoxId())
                .firstOrError()
                .toCompletable()
                .timeout(mAppSettings.lockScreenDisconnectionTimeout(), TimeUnit.MILLISECONDS)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof TimeoutException || throwable instanceof BlackBoxConnectionException) {
                        // and show disconnection popup and continue monitoring
                        return Completable
                                // If timeout is occurs save ON_DUTY for DRIVING
                                .defer(() -> {
                                    DutyType dutyType = mDutyTypeManager.getDutyType();
                                    Completable completable;
                                    if (dutyType == DutyType.DRIVING) {
                                        completable = createPostNewEventCompletable(eldEvent);

                                    } else {
                                        completable = Completable.complete();
                                    }
                                    return completable.andThen(Completable.fromAction(() -> {
                                        if (mOnDisconnectListener != null) {
                                            mOnDisconnectListener.onDisconnect();
                                        }
                                    }));
                                })
                                .andThen(createReconnectionCompletable());
                    }
                    return Completable.error(throwable);
                });
    }

    private Completable createReconnectionCompletable() {
        return mBlackBoxInteractor.getData(mPreferencesManager.getBoxId())
                .firstElement()
                .flatMapCompletable(blackBoxModel -> Completable.defer(() -> {
                    // At this moment ON_DUTY event is in database
                    // Switch to driving if box in driving mode
                    DutyType dutyType = mDutyTypeManager.getDutyType();
                    if (mBlackBoxStateChecker.isMoving(blackBoxModel)
                            && dutyType != DutyType.PERSONAL_USE && dutyType != DutyType.YARD_MOVES) {
                        return Single
                                .fromCallable(() -> mEventsInteractor.getEvent(DutyType.DRIVING))
                                .flatMapCompletable(this::createPostNewEventCompletable);
                    }
                    return Completable.complete();
                }));
    }

    private Completable createPostNewEventCompletable(ELDEvent eldEventInfo) {
        return mEventsInteractor.postNewELDEvent(eldEventInfo)
                .toCompletable()
                .onErrorComplete(throwable -> {
                    Timber.e(throwable, "Post new duty event error");
                    return true;
                });
    }

    private void saveEvents(List<ELDEvent> events) {

        if (events.isEmpty()) {
            return;
        }

        mEventsInteractor
                .postNewELDEvents(events)
                .flatMapCompletable(ids -> Completable.defer(() -> {
                    // Check events from last and if duty event is exist change duty status
                    for (int i = events.size() - 1; i >= 0; i--) {
                        ELDEvent eldEvent = events.get(i);
                        if (eldEvent.isDutyEvent() && eldEvent.isActive()) {
                            return Completable.fromAction(() -> {
                                DutyType dutyType = DutyType
                                        .getDutyTypeByCode(eldEvent.getEventType(), eldEvent.getEventCode());
                                mDutyTypeManager.setDutyType(dutyType, true);
                            });
                        }
                    }
                    return Completable.complete();
                }))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void clearStoppedTasks() {
        mHandler.removeCallbacks(mAutoOnDutyTask);
        mHandler.removeCallbacks(mStoppedInNotDrivingDutyTask);
    }

    public void setOnIgnitionOffListener(OnIgnitionOffListener listener) {
        mIgnitionOffListener = listener;
    }

    public void setOnStoppedListener(OnStoppedListener listener) {
        mOnStoppedListener = listener;
    }

    public void setOnMovingListener(OnMovingListener onMovingListener) {
        mOnMovingListener = onMovingListener;
    }

    public void setOnDisconnectListener(OnDisconnectListener onDisconnectListener) {
        mOnDisconnectListener = onDisconnectListener;
    }

    public interface AutoDutyTypeListener {
        void onAutoOnDuty(long stoppedTime);

        void onAutoDriving();

        void onAutoDrivingWithoutConfirm();
    }

    public interface OnIgnitionOffListener {
        /**
         * Called when ignition off is detected
         *
         * @param currentDutyType current duty type when ignition off is detected
         */
        void onIgnitionOff(DutyType currentDutyType);
    }

    public interface OnStoppedListener {
        void onStopped();
    }

    public interface OnMovingListener {
        void onMoving();
    }

    public interface OnDisconnectListener {
        void onDisconnect();
    }
}
