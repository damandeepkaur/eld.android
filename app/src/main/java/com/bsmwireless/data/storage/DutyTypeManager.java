package com.bsmwireless.data.storage;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public static final List<DutyType> DRIVER_DUTY_EXTENDED_WITH_CLEAR = createDataTypeList(ON_DUTY, OFF_DUTY, SLEEPER_BERTH, DRIVING, YARD_MOVES, PERSONAL_USE, CLEAR);
    @SuppressWarnings("PublicStaticCollectionField")
    public static final List<DutyType> DRIVER_DUTY = createDataTypeList(ON_DUTY, OFF_DUTY, SLEEPER_BERTH, DRIVING);
    @SuppressWarnings("PublicStaticCollectionField")
    public static final List<DutyType> CO_DRIVER_DUTY = createDataTypeList(ON_DUTY, OFF_DUTY, SLEEPER_BERTH);

    private PreferencesManager mPreferencesManager;

    private DutyType mDutyType = OFF_DUTY;
    private long mStart = DateUtils.currentTimeMillis();

    private final ArrayList<DutyTypeListener> mListeners = new ArrayList<>();

    private Handler mHandler = new Handler();
    private Runnable mNotifyTask = () -> {
        synchronized (mListeners) {
            for (DutyTypeListener listener : mListeners) {
                listener.onDutyTypeChanged(mDutyType);
            }
        }
    };

    private static List<DutyType> createDataTypeList(DutyType... array) {
        return Collections.unmodifiableList(Arrays.asList(array));
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
        } else if (dutyType == CLEAR) {
            if (mDutyType == PERSONAL_USE) {
                dutyType = OFF_DUTY;
            } else if (mDutyType == YARD_MOVES) {
                dutyType = ON_DUTY;
            } else {
                dutyType = mDutyType;
            }
        }

        long current = DateUtils.currentTimeMillis();

        if (setTime) {
            setDutyTypeTime(mDutyType, (int) ((current - mStart) / MS_IN_SEC));
        }

        mDutyType = dutyType;
        mStart = current;
        mPreferencesManager.setDutyType(dutyType.ordinal());

        notifyListeners();
    }

    public void resetTime(List<ELDEvent> events, long startOfDay) {
        DutyType dutyType = DutyType.OFF_DUTY;
        DutyType eventDutyType;
        ELDEvent event;

        boolean isClear = false;

        for (int i = events.size() - 1; i >= 0; i--) {
            event = events.get(i);

            if (event.isDutyEvent() && event.isActive()) {
                eventDutyType = DutyType.getDutyTypeByCode(event.getEventType(), event.getEventCode());

                if (eventDutyType == DutyType.CLEAR) {
                    isClear = true;

                //find previous duty event with actual status
                } else if (isClear) {
                    switch (eventDutyType) {
                        case PERSONAL_USE:
                            dutyType = DutyType.OFF_DUTY;
                            break;

                        case YARD_MOVES:
                            dutyType = DutyType.ON_DUTY;
                            break;

                        default:
                            dutyType = eventDutyType;
                            break;
                    }
                    break;

                } else {
                    dutyType = eventDutyType;
                    break;
                }
            }
        }

        long[] times = DutyTypeManager.getDutyTypeTimes(new ArrayList<>(events), startOfDay, DateUtils.currentTimeMillis());

        setDutyTypeTime(
                (int) (times[DutyType.ON_DUTY.ordinal()]),
                (int) (times[DutyType.DRIVING.ordinal()]),
                (int) (times[DutyType.SLEEPER_BERTH.ordinal()]), dutyType
        );
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
            time += (DateUtils.currentTimeMillis() - mStart);
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
                currentDutyType = DutyType.getDutyTypeByCode(event.getEventType(), event.getEventCode());

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
