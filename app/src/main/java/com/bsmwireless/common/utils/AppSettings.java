package com.bsmwireless.common.utils;

import com.bsmwireless.common.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AppSettings {

    @Inject
    public AppSettings() {
    }

    /**
     * Returns difference between ELD time and UTC for generating malfunction event
     * @return diff in millisecond
     */
    public long getTimingMalfunctionDiff(){
        return Constants.DIFF_FOR_TRIGGER_TIMING_MALFUNCTION_MS;
    }

    /**
     * Returns time interval for running a timing check
     * @return time interval in milliseconds
     */
    public long getIntervalForCheckTime(){
        return Constants.CHECK_TIME_INTERVAL;
    }

    public long lockScreenIdlingTimeout(){
        return Constants.LOCK_SCREEN_IDLE_MONITORING_TIMEOUT_MS;
    }

    public long lockScreenDisconnectionTimeout(){
        return Constants.LOCK_SCREEN_DISCONNECTION_TIMEOUT_MS;
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
        return Constants.DEFAULT_STORAGE_CAPACITY_THRESHOLD;
    }
}
