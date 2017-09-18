package com.bsmwireless.screens.multiday;

import com.bsmwireless.common.utils.DateUtils;

public final class MultidayItemModel {

    private static final String DEFAULT_TIME = "00:00";

    private String mTotalOffDuty = DEFAULT_TIME;
    private String mTotalSleeping = DEFAULT_TIME;
    private String mTotalDriving = DEFAULT_TIME;
    private String mTotalOnDuty = DEFAULT_TIME;

    private long mTotalOffDutyTime;
    private long mTotalSleepingTime;
    private long mTotalDrivingTime;
    private long mTotalOnDutyTime;

    private String mDay;

    private long mStartOfDay;

    public MultidayItemModel(long startOfDay) {
        mStartOfDay = startOfDay;
    }

    public String getTotalOffDuty() {
        return mTotalOffDuty;
    }

    public void setTotalOffDuty(long totalOffDuty) {
        mTotalOffDuty = DateUtils.convertTotalTimeInMsToStringTime(totalOffDuty);
        mTotalOffDutyTime = totalOffDuty;
    }

    public String getTotalSleeping() {
        return mTotalSleeping;
    }

    public void setTotalSleeping(long totalSleeping) {
        mTotalSleeping = DateUtils.convertTotalTimeInMsToStringTime(totalSleeping);
        mTotalSleepingTime = totalSleeping;
    }

    public String getTotalDriving() {
        return mTotalDriving;
    }

    public void setTotalDriving(long totalDriving) {
        mTotalDriving = DateUtils.convertTotalTimeInMsToStringTime(totalDriving);
        mTotalDrivingTime = totalDriving;
    }

    public String getTotalOnDuty() {
        return mTotalOnDuty;
    }

    public void setTotalOnDuty(long totalOnDuty) {
        mTotalOnDuty = DateUtils.convertTotalTimeInMsToStringTime(totalOnDuty);
        mTotalOnDutyTime = totalOnDuty;
    }

    public String getDay() {
        return mDay;
    }

    public void setDay(String day) {
        mDay = day;
    }

    public long getTotalOffDutyTime() {
        return mTotalOffDutyTime;
    }

    public long getTotalSleepingTime() {
        return mTotalSleepingTime;
    }

    public long getTotalDrivingTime() {
        return mTotalDrivingTime;
    }

    public long getTotalOnDutyTime() {
        return mTotalOnDutyTime;
    }

    public long getStartOfDay() {
        return mStartOfDay;
    }
}
