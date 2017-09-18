package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class CUDTripInfo implements Parcelable {

    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("boxid")
    @Expose
    private Integer mBoxId;
    @SerializedName("license")
    @Expose
    private String mLicense;
    @SerializedName("province")
    @Expose
    private String mProvince;
    @SerializedName("codriver")
    @Expose
    private String mCoDriver;
    @SerializedName("permit")
    @Expose
    private String mPermit;
    @SerializedName("blnumber")
    @Expose
    private String mBlNumber;
    @SerializedName("trailerlicense")
    @Expose
    private String mTrailerLicense;
    @SerializedName("logtime")
    @Expose
    private String mLogTime;
    @SerializedName("savetime")
    @Expose
    private Long mSaveTime;
    @SerializedName("isdeleted")
    @Expose
    private Boolean mIsDeleted;

    public final static Parcelable.Creator<CUDTripInfo> CREATOR = new Creator<CUDTripInfo>() {

        @SuppressWarnings({
            "unchecked"
        })
        public CUDTripInfo createFromParcel(Parcel in) {
            CUDTripInfo instance = new CUDTripInfo();
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mProvince = ((String) in.readValue((String.class.getClassLoader())));
            instance.mCoDriver = ((String) in.readValue((String.class.getClassLoader())));
            instance.mPermit = ((String) in.readValue((String.class.getClassLoader())));
            instance.mBlNumber = ((String) in.readValue((String.class.getClassLoader())));
            instance.mTrailerLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLogTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mSaveTime = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mIsDeleted = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public CUDTripInfo[] newArray(int size) {
            return (new CUDTripInfo[size]);
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

    public String getCoDriver() {
        return mCoDriver;
    }

    public void setCoDriver(String coDriver) {
        this.mCoDriver = coDriver;
    }

    public String getPermit() {
        return mPermit;
    }

    public void setPermit(String permit) {
        this.mPermit = permit;
    }

    public String getBlNumber() {
        return mBlNumber;
    }

    public void setBlNumber(String blNumber) {
        this.mBlNumber = blNumber;
    }

    public String getTrailerLicense() {
        return mTrailerLicense;
    }

    public void setTrailerLicense(String trailerLicense) {
        this.mTrailerLicense = trailerLicense;
    }

    public String getLogTime() {
        return mLogTime;
    }

    public void setLogTime(String logTime) {
        this.mLogTime = logTime;
    }

    public Long getSaveTime() {
        return mSaveTime;
    }

    public void setSaveTime(Long saveTime) {
        this.mSaveTime = saveTime;
    }

    public Boolean getIsDeleted() {
        return mIsDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.mIsDeleted = isDeleted;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CUDTripInfo{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mProvince='").append(mProvince).append('\'');
        sb.append(", mCoDriver='").append(mCoDriver).append('\'');
        sb.append(", mPermit='").append(mPermit).append('\'');
        sb.append(", mBlNumber='").append(mBlNumber).append('\'');
        sb.append(", mTrailerLicense='").append(mTrailerLicense).append('\'');
        sb.append(", mLogTime='").append(mLogTime).append('\'');
        sb.append(", mSaveTime=").append(mSaveTime);
        sb.append(", mIsDeleted=").append(mIsDeleted);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDriverId).append(mBoxId).append(mLicense).append(mProvince).append(mCoDriver).append(mPermit).append(mBlNumber).append(mTrailerLicense).append(mLogTime).append(mSaveTime).append(mIsDeleted).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CUDTripInfo)) {
            return false;
        }
        CUDTripInfo rhs = ((CUDTripInfo) other);
        return new EqualsBuilder().append(mDriverId, rhs.mDriverId).append(mBoxId, rhs.mBoxId).append(mLicense, rhs.mLicense).append(mProvince, rhs.mProvince).append(mCoDriver, rhs.mCoDriver).append(mPermit, rhs.mPermit).append(mBlNumber, rhs.mBlNumber).append(mTrailerLicense, rhs.mTrailerLicense).append(mLogTime, rhs.mLogTime).append(mSaveTime, rhs.mSaveTime).append(mIsDeleted, rhs.mIsDeleted).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mDriverId);
        dest.writeValue(mBoxId);
        dest.writeValue(mLicense);
        dest.writeValue(mProvince);
        dest.writeValue(mCoDriver);
        dest.writeValue(mPermit);
        dest.writeValue(mBlNumber);
        dest.writeValue(mTrailerLicense);
        dest.writeValue(mLogTime);
        dest.writeValue(mSaveTime);
        dest.writeValue(mIsDeleted);
    }

    public int describeContents() {
        return  0;
    }
}
