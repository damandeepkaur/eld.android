package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Location implements Parcelable {

    @SerializedName("lat")
    @Expose
    private Double mLat;
    @SerializedName("lng")
    @Expose
    private Double mLng;
    @SerializedName("address")
    @Expose
    private String mAddress;
    @SerializedName("opt")
    @Expose
    private String mOpt;
    @SerializedName("latlngflag")
    @Expose
    private String mLatLngFlag;

    public final static Parcelable.Creator<Location> CREATOR = new Creator<Location>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Location createFromParcel(Parcel in) {
            Location instance = new Location();
            instance.mLat = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mLng = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mAddress = ((String) in.readValue((String.class.getClassLoader())));
            instance.mOpt = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLatLngFlag = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Location[] newArray(int size) {
            return (new Location[size]);
        }

    };

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        this.mLat = lat;
    }

    public Double getLng() {
        return mLng;
    }

    public void setLng(Double lng) {
        this.mLng = lng;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getOpt() {
        return mOpt;
    }

    public void setOpt(String opt) {
        this.mOpt = opt;
    }

    public String getLatLngFlag() {
        return mLatLngFlag;
    }

    public void setLatLngFlag(String latLngFlag) {
        this.mLatLngFlag = latLngFlag;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Location{");
        sb.append("mLat=").append(mLat);
        sb.append(", mLng=").append(mLng);
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append(", mOpt='").append(mOpt).append('\'');
        sb.append(", mLatLngFlag='").append(mLatLngFlag).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mLat).append(mLng).append(mAddress).append(mOpt).append(mLatLngFlag).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Location)) {
            return false;
        }
        Location rhs = ((Location) other);
        return new EqualsBuilder().append(mLat, rhs.mLat).append(mLng, rhs.mLng).append(mAddress, rhs.mAddress).append(mOpt, rhs.mOpt).append(mLatLngFlag, rhs.mLatLngFlag).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mLat);
        dest.writeValue(mLng);
        dest.writeValue(mAddress);
        dest.writeValue(mOpt);
        dest.writeValue(mLatLngFlag);
    }

    public int describeContents() {
        return  0;
    }
}
