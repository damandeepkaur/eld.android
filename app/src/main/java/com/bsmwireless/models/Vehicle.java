package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Vehicle implements Parcelable {
    @SerializedName("id")
    private Integer mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("boxId")
    private Integer mBoxId;

    @SerializedName("license")
    private String mLicense;

    @SerializedName("province")
    private String mProvince;

    @SerializedName("weight")
    private Integer mWeight;

    @SerializedName("dot")
    private String mDot;

    public Vehicle() {
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        mBoxId = boxId;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        mLicense = license;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public Integer getWeight() {
        return mWeight;
    }

    public void setWeight(Integer weight) {
        mWeight = weight;
    }

    public String getDot() {
        return mDot;
    }

    public void setDot(String dot) {
        mDot = dot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mId);
        dest.writeString(this.mName);
        dest.writeValue(this.mBoxId);
        dest.writeString(this.mLicense);
        dest.writeString(this.mProvince);
        dest.writeValue(this.mWeight);
        dest.writeString(this.mDot);
    }

    protected Vehicle(Parcel in) {
        this.mId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mName = in.readString();
        this.mBoxId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mLicense = in.readString();
        this.mProvince = in.readString();
        this.mWeight = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mDot = in.readString();
    }

    public static final Parcelable.Creator<Vehicle> CREATOR = new Parcelable.Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel source) {
            return new Vehicle(source);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Vehicle vehicle = (Vehicle) o;

        return new EqualsBuilder()
                .append(mId, vehicle.mId)
                .append(mName, vehicle.mName)
                .append(mBoxId, vehicle.mBoxId)
                .append(mLicense, vehicle.mLicense)
                .append(mProvince, vehicle.mProvince)
                .append(mWeight, vehicle.mWeight)
                .append(mDot, vehicle.mDot)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mId)
                .append(mName)
                .append(mBoxId)
                .append(mLicense)
                .append(mProvince)
                .append(mWeight)
                .append(mDot)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Vehicle{");
        sb.append("mId=").append(mId);
        sb.append(", mName='").append(mName).append('\'');
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mProvince='").append(mProvince).append('\'');
        sb.append(", mWeight=").append(mWeight);
        sb.append(", mDot='").append(mDot).append('\'');
        sb.append('}');
        return sb.toString();
    }
}