package com.bsmwireless.screens.logs;

public class TripInfo {

    private String mOffDutyTime = "";
    private String mSleeperBerthTime = "";
    private String mDrivingTime = "-";
    private String mOnDutyTime = "-";
    private String mCoDriverValue = "-";
    private UnitType mUnitType = UnitType.KM;
    private int mOdometerValue;


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

    public String getCoDriverValue() {
        return mCoDriverValue;
    }

    public void setCoDriverValue(String coDriverValue) {
        mCoDriverValue = coDriverValue;
    }

    public UnitType getUnitType() {
        return mUnitType;
    }

    public void setUnitType(UnitType unitType) {
        mUnitType = unitType;
    }

    public int getOdometerValue() {
        return mOdometerValue;
    }

    public void setOdometerValue(int odometerValue) {
        mOdometerValue = odometerValue;
    }

    public enum UnitType {
        KM,
        ML
    }
}
