package com.bsmwireless.screens.logs;

public class HOSTimesModel {
    private static final String DEFAULT_TIME = "00:00";

    private String mOffDutyTime;
    private String mSleeperBerthTime;
    private String mDrivingTime;
    private String mOnDutyTime;

    public HOSTimesModel() {
        mOffDutyTime = DEFAULT_TIME;
        mSleeperBerthTime = DEFAULT_TIME;
        mDrivingTime = DEFAULT_TIME;
        mOnDutyTime = DEFAULT_TIME;
    }

    public String getOffDutyTime() {
        return mOffDutyTime;
    }

    public void setOffDutyTime(String offDutyTime) {
        mOffDutyTime = offDutyTime;
    }

    public String getSleeperBerthTime() {
        return mSleeperBerthTime;
    }

    public void setSleeperBerthTime(String sleeperBerthTime) {
        mSleeperBerthTime = sleeperBerthTime;
    }

    public String getDrivingTime() {
        return mDrivingTime;
    }

    public void setDrivingTime(String drivingTime) {
        mDrivingTime = drivingTime;
    }

    public String getOnDutyTime() {
        return mOnDutyTime;
    }

    public void setOnDutyTime(String onDutyTime) {
        mOnDutyTime = onDutyTime;
    }
}
