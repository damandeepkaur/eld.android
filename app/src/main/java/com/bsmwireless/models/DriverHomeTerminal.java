package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class DriverHomeTerminal implements Parcelable {

    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("homeTermId")
    @Expose
    private Integer mHomeTermId;
    public final static Parcelable.Creator<DriverHomeTerminal> CREATOR = new Creator<DriverHomeTerminal>() {

        @SuppressWarnings({
            "unchecked"
        })
        public DriverHomeTerminal createFromParcel(Parcel in) {
            return new DriverHomeTerminal(in);
        }

        public DriverHomeTerminal[] newArray(int size) {
            return (new DriverHomeTerminal[size]);
        }

    };

    public DriverHomeTerminal() {}

    public DriverHomeTerminal(Parcel in) {
        mDriverId = in.readInt();
        mHomeTermId = in.readInt();
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Integer getHomeTermId() {
        return mHomeTermId;
    }

    public void setHomeTermId(Integer homeTermId) {
        this.mHomeTermId = homeTermId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DriverHomeTerminal{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mHomeTermId=").append(mHomeTermId);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(mDriverId)
                .append(mHomeTermId)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DriverHomeTerminal)) {
            return false;
        }
        DriverHomeTerminal rhs = ((DriverHomeTerminal) other);
        return new EqualsBuilder()
                .append(mDriverId, rhs.mDriverId)
                .append(mHomeTermId, rhs.mHomeTermId)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDriverId);
        dest.writeInt(mHomeTermId);
    }

    public int describeContents() {
        return  0;
    }

}
