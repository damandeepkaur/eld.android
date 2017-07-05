package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public Inspection() {}

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

    public void setCategories(String categories) {
        this.mCategories = categories;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public String getDriverName() {
        return mDriverName;
    }

    public void setDriverName(String driverName) {
        this.mDriverName = driverName;
    }

    public String getTrailerOnFly() {
        return mTrailerOnFly;
    }

    public void setTrailerOnFly(String trailerOnFly) {
        this.mTrailerOnFly = trailerOnFly;
    }

    public String getTz() {
        return mTz;
    }

    public void setTz(String tz) {
        this.mTz = tz;
    }

    public boolean isDst() {
        return mDst;
    }

    public void setDst(boolean dst) {
        this.mDst = dst;
    }

    public Long getLogTime() {
        return mLogTime;
    }

    public void setLogTime(Long logTime) {
        this.mLogTime = logTime;
    }

    public Long getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.mUpdateTime = updateTime;
    }

    public Integer getTrailerId() {
        return mTrailerId;
    }

    public void setTrailerId(Integer trailerId) {
        this.mTrailerId = trailerId;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Integer odometer) {
        this.mOdometer = odometer;
    }

    public String getAttachCats() {
        return mAttachCats;
    }

    public void setAttachCats(String attachCats) {
        this.mAttachCats = attachCats;
    }

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

    public Integer getType() {
        return mType;
    }

    public void setType(Integer type) {
        this.mType = type;
    }

    public Boolean getSafe() {
        return mSafe;
    }

    public void setSafe(Boolean safe) {
        this.mSafe = safe;
    }

    public Long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Long startTime) {
        this.mStartTime = startTime;
    }

    public Long getUtcWriteTime() {
        return mUtcWriteTime;
    }

    public void setUtcWriteTime(Long utcWriteTime) {
        this.mUtcWriteTime = utcWriteTime;
    }

    public List<InspectionDefect> getDefectsList() {
        return mDefectList;
    }

    public void setDefectsList(List<InspectionDefect> defectsList) {
        this.mDefectList = defectsList;
    }

    public Boolean getTrailerSafe() {
        return mTrailerSafe;
    }

    public void setTrailerSafe(Boolean trailerSafe) {
        this.mTrailerSafe = trailerSafe;
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
