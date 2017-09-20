package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Trailer implements Parcelable {

    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("license")
    @Expose
    private String mLicense;
    @SerializedName("province")
    @Expose
    private String mProvince;
    @SerializedName("weight")
    @Expose
    private Integer mWeight;
    @SerializedName("uofm")
    @Expose
    private Integer mUofm;
    @SerializedName("boxid")
    @Expose
    private Integer mBoxId;

    public final static Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Trailer createFromParcel(Parcel in) {
            Trailer instance = new Trailer();
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mProvince = ((String) in.readValue((String.class.getClassLoader())));
            instance.mWeight = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mUofm = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Trailer[] newArray(int size) {
            return (new Trailer[size]);
        }

    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
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

    public Integer getUofm() {
        return mUofm;
    }

    public void setUofm(Integer uofm) {
        this.mUofm = uofm;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        this.mBoxId = boxId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Trailer{");
        sb.append("mName='").append(mName).append('\'');
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mProvince='").append(mProvince).append('\'');
        sb.append(", mWeight=").append(mWeight);
        sb.append(", mUofm=").append(mUofm);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mName)
                .append(mLicense)
                .append(mProvince)
                .append(mWeight)
                .append(mUofm)
                .append(mBoxId)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Trailer)) {
            return false;
        }
        Trailer rhs = ((Trailer) other);
        return new EqualsBuilder().append(mName, rhs.mName)
                .append(mLicense, rhs.mLicense)
                .append(mProvince, rhs.mProvince)
                .append(mWeight, rhs.mWeight)
                .append(mUofm, rhs.mUofm)
                .append(mBoxId, rhs.mBoxId)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mName);
        dest.writeValue(mLicense);
        dest.writeValue(mProvince);
        dest.writeValue(mWeight);
        dest.writeValue(mUofm);
        dest.writeValue(mBoxId);
    }

    public int describeContents() {
        return  0;
    }
}
