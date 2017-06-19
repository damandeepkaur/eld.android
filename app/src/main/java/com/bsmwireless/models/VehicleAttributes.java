package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class VehicleAttributes implements Parcelable {

    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("boxid")
    @Expose
    private Integer mBoxId;
    @SerializedName("license")
    @Expose
    private String mLicense;
    @SerializedName("province")
    @Expose
    private String mProvince;
    @SerializedName("weight")
    @Expose
    private Integer mWeight;
    @SerializedName("dot")
    @Expose
    private String mDot;

    public final static Parcelable.Creator<VehicleAttributes> CREATOR = new Creator<VehicleAttributes>() {

        @SuppressWarnings({
                "unchecked"
        })
        public VehicleAttributes createFromParcel(Parcel in) {
            VehicleAttributes instance = new VehicleAttributes();
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mProvince = ((String) in.readValue((String.class.getClassLoader())));
            instance.mWeight = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDot = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public VehicleAttributes[] newArray(int size) {
            return (new VehicleAttributes[size]);
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VehicleAttributes{");
        sb.append("mName='").append(mName).append('\'');
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mProvince='").append(mProvince).append('\'');
        sb.append(", mWeight=").append(mWeight);
        sb.append(", mDot='").append(mDot).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mName).append(mBoxId).append(mLicense).append(mProvince).append(mWeight).append(mDot).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof VehicleAttributes)) {
            return false;
        }
        VehicleAttributes rhs = ((VehicleAttributes) other);
        return new EqualsBuilder().append(mName, rhs.mName).append(mBoxId, rhs.mBoxId).append(mLicense, rhs.mLicense).append(mProvince, rhs.mProvince).append(mWeight, rhs.mWeight).append(mDot, rhs.mDot).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mName);
        dest.writeValue(mBoxId);
        dest.writeValue(mLicense);
        dest.writeValue(mProvince);
        dest.writeValue(mWeight);
        dest.writeValue(mDot);
    }

    public int describeContents() {
        return 0;
    }
}
