package com.bsmwireless.common.utils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class SettingsManager {

    @Inject
    public SettingsManager() {
    }

    /**
     * Returns difference between ELD time and UTC for generating malfunction event
     * @return diff in millisecond
     */
    public long getTimingMalfunctionDiff(){
        return TimeUnit.MINUTES.toMillis(10);
    }

    /**
     * Returns time interval for running a timing check
     * @return time interval in milliseconds
     */
    public long getIntervalForCheckTime(){
        return TimeUnit.MINUTES.toMillis(1);
    }

    /**
     * Returns time interval for running a storage capacity check
     * @return time interval
     */
    public long getIntervalForCheckStorageCapacity(){
        // just the same interval
        return getIntervalForCheckTime();
    }

    /**
     * Return threshold in percent for triggered Data Recording Malfunction
     * 0.00 is 0%, 1.00 is 100%
     * For example, the threshold 0.05 means check should be triggered when available space
     * less than 5%  of total space
     * @return threshold in percent
     */
    public double getFreeSpaceThreshold(){
        return 0.05;
    }
}
