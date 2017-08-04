package com.bsmwireless.screens.logs;

public class TripInfo {
    private String mCoDriverValue;
    private String mOnDutyLeftValue;
    private String mDriveValue;
    private UnitType mUnitType;
    private int mOdometerValue;

    public String getCoDriverValue() {
        return mCoDriverValue;
    }

    public void setCoDriverValue(String coDriverValue) {
        mCoDriverValue = coDriverValue;
    }

    public String getOnDutyLeftValue() {
        return mOnDutyLeftValue;
    }

    public void setOnDutyLeftValue(String onDutyLeftValue) {
        mOnDutyLeftValue = onDutyLeftValue;
    }

    public String getDriveValue() {
        return mDriveValue;
    }

    public void setDriveValue(String driveValue) {
        mDriveValue = driveValue;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TripInfo{");
        sb.append("mCoDriverValue='").append(mCoDriverValue).append('\'');
        sb.append(", mOnDutyLeftValue='").append(mOnDutyLeftValue).append('\'');
        sb.append(", mDriveValue='").append(mDriveValue).append('\'');
        sb.append(", mUnitType=").append(mUnitType);
        sb.append(", mOdometerValue=").append(mOdometerValue);
        sb.append('}');
        return sb.toString();
    }

    public enum UnitType {
        KM,
        ML
    }
}
