package com.bsmwireless.data.storage;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_SEC;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR;
import static com.bsmwireless.widgets.alerts.DutyType.DRIVING;
import static com.bsmwireless.widgets.alerts.DutyType.OFF_DUTY;
import static com.bsmwireless.widgets.alerts.DutyType.ON_DUTY;
import static com.bsmwireless.widgets.alerts.DutyType.PERSONAL_USE;
import static com.bsmwireless.widgets.alerts.DutyType.SLEEPER_BERTH;
import static com.bsmwireless.widgets.alerts.DutyType.YARD_MOVES;

public class DutyManager {
    public static final DutyType[] DRIVER_DUTY_EXTENDED = {OFF_DUTY, SLEEPER_BERTH, DRIVING, ON_DUTY, PERSONAL_USE, YARD_MOVES};
    public static final DutyType[] DRIVER_DUTY_EXTENDED_WITH_CLEAR = {OFF_DUTY, SLEEPER_BERTH, DRIVING, ON_DUTY, PERSONAL_USE, YARD_MOVES, CLEAR};
    public static final DutyType[] DRIVING_DUTY = {OFF_DUTY, SLEEPER_BERTH, DRIVING, ON_DUTY};

    public static final DutyType[] CO_DRIVER_DUTY_EXTENDED = {OFF_DUTY, SLEEPER_BERTH, ON_DUTY, PERSONAL_USE, YARD_MOVES};
    public static final DutyType[] CO_DRIVER_DUTY = {OFF_DUTY, SLEEPER_BERTH, ON_DUTY};

    private PreferencesManager mPreferencesManager;

    private DutyType mDutyType = OFF_DUTY;
    private long mStart = System.currentTimeMillis();

    private final ArrayList<DutyTypeListener> mListeners = new ArrayList<>();

    private Handler mHandler = new Handler();
    private Runnable mNotifyTask = () -> {
        synchronized (mListeners) {
            for (DutyTypeListener listener : mListeners) {
                listener.onDutyTypeChanged(mDutyType);
            }
        }
    };

    public DutyManager(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
        mDutyType = DutyType.values()[mPreferencesManager.getDutyType()];
    }

    public void setDutyTypeTime(int onDuty, int driving, int sleeperBerth, DutyType dutyType) {
        mPreferencesManager.setOnDutyTime(onDuty);
        mPreferencesManager.setDrivingTime(driving);
        mPreferencesManager.setSleeperBerthTime(sleeperBerth);

        setDutyType(dutyType, false);
    }

    private void setDutyTypeTime(DutyType dutyType, int time) {
        switch (dutyType) {
            case ON_DUTY:
            case YARD_MOVES:
                mPreferencesManager.setOnDutyTime(mPreferencesManager.getOnDutyTime() + time);
                break;

            case DRIVING:
                mPreferencesManager.setDrivingTime(mPreferencesManager.getDrivingTime() + time);
                break;

            case SLEEPER_BERTH:
                mPreferencesManager.setSleeperBerthTime(mPreferencesManager.getSleeperBerthTime() + time);
                break;
        }
    }

    public void setDutyType(DutyType dutyType, boolean setTime) {
        if (dutyType == null) {
            dutyType = OFF_DUTY;
        }

        long current = System.currentTimeMillis();

        if (setTime) {
            setDutyTypeTime(mDutyType, (int) ((current - mStart) / MS_IN_SEC));
        }

        mDutyType = dutyType;
        mStart = current;
        mPreferencesManager.setDutyType(dutyType.ordinal());

        notifyListeners();
    }

    public DutyType getDutyType() {
        return mDutyType;
    }

    public long getDutyTypeTime(DutyType dutyType) {
        long time = 0;

        switch (dutyType) {
            case ON_DUTY:
            case YARD_MOVES:
                time = mPreferencesManager.getOnDutyTime();
                break;

            case DRIVING:
                time = mPreferencesManager.getDrivingTime();
                break;

            case SLEEPER_BERTH:
                time = mPreferencesManager.getSleeperBerthTime();
                break;
        }

        if (mDutyType == dutyType) {
            time += (System.currentTimeMillis() - mStart);
        }

        return time;
    }

    public void addListener(@NonNull DutyTypeListener listener) {
        mHandler.post(() -> {
            synchronized (mListeners) {
                mListeners.add(listener);
                listener.onDutyTypeChanged(mDutyType);
            }
        });
    }

    public void removeListener(@NonNull DutyTypeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    public static long[] getDutyTypeTimes(List<DutyCheckable> events, long startTime, long endTime) {
        long offDutyTime = 0;
        long onDutyTime = 0;
        long drivingTime = 0;
        long sleeperBerthTime = 0;

        long currentTime = endTime;
        long duration;

        DutyType currentDutyType = OFF_DUTY;
        DutyCheckable event;

        for (int i = events.size() - 1; i >= 0; i--) {
            // all events from current day is checked
            if (currentTime < startTime) {
                break;
            }

            event = events.get(i);

            if ((event.getEventType() == ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()
                    || event.getEventType() == ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue()) &&
                    event.isActive()) {
                duration = currentTime - Math.max(event.getEventTime(), startTime);
                currentTime = event.getEventTime();

                //for clear events keep the previous status
                if (event.getEventCode() != CLEAR.getCode()) {
                    currentDutyType = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());
                }

            } else {
                continue;
            }

            switch (currentDutyType) {
                case ON_DUTY:
                case YARD_MOVES:
                    onDutyTime += duration;
                    break;

                case DRIVING:
                    drivingTime += duration;
                    break;

                case SLEEPER_BERTH:
                    sleeperBerthTime += duration;
                    break;

                default:
                    offDutyTime += duration;
                    break;
            }
        }

        return new long[] {offDutyTime, sleeperBerthTime, drivingTime, onDutyTime};
    }

    private void notifyListeners() {
        mHandler.post(mNotifyTask);
    }

    public interface DutyCheckable {
        Long getEventTime();
        Integer getEventType();
        Integer getEventCode();
        Boolean isActive();
    }

    public interface DutyTypeListener {
        void onDutyTypeChanged(DutyType dutyType);
    }
}
