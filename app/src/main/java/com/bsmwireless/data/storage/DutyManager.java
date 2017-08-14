package com.bsmwireless.data.storage;

import android.content.SharedPreferences;

import com.bsmwireless.widgets.alerts.DutyType;

import static com.bsmwireless.common.Constants.MS_IN_SEC;
import static com.bsmwireless.data.storage.PreferencesManager.KEY_DUTY_TYPE;

public class DutyManager {
    private PreferencesManager mPreferencesManager;

    private DutyType mDutyType = DutyType.OFF_DUTY;
    private long mStart = System.currentTimeMillis();

    public DutyManager(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
        mDutyType = DutyType.values()[mPreferencesManager.getDutyType()];
    }

    public void setDutyTypeTime(int onDuty, int driving, int sleeperBerth) {
        mPreferencesManager.setOnDutyTime(onDuty);
        mPreferencesManager.setDrivingTime(driving);
        mPreferencesManager.setSleeperBerthTime(sleeperBerth);
    }

    public void setDutyType(DutyType dutyType) {
        mPreferencesManager.setDutyType(dutyType.ordinal());

        long current = System.currentTimeMillis();
        int time = (int) ((current - mStart) / MS_IN_SEC);

        switch (mDutyType) {
            case ON_DUTY:
            case YARD_MOVES:
                mPreferencesManager.setOnDutyTime(mPreferencesManager.getOnDutyTime() + time);
                break;

            case DRIVING:
            case PERSONAL_USE:
                mPreferencesManager.setDrivingTime(mPreferencesManager.getDrivingTime() + time);
                break;

            case SLEEPER_BERTH:
                mPreferencesManager.setSleeperBerthTime(mPreferencesManager.getSleeperBerthTime() + time);
                break;
        }

        mDutyType = dutyType;
        mStart = current;
    }

    public DutyType getDutyType() {
        return mDutyType;
    }

    public int getDutyTypeTime(DutyType dutyType) {
        int time = 0;

        switch (dutyType) {
            case ON_DUTY:
            case YARD_MOVES:
                time = mPreferencesManager.getOnDutyTime();
                break;

            case DRIVING:
            case PERSONAL_USE:
                time = mPreferencesManager.getDrivingTime();
                break;

            case SLEEPER_BERTH:
                time = mPreferencesManager.getSleeperBerthTime();
                break;
        }

        if (mDutyType == dutyType) {
            time += (int) ((System.currentTimeMillis() - mStart) / MS_IN_SEC);
        }

        return time;
    }

    public void resetTime() {
        mPreferencesManager.setDrivingTime(0);
        mPreferencesManager.setOnDutyTime(0);
        mPreferencesManager.setSleeperBerthTime(0);

        mPreferencesManager.setDutyType(mDutyType.ordinal());
    }

    public void addListener(DutyTypeListener listener) {
        mPreferencesManager.addListener(listener);
        listener.onDutyTypeChanged(mDutyType);
    }

    public void removeListener(DutyTypeListener listener) {
        mPreferencesManager.removeListener(listener);
    }

    public static abstract class DutyTypeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        public abstract void onDutyTypeChanged(DutyType dutyType);

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (KEY_DUTY_TYPE.equals(key)) {
                onDutyTypeChanged(DutyType.values()[sharedPreferences.getInt(key, 0)]);
            }
        }
    }
}
