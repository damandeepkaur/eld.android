package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class DriverSignature implements Parcelable {

    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("signature")
    @Expose
    private String mSignature;

    public final static Parcelable.Creator<DriverSignature> CREATOR = new Creator<DriverSignature>() {

        @SuppressWarnings({
            "unchecked"
        })
        public DriverSignature createFromParcel(Parcel in) {
            return new DriverSignature(in);
        }

        public DriverSignature[] newArray(int size) {
            return (new DriverSignature[size]);
        }

    };

    public DriverSignature() {}

    public DriverSignature(Parcel in) {
        mDriverId = in.readInt();
        mSignature = in.readString();
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public DriverSignature setDriverId(Integer driverId) {
        this.mDriverId = driverId;
        return this;
    }

    public String getSignature() {
        return mSignature;
    }

    public DriverSignature setSignature(String signature) {
        this.mSignature = signature;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DriverSignature{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mSignature='").append(mSignature).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDriverId).append(mSignature).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DriverSignature)) {
            return false;
        }
        DriverSignature rhs = ((DriverSignature) other);
        return new EqualsBuilder().append(mDriverId, rhs.mDriverId).append(mSignature, rhs.mSignature).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mDriverId);
        dest.writeValue(mSignature);
    }

    public int describeContents() {
        return  0;
    }

}
