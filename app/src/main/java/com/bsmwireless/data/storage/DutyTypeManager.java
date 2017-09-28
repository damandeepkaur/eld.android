package com.bsmwireless.data.storage;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.bsm.sd.hos.Calculator;
import com.bsm.sd.hos.model.LogEvent;
import com.bsm.sd.hos.model.Result;
import com.bsm.sd.hos.model.RuleSelectionHst;
import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;
import com.bsmwireless.widgets.alerts.DutyType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.bsmwireless.common.utils.DateUtils.MS_IN_HOUR;
import static com.bsmwireless.common.utils.DateUtils.MS_IN_SEC;
import static com.bsmwireless.widgets.alerts.DutyType.CLEAR;
import static com.bsmwireless.widgets.alerts.DutyType.DRIVING;
import static com.bsmwireless.widgets.alerts.DutyType.OFF_DUTY;
import static com.bsmwireless.widgets.alerts.DutyType.ON_DUTY;
import static com.bsmwireless.widgets.alerts.DutyType.PERSONAL_USE;
import static com.bsmwireless.widgets.alerts.DutyType.SLEEPER_BERTH;
import static com.bsmwireless.widgets.alerts.DutyType.YARD_MOVES;

public final class DutyTypeManager {

    //Suppressed, but the problem is fixed by createDataTypeList funcrion which returns unmodifiable colleaction
    @SuppressWarnings("PublicStaticCollectionField")
    public static final List<DutyType> DRIVER_DUTY_EXTENDED = createDataTypeList(OFF_DUTY, SLEEPER_BERTH, DRIVING, ON_DUTY, PERSONAL_USE, YARD_MOVES);
    @SuppressWarnings("PublicStaticCollectionField")
    public static final List<DutyType> DRIVER_DUTY_EXTENDED_WITH_CLEAR = createDataTypeList(ON_DUTY, OFF_DUTY, SLEEPER_BERTH, DRIVING, YARD_MOVES, PERSONAL_USE, CLEAR);
    @SuppressWarnings("PublicStaticCollectionField")
    public static final List<DutyType> DRIVING_DUTY = createDataTypeList(OFF_DUTY, SLEEPER_BERTH, DRIVING, ON_DUTY);

    @SuppressWarnings("PublicStaticCollectionField")
    public static final List<DutyType> CO_DRIVER_DUTY_EXTENDED = createDataTypeList(OFF_DUTY, SLEEPER_BERTH, ON_DUTY, PERSONAL_USE, YARD_MOVES);
    @SuppressWarnings("PublicStaticCollectionField")
    public static final List<DutyType> CO_DRIVER_DUTY = createDataTypeList(OFF_DUTY, SLEEPER_BERTH, ON_DUTY);

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

    private static List createDataTypeList(DutyType... array) {
        ArrayList<DutyType> collection = new ArrayList<DutyType>(Arrays.asList(array));
        return Collections.unmodifiableList(collection);
    }

    public DutyTypeManager(PreferencesManager preferencesManager) {
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
                mPreferencesManager.setOnDutyTimeLeft(mPreferencesManager.getOnDutyTimeLeft() - time);
                break;

            case DRIVING:
                mPreferencesManager.setDrivingTime(mPreferencesManager.getDrivingTime() + time);
                mPreferencesManager.setDrivingTimeLeft(mPreferencesManager.getDrivingTimeLeft() - time);
                break;

            case SLEEPER_BERTH:
                mPreferencesManager.setSleeperBerthTime(mPreferencesManager.getSleeperBerthTime() + time);
                break;

            default:
                //TODO: validate cases
                mPreferencesManager.setCycleTimeLeft(mPreferencesManager.getCycleTimeLeft() - time);
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

            default:
                //TODO: return cycle time
                break;
        }

        if (mDutyType == dutyType) {
            time += (System.currentTimeMillis() - mStart);
        }

        return time;
    }

    public long getDutyTypeTimeLeft(DutyType dutyType) {
        long time;

        switch (dutyType) {
            case ON_DUTY:
            case YARD_MOVES:
                time = mPreferencesManager.getOnDutyTimeLeft();
                break;

            case DRIVING:
                time = mPreferencesManager.getDrivingTimeLeft();
                break;

            case SLEEPER_BERTH:
                time = 8 * MS_IN_HOUR - mPreferencesManager.getSleeperBerthTime();
                break;

            default:
                time = mPreferencesManager.getCycleTimeLeft();
                break;
        }

        if (mDutyType == dutyType) {
            time -= (System.currentTimeMillis() - mStart);
        }

        return Math.max(0, time);
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

    public static long[] getDutyTypeTimes(List<DutyTypeCheckable> events, long startTime, long endTime) {
        long offDutyTime = 0;
        long onDutyTime = 0;
        long drivingTime = 0;
        long sleeperBerthTime = 0;

        long currentTime = endTime;
        long duration;

        DutyType currentDutyType;
        DutyTypeCheckable event;

        for (int i = events.size() - 1; i >= 0; i--) {
            // all events from current day is checked
            if (currentTime < startTime) {
                break;
            }

            event = events.get(i);

            if (event.isDutyEvent() && event.isActive()) {
                currentDutyType = DutyType.getTypeByCode(event.getEventType(), event.getEventCode());

                if (currentDutyType == CLEAR) {
                    continue;
                }

                duration = currentTime - Math.max(event.getEventTime(), startTime);
                currentTime = event.getEventTime();

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

        return new long[]{onDutyTime, offDutyTime, sleeperBerthTime, drivingTime};
    }

    public boolean setDutyTypeTimeLeft(List<ELDEvent> events, User user) {
        Result result = null;
        long start = DateUtils.getStartDayTimeInMs(user.getTimezone(), System.currentTimeMillis());

        List<LogEvent> calculatorEvents = CalculatorConverter.eventListToCalculatorEventList(events);
        List<RuleSelectionHst> calculatorRules = CalculatorConverter.userToCalculatorRule(user, start);

        try {
            result = Calculator.getInstance(null).calculate(calculatorEvents, calculatorRules, new Timestamp(start), new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            Timber.e(e);
        }

        if (result == null) {
            mPreferencesManager.setOnDutyTimeLeft(0);
            mPreferencesManager.setCycleTimeLeft(0);
            mPreferencesManager.setDrivingTimeLeft(0);
        } else {
            mPreferencesManager.setOnDutyTimeLeft(result.getAvaliableOndutySecs() * 1000);
            mPreferencesManager.setCycleTimeLeft(result.getCycleAvaliable() * 1000);
            mPreferencesManager.setDrivingTimeLeft(result.getAvaliableDrivingSecs() * 1000);
        }

        return result != null;
    }

    private void notifyListeners() {
        mHandler.post(mNotifyTask);
    }

    public interface DutyTypeCheckable {
        Long getEventTime();

        Integer getEventType();

        Integer getEventCode();

        Boolean isActive();

        Boolean isDutyEvent();
    }

    public interface DutyTypeListener {
        void onDutyTypeChanged(DutyType dutyType);
    }
}
