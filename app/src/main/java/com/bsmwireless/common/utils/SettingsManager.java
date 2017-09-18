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
     * Return difference between ELD time and UTC for generating malfunction event
     * @return diff in millisecond
     */
    public long getTimingMalfunctionDiff(){
        return TimeUnit.MINUTES.toMillis(10);
    }
}
