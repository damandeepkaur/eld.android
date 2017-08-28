package com.bsmwireless.data.storage;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bsmwireless.common.utils.SchedulerUtils;
import com.bsmwireless.domain.interactors.BlackBoxInteractor;
import com.bsmwireless.domain.interactors.ELDEventsInteractor;
import com.bsmwireless.models.BlackBoxModel;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_MIN;

public class AutoDutyTypeManager implements DutyTypeManager.DutyTypeListener {
    private static final int AUTO_ON_DUTY_DELAY = 5 * MS_IN_MIN;

    private BlackBoxInteractor mBlackBoxInteractor;
    private ELDEventsInteractor mEventsInteractor;
    private DutyTypeManager mDutyTypeManager;
    private PreferencesManager mPreferencesManager;

    private Disposable mBlackBoxDisposable;

    private final ArrayList<AutoDutyTypeListener> mListeners = new ArrayList<>();

    private Handler mHandler = new Handler();
    private Runnable mAutoOnDutyTask = () -> {
        synchronized (mListeners) {
            for (AutoDutyTypeListener listener : mListeners) {
                listener.onAutoOnDuty();
            }
        }
    };

    private Runnable mAutoDrivingTask = () -> {
        synchronized (mListeners) {
            for (AutoDutyTypeListener listener : mListeners) {
                listener.onAutoDriving();
            }
        }
    };

    public AutoDutyTypeManager(BlackBoxInteractor blackBoxInteractor, PreferencesManager preferencesManager, ELDEventsInteractor eventsInteractor, DutyTypeManager dutyTypeManager) {
        mBlackBoxInteractor = blackBoxInteractor;
        mEventsInteractor = eventsInteractor;
        mDutyTypeManager = dutyTypeManager;
        mPreferencesManager = preferencesManager;

        mDutyTypeManager.addListener(this);
        SchedulerUtils.schedule();
    }

    public void validateBlackBoxState() {
        int boxId = mPreferencesManager.getBoxId();
        if (boxId != 0) {
            validateBlackBoxState(boxId);
        }
    }

    public void validateBlackBoxState(int boxId) {
        if (mBlackBoxDisposable != null) {
            mBlackBoxDisposable.dispose();
        }

        mBlackBoxDisposable = (mBlackBoxInteractor.getData(boxId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        blackBoxState -> processBlackBoxState(blackBoxState),
                        error -> Timber.e("BlackBox error: %s", error)
        ));
    }

    private void processBlackBoxState(BlackBoxModel blackBoxState) {
        ArrayList<ELDEvent> events = new ArrayList<>();

        switch (blackBoxState.getResponseType()) {
            //4.3.2.2.2 Driver's indication of situations impacting drive time recording
            case IGNITION_OFF:
                events.add(mEventsInteractor.getEvent(mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE ?
                        ELDEvent.EnginePowerCode.SHUT_DOWN_REDUCE_DECISION :
                        ELDEvent.EnginePowerCode.SHUT_DOWN));

                if (mDutyTypeManager.getDutyType() == DutyType.YARD_MOVES) {
                    events.add(mEventsInteractor.getEvent(DutyType.CLEAR, true));
                }
                break;

            //4.3.2.2.2 Driver's indication of situations impacting drive time recording
            case IGNITION_ON:
                events.add(mEventsInteractor.getEvent(mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE ?
                        ELDEvent.EnginePowerCode.POWER_UP_REDUCE_DECISION :
                        ELDEvent.EnginePowerCode.POWER_UP));

                mHandler.removeCallbacks(mAutoDrivingTask);

                if (mDutyTypeManager.getDutyType() == DutyType.PERSONAL_USE) {
                    mHandler.post(mAutoDrivingTask);
                }
                break;

            //4.4.1.1 Automatic Setting of Duty Status to Driving
            case MOVING:
                SchedulerUtils.cancel();

                if (mDutyTypeManager.getDutyType() != DutyType.PERSONAL_USE && mDutyTypeManager.getDutyType() != DutyType.YARD_MOVES) {
                    events.add(mEventsInteractor.getEvent(DutyType.DRIVING, true));
                }
                break;

            // 4.4.1.2
            // When the duty status is set to driving, and the CMV has not been in-motion for 5 consecutive minutes,
            // the ELD must prompt the driver to confirm continued driving status or enter the proper duty status.
            case STOPPED:
                mHandler.removeCallbacks(mAutoOnDutyTask);

                if (mDutyTypeManager.getDutyType() == DutyType.DRIVING) {
                    mHandler.postDelayed(mAutoOnDutyTask, AUTO_ON_DUTY_DELAY);
                } else {
                    SchedulerUtils.schedule();
                }
                break;

            case STATUS_UPDATE:
                break;
        }

        //TODO: change to putting in data base
        if (!events.isEmpty()) {
            mEventsInteractor.postNewELDEvents(events).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    public void addListener(@NonNull AutoDutyTypeListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    public void removeListener(@NonNull AutoDutyTypeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void onDutyTypeChanged(DutyType dutyType) {
        mHandler.removeCallbacks(mAutoOnDutyTask);

        if (dutyType == DutyType.DRIVING && mBlackBoxInteractor.getLastData().getSpeed() == 0) {
            mHandler.removeCallbacks(mAutoDrivingTask);
            mHandler.postDelayed(mAutoOnDutyTask, AUTO_ON_DUTY_DELAY);
        }
    }

    public interface AutoDutyTypeListener {
        void onAutoOnDuty();
        void onAutoDriving();
    }
}
