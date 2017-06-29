package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class Inspection implements Parcelable {

    @SerializedName("categories")
    @Expose
    private String mCategories;
    @SerializedName("address")
    @Expose
    private String mAddress;
    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("driverName")
    @Expose
    private String mDriverName;
    @SerializedName("trailerOnFly")
    @Expose
    private String mTrailerOnFly;
    @SerializedName("tz")
    @Expose
    private String mTz;
    @SerializedName("dst")
    @Expose
    private boolean mDst;
    @SerializedName("logtime")
    @Expose
    private Long mLogTime;
    @SerializedName("updatetime")
    @Expose
    private Long mUpdateTime;
    @SerializedName("trailerid")
    @Expose
    private Integer mTrailerId;
    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("odometer")
    @Expose
    private Integer mOdometer;
    @SerializedName("attachCats")
    @Expose
    private String mAttachCats;
    @SerializedName("lat")
    @Expose
    private Double mLat;
    @SerializedName("lng")
    @Expose
    private Double mLng;
    @SerializedName("type")
    @Expose
    private Integer mType;
    @SerializedName("safe")
    @Expose
    private Boolean mSafe;
    @SerializedName("startTime")
    @Expose
    private Long mStartTime;
    @SerializedName("utcWriteTime")
    @Expose
    private Long mUtcWriteTime;
    @SerializedName("defects")
    @Expose
    private List<InspectionDefect> mDefectList = null;
    @SerializedName("trailerSafe")
    @Expose
    private Boolean mTrailerSafe;

    private Inspection(Parcel in) {
        mCategories = in.readString();
        mAddress = in.readString();
        mDriverId = in.readInt();
        mDriverName = in.readString();
        mTrailerOnFly = in.readString();
        mTz = in.readString();
        mDst = in.readByte() != 0;
        mLogTime = in.readLong();
        mUpdateTime = in.readLong();
        mTrailerId = in.readInt();
        mId = in.readInt();
        mOdometer = in.readInt();
        mAttachCats = in.readString();
        mLat = in.readDouble();
        mLng = in.readDouble();
        mType = in.readInt();
        mSafe = in.readByte() != 0;
        mStartTime = in.readLong();
        mUtcWriteTime = in.readLong();
        in.readTypedList(mDefectList, InspectionDefect.CREATOR);
        mTrailerSafe = in.readByte() != 0;
    }

    public final static Parcelable.Creator<Inspection> CREATOR = new Creator<Inspection>() {

        @SuppressWarnings({"unchecked"})
        @Override
        public Inspection createFromParcel(Parcel in) {
            return new Inspection(in);
        }

        @Override
        public Inspection[] newArray(int size) {
            return (new Inspection[size]);
        }
    };

    public String getCategories() {
        return mCategories;
    }

    public void setCategories(String mCategories) {
        this.mCategories = mCategories;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer mDriverId) {
        this.mDriverId = mDriverId;
    }

    public String getDriverName() {
        return mDriverName;
    }

    public void setDriverName(String mDriverName) {
        this.mDriverName = mDriverName;
    }

    public String getTrailerOnFly() {
        return mTrailerOnFly;
    }

    public void setTrailerOnFly(String mTrailerOnFly) {
        this.mTrailerOnFly = mTrailerOnFly;
    }

    public String getTz() {
        return mTz;
    }

    public void setTz(String mTz) {
        this.mTz = mTz;
    }

    public boolean isDst() {
        return mDst;
    }

    public void setDst(boolean mDst) {
        this.mDst = mDst;
    }

    public Long getLogTime() {
        return mLogTime;
    }

    public void setLogTime(Long mLogTime) {
        this.mLogTime = mLogTime;
    }

    public Long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(Long mUpdateTime) {
        this.mUpdateTime = mUpdateTime;
    }

    public Integer getTrailerId() {
        return mTrailerId;
    }

    public void setTrailerId(Integer mTrailerId) {
        this.mTrailerId = mTrailerId;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer mId) {
        this.mId = mId;
    }

    public Integer getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Integer mOdometer) {
        this.mOdometer = mOdometer;
    }

    public String getAttachCats() {
        return mAttachCats;
    }

    public void setAttachCats(String mAttachCats) {
        this.mAttachCats = mAttachCats;
    }

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double mLat) {
        this.mLat = mLat;
    }

    public Double getLng() {
        return mLng;
    }

    public void setLng(Double mLng) {
        this.mLng = mLng;
    }

    public Integer getType() {
        return mType;
    }

    public void setType(Integer mType) {
        this.mType = mType;
    }

    public Boolean getSafe() {
        return mSafe;
    }

    public void setSafe(Boolean mSafe) {
        this.mSafe = mSafe;
    }

    public Long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public Long getUtcWriteTime() {
        return mUtcWriteTime;
    }

    public void setUtcWriteTime(Long mUtcWriteTime) {
        this.mUtcWriteTime = mUtcWriteTime;
    }

    public List<InspectionDefect> getDefectsList() {
        return mDefectList;
    }

    public void setDefectsList(List<InspectionDefect> mDefectsList) {
        this.mDefectList = mDefectsList;
    }

    public Boolean getTrailerSafe() {
        return mTrailerSafe;
    }

    public void setTrailerSafe(Boolean mTrailerSafe) {
        this.mTrailerSafe = mTrailerSafe;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Inspection{");
        sb.append("mCategories=").append(mCategories);
        sb.append(", mAddress=").append(mAddress);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mDriverName='").append(mDriverName);
        sb.append(", mTrailerOnFly='").append(mTrailerOnFly);
        sb.append(", mTz='").append(mTz);
        sb.append(", mDst='").append(mDst);
        sb.append(", mLogTime='").append(mLogTime);
        sb.append(", mUpdateTime='").append(mUpdateTime);
        sb.append(", mTrailerId='").append(mTrailerId);
        sb.append(", mId='").append(mId);
        sb.append(", mOdometer='").append(mOdometer);
        sb.append(", mAttachCats='").append(mAttachCats);
        sb.append(", mLat='").append(mLat);
        sb.append(", mLng='").append(mLng);
        sb.append(", mType='").append(mType);
        sb.append(", mSafe='").append(mSafe);
        sb.append(", mStartTime='").append(mStartTime);
        sb.append(", mUtcWriteTime='").append(mUtcWriteTime);
        sb.append(", mDefectsList='").append(mDefectList);
        sb.append(", mTrailerSafe='").append(mTrailerSafe);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mCategories)
                .append(mAddress)
                .append(mDriverId)
                .append(mDriverName)
                .append(mTrailerOnFly)
                .append(mTz)
                .append(mDst)
                .append(mLogTime)
                .append(mUpdateTime)
                .append(mTrailerId)
                .append(mId)
                .append(mOdometer)
                .append(mAttachCats)
                .append(mLat)
                .append(mLng)
                .append(mType)
                .append(mSafe)
                .append(mStartTime)
                .append(mUtcWriteTime)
                .append(mDefectList)
                .append(mTrailerSafe)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        // self check
        if (this == other) {
            return true;
        }
        // null check and type check (cast)
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Inspection rhs = ((Inspection) other);
        // field comparison
        return new EqualsBuilder().append(mCategories, rhs.mCategories)
                .append(mAddress, rhs.mAddress)
                .append(mDriverId, rhs.mDriverId)
                .append(mDriverName, rhs.mDriverName)
                .append(mTrailerOnFly, rhs.mTrailerOnFly)
                .append(mTz, rhs.mTz)
                .append(mDst, rhs.mDst)
                .append(mLogTime, rhs.mLogTime)
                .append(mUpdateTime, rhs.mUpdateTime)
                .append(mTrailerId, rhs.mTrailerId)
                .append(mId, rhs.mId)
                .append(mOdometer, rhs.mOdometer)
                .append(mAttachCats, rhs.mAttachCats)
                .append(mLat, rhs.mLat)
                .append(mLng, rhs.mLng)
                .append(mType, rhs.mType)
                .append(mSafe, rhs.mSafe)
                .append(mStartTime, rhs.mStartTime)
                .append(mUtcWriteTime, rhs.mUtcWriteTime)
                .append(mDefectList, rhs.mDefectList)
                .append(mTrailerSafe, rhs.mTrailerSafe)
                .isEquals();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCategories);
        dest.writeString(mAddress);
        dest.writeInt(mDriverId);
        dest.writeString(mDriverName);
        dest.writeString(mTrailerOnFly);
        dest.writeString(mTz);
        dest.writeByte((byte) (mDst ? 1 : 0));
        dest.writeLong(mLogTime);
        dest.writeLong(mUpdateTime);
        dest.writeInt(mTrailerId);
        dest.writeInt(mId);
        dest.writeInt(mOdometer);
        dest.writeString(mAttachCats);
        dest.writeDouble(mLat);
        dest.writeDouble(mLng);
        dest.writeInt(mType);
        dest.writeByte((byte) (mSafe ? 1 : 0));
        dest.writeLong(mStartTime);
        dest.writeLong(mUtcWriteTime);
        dest.writeTypedList(mDefectList);
        dest.writeByte((byte) (mTrailerSafe ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
