package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DriverStatus implements Parcelable {

    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("boxid")
    @Expose
    private Integer mBoxId;
    @SerializedName("datetime")
    @Expose
    private Long mDateTime;
    @SerializedName("todaysec")
    @Expose
    private Integer mTodaySec;
    @SerializedName("totalsec")
    @Expose
    private Integer mTotalSec;
    @SerializedName("availablesec")
    @Expose
    private Integer mAvailableSec;
    @SerializedName("cycle")
    @Expose
    private String mCycle;

    public final static Parcelable.Creator<DriverStatus> CREATOR = new Creator<DriverStatus>() {

        @SuppressWarnings({
            "unchecked"
        })
        public DriverStatus createFromParcel(Parcel in) {
            DriverStatus instance = new DriverStatus();
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDateTime = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mTodaySec = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mTotalSec = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mAvailableSec = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mCycle = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public DriverStatus[] newArray(int size) {
            return (new DriverStatus[size]);
        }

    };

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        this.mBoxId = boxId;
    }

    public Long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Long dateTime) {
        this.mDateTime = dateTime;
    }

    public Integer getTodaySec() {
        return mTodaySec;
    }

    public void setTodaySec(Integer todaySec) {
        this.mTodaySec = todaySec;
    }

    public Integer getTotalSec() {
        return mTotalSec;
    }

    public void setTotalSec(Integer totalSec) {
        this.mTotalSec = totalSec;
    }

    public Integer getAvailableSec() {
        return mAvailableSec;
    }

    public void setAvailableSec(Integer availableSec) {
        this.mAvailableSec = availableSec;
    }

    public String getCycle() {
        return mCycle;
    }

    public void setCycle(String cycle) {
        this.mCycle = cycle;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DriverStatus{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mDateTime=").append(mDateTime);
        sb.append(", mTodaySec=").append(mTodaySec);
        sb.append(", mTotalSec=").append(mTotalSec);
        sb.append(", mAvailableSec=").append(mAvailableSec);
        sb.append(", mCycle='").append(mCycle).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDriverId).append(mBoxId).append(mDateTime).append(mTodaySec).append(mTotalSec).append(mAvailableSec).append(mCycle).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DriverStatus)) {
            return false;
        }
        DriverStatus rhs = ((DriverStatus) other);
        return new EqualsBuilder().append(mDriverId, rhs.mDriverId).append(mBoxId, rhs.mBoxId).append(mDateTime, rhs.mDateTime).append(mTodaySec, rhs.mTodaySec).append(mTotalSec, rhs.mTotalSec).append(mAvailableSec, rhs.mAvailableSec).append(mCycle, rhs.mCycle).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mDriverId);
        dest.writeValue(mBoxId);
        dest.writeValue(mDateTime);
        dest.writeValue(mTodaySec);
        dest.writeValue(mTotalSec);
        dest.writeValue(mAvailableSec);
        dest.writeValue(mCycle);
    }

    public int describeContents() {
        return  0;
    }
}
