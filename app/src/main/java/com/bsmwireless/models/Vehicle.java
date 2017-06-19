package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Vehicle implements Parcelable {
    @SerializedName("id")
    private Integer mId;

    @SerializedName("boxid")
    private Integer mBoxId;

    @SerializedName("license")
    private String mLicense;

    @SerializedName("province")
    private String mProvince;

    @SerializedName("weight")
    private Integer mWeight;

    @SerializedName("dot")
    private String mDot;

    public final static Parcelable.Creator<Vehicle> CREATOR = new Creator<Vehicle>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Vehicle createFromParcel(Parcel in) {
            Vehicle instance = new Vehicle();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mProvince = ((String) in.readValue((String.class.getClassLoader())));
            instance.mWeight = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDot = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Vehicle[] newArray(int size) {
            return (new Vehicle[size]);
        }

    };

    public Vehicle() {
    }

    public Vehicle(Integer id, Integer boxId, String license, String province, Integer weight, String dot) {
        mId = id;
        mBoxId = boxId;
        mLicense = license;
        mProvince = province;
        mWeight = weight;
        mDot = dot;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        this.mBoxId = boxId;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        this.mLicense = license;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        this.mProvince = province;
    }

    public Integer getWeight() {
        return mWeight;
    }

    public void setWeight(Integer weight) {
        this.mWeight = weight;
    }

    public String getDot() {
        return mDot;
    }

    public void setDot(String dot) {
        this.mDot = dot;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mBoxId);
        dest.writeValue(mLicense);
        dest.writeValue(mProvince);
        dest.writeValue(mWeight);
        dest.writeValue(mDot);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vehicle vehicle = (Vehicle) o;

        if (mId != null ? !mId.equals(vehicle.mId) : vehicle.mId != null) return false;
        if (mBoxId != null ? !mBoxId.equals(vehicle.mBoxId) : vehicle.mBoxId != null) return false;
        if (mLicense != null ? !mLicense.equals(vehicle.mLicense) : vehicle.mLicense != null)
            return false;
        if (mProvince != null ? !mProvince.equals(vehicle.mProvince) : vehicle.mProvince != null)
            return false;
        if (mWeight != null ? !mWeight.equals(vehicle.mWeight) : vehicle.mWeight != null)
            return false;
        return mDot != null ? mDot.equals(vehicle.mDot) : vehicle.mDot == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mBoxId != null ? mBoxId.hashCode() : 0);
        result = 31 * result + (mLicense != null ? mLicense.hashCode() : 0);
        result = 31 * result + (mProvince != null ? mProvince.hashCode() : 0);
        result = 31 * result + (mWeight != null ? mWeight.hashCode() : 0);
        result = 31 * result + (mDot != null ? mDot.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Vehicle{");
        sb.append("mId=").append(mId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mProvince='").append(mProvince).append('\'');
        sb.append(", mWeight=").append(mWeight);
        sb.append(", mDot='").append(mDot).append('\'');
        sb.append('}');
        return sb.toString();
    }
}