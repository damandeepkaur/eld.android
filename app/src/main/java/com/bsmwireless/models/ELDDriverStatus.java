package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ELDDriverStatus implements Parcelable {

    @SerializedName("recordStatus")
    @Expose
    private String mRecordStatus;
    @SerializedName("recordOrigin")
    @Expose
    private Integer mRecordOrigin;
    @SerializedName("eventType")
    @Expose
    private Integer mEventType;
    @SerializedName("eventCode")
    @Expose
    private Integer mEventCode;
    @SerializedName("eventTime")
    @Expose
    private Long mEventTime;
    @SerializedName("odometer")
    @Expose
    private Integer mOdometer;
    @SerializedName("engineHours")
    @Expose
    private Integer mEngineHours;
    @SerializedName("lat")
    @Expose
    private Double mLat;
    @SerializedName("lng")
    @Expose
    private Double mLng;
    @SerializedName("distance")
    @Expose
    private Integer mDistance;
    @SerializedName("comment")
    @Expose
    private String mComment;
    @SerializedName("locationDesc")
    @Expose
    private String mLocationDesc;
    @SerializedName("checksum")
    @Expose
    private String mChecksum;
    @SerializedName("shippingId")
    @Expose
    private String mShippingId;
    @SerializedName("coDriverId")
    @Expose
    private Integer mCoDriverId;
    @SerializedName("boxId")
    @Expose
    private Integer mBoxId;
    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("tzOffset")
    @Expose
    private Double mTzOffset;
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("miStatus")
    @Expose
    private Boolean mMiStatus;
    @SerializedName("ddStatus")
    @Expose
    private Boolean mDdStatus;

    public String getRecordStatus() {
        return mRecordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        mRecordStatus = recordStatus;
    }

    public Integer getRecordOrigin() {
        return mRecordOrigin;
    }

    public void setRecordOrigin(Integer recordOrigin) {
        mRecordOrigin = recordOrigin;
    }

    public Integer getEventType() {
        return mEventType;
    }

    public void setEventType(Integer eventType) {
        mEventType = eventType;
    }

    public Integer getEventCode() {
        return mEventCode;
    }

    public void setEventCode(Integer eventCode) {
        mEventCode = eventCode;
    }

    public Long getEventTime() {
        return mEventTime;
    }

    public void setEventTime(Long eventTime) {
        mEventTime = eventTime;
    }

    public Integer getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Integer odometer) {
        mOdometer = odometer;
    }

    public Integer getEngineHours() {
        return mEngineHours;
    }

    public void setEngineHours(Integer engineHours) {
        mEngineHours = engineHours;
    }

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        mLat = lat;
    }

    public Double getLng() {
        return mLng;
    }

    public void setLng(Double lng) {
        mLng = lng;
    }

    public Integer getDistance() {
        return mDistance;
    }

    public void setDistance(Integer distance) {
        mDistance = distance;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getLocationDesc() {
        return mLocationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        mLocationDesc = locationDesc;
    }

    public String getChecksum() {
        return mChecksum;
    }

    public void setChecksum(String checksum) {
        mChecksum = checksum;
    }

    public String getShippingId() {
        return mShippingId;
    }

    public void setShippingId(String shippingId) {
        mShippingId = shippingId;
    }

    public Integer getCoDriverId() {
        return mCoDriverId;
    }

    public void setCoDriverId(Integer coDriverId) {
        mCoDriverId = coDriverId;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        mBoxId = boxId;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Double getTzOffset() {
        return mTzOffset;
    }

    public void setTzOffset(Double tzOffset) {
        mTzOffset = tzOffset;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public Boolean getMiStatus() {
        return mMiStatus;
    }

    public void setMiStatus(Boolean miStatus) {
        mMiStatus = miStatus;
    }

    public Boolean getDdStatus() {
        return mDdStatus;
    }

    public void setDdStatus(Boolean ddStatus) {
        mDdStatus = ddStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ELDDriverStatus{");
        sb.append("mRecordStatus='").append(mRecordStatus).append('\'');
        sb.append(", mRecordOrigin=").append(mRecordOrigin);
        sb.append(", mEventType=").append(mEventType);
        sb.append(", mEventCode=").append(mEventCode);
        sb.append(", mEventTime=").append(mEventTime);
        sb.append(", mOdometer=").append(mOdometer);
        sb.append(", mEngineHours=").append(mEngineHours);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLng=").append(mLng);
        sb.append(", mDistance=").append(mDistance);
        sb.append(", mComment='").append(mComment).append('\'');
        sb.append(", mLocationDesc='").append(mLocationDesc).append('\'');
        sb.append(", mChecksum='").append(mChecksum).append('\'');
        sb.append(", mShippingId='").append(mShippingId).append('\'');
        sb.append(", mCoDriverId=").append(mCoDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mId=").append(mId);
        sb.append(", mTzOffset=").append(mTzOffset);
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mMiStatus=").append(mMiStatus);
        sb.append(", mDdStatus=").append(mDdStatus);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ELDDriverStatus that = (ELDDriverStatus) o;

        return new EqualsBuilder()
                .append(mRecordStatus, that.mRecordStatus)
                .append(mRecordOrigin, that.mRecordOrigin)
                .append(mEventType, that.mEventType)
                .append(mEventCode, that.mEventCode)
                .append(mEventTime, that.mEventTime)
                .append(mOdometer, that.mOdometer)
                .append(mEngineHours, that.mEngineHours)
                .append(mLat, that.mLat)
                .append(mLng, that.mLng)
                .append(mDistance, that.mDistance)
                .append(mComment, that.mComment)
                .append(mLocationDesc, that.mLocationDesc)
                .append(mChecksum, that.mChecksum)
                .append(mShippingId, that.mShippingId)
                .append(mCoDriverId, that.mCoDriverId)
                .append(mBoxId, that.mBoxId)
                .append(mId, that.mId)
                .append(mTzOffset, that.mTzOffset)
                .append(mTimezone, that.mTimezone)
                .append(mMiStatus, that.mMiStatus)
                .append(mDdStatus, that.mDdStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mRecordStatus)
                .append(mRecordOrigin)
                .append(mEventType)
                .append(mEventCode)
                .append(mEventTime)
                .append(mOdometer)
                .append(mEngineHours)
                .append(mLat)
                .append(mLng)
                .append(mDistance)
                .append(mComment)
                .append(mLocationDesc)
                .append(mChecksum)
                .append(mShippingId)
                .append(mCoDriverId)
                .append(mBoxId)
                .append(mId)
                .append(mTzOffset)
                .append(mTimezone)
                .append(mMiStatus)
                .append(mDdStatus)
                .toHashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mRecordStatus);
        dest.writeValue(this.mRecordOrigin);
        dest.writeValue(this.mEventType);
        dest.writeValue(this.mEventCode);
        dest.writeValue(this.mEventTime);
        dest.writeValue(this.mOdometer);
        dest.writeValue(this.mEngineHours);
        dest.writeValue(this.mLat);
        dest.writeValue(this.mLng);
        dest.writeValue(this.mDistance);
        dest.writeString(this.mComment);
        dest.writeString(this.mLocationDesc);
        dest.writeString(this.mChecksum);
        dest.writeString(this.mShippingId);
        dest.writeValue(this.mCoDriverId);
        dest.writeValue(this.mBoxId);
        dest.writeValue(this.mId);
        dest.writeValue(this.mTzOffset);
        dest.writeString(this.mTimezone);
        dest.writeValue(this.mMiStatus);
        dest.writeValue(this.mDdStatus);
    }

    public ELDDriverStatus() {
    }

    protected ELDDriverStatus(Parcel in) {
        this.mRecordStatus = in.readString();
        this.mRecordOrigin = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mEventType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mEventCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mEventTime = (Long) in.readValue(Long.class.getClassLoader());
        this.mOdometer = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mEngineHours = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mLat = (Double) in.readValue(Double.class.getClassLoader());
        this.mLng = (Double) in.readValue(Double.class.getClassLoader());
        this.mDistance = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mComment = in.readString();
        this.mLocationDesc = in.readString();
        this.mChecksum = in.readString();
        this.mShippingId = in.readString();
        this.mCoDriverId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mBoxId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mTzOffset = (Double) in.readValue(Double.class.getClassLoader());
        this.mTimezone = in.readString();
        this.mMiStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mDdStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<ELDDriverStatus> CREATOR = new Creator<ELDDriverStatus>() {
        @Override
        public ELDDriverStatus createFromParcel(Parcel source) {
            return new ELDDriverStatus(source);
        }

        @Override
        public ELDDriverStatus[] newArray(int size) {
            return new ELDDriverStatus[size];
        }
    };
}
