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
    private Integer mEventTime;
    @SerializedName("odometer")
    @Expose
    private Integer mOdometer;
    @SerializedName("engineHours")
    @Expose
    private Integer mEngineHours;
    @SerializedName("lat")
    @Expose
    private Integer mLat;
    @SerializedName("lng")
    @Expose
    private Integer mLng;
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
    private Integer mTzOffset;
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("miStatus")
    @Expose
    private Boolean mMiStatus;
    @SerializedName("ddStatus")
    @Expose
    private Boolean mDdStatus;

    public final static Parcelable.Creator<ELDDriverStatus> CREATOR = new Creator<ELDDriverStatus>() {

        @SuppressWarnings({
                "unchecked"
        })
        public ELDDriverStatus createFromParcel(Parcel in) {
            ELDDriverStatus instance = new ELDDriverStatus();
            instance.mRecordStatus = ((String) in.readValue((String.class.getClassLoader())));
            instance.mRecordOrigin = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEventType = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEventCode = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEventTime = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mOdometer = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEngineHours = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mLat = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mLng = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDistance = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mComment = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLocationDesc = ((String) in.readValue((String.class.getClassLoader())));
            instance.mChecksum = ((String) in.readValue((String.class.getClassLoader())));
            instance.mShippingId = ((String) in.readValue((String.class.getClassLoader())));
            instance.mCoDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mTzOffset = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mTimezone = ((String) in.readValue((String.class.getClassLoader())));
            instance.mMiStatus = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mDdStatus = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public ELDDriverStatus[] newArray(int size) {
            return (new ELDDriverStatus[size]);
        }

    };

    public String getRecordStatus() {
        return mRecordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.mRecordStatus = recordStatus;
    }

    public Integer getRecordOrigin() {
        return mRecordOrigin;
    }

    public void setRecordOrigin(Integer recordOrigin) {
        this.mRecordOrigin = recordOrigin;
    }

    public Integer getEventType() {
        return mEventType;
    }

    public void setEventType(Integer eventType) {
        this.mEventType = eventType;
    }

    public Integer getEventCode() {
        return mEventCode;
    }

    public void setEventCode(Integer eventCode) {
        this.mEventCode = eventCode;
    }

    public Integer getEventTime() {
        return mEventTime;
    }

    public void setEventTime(Integer eventTime) {
        this.mEventTime = eventTime;
    }

    public Integer getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Integer odometer) {
        this.mOdometer = odometer;
    }

    public Integer getEngineHours() {
        return mEngineHours;
    }

    public void setEngineHours(Integer engineHours) {
        this.mEngineHours = engineHours;
    }

    public Integer getLat() {
        return mLat;
    }

    public void setLat(Integer lat) {
        this.mLat = lat;
    }

    public Integer getLng() {
        return mLng;
    }

    public void setLng(Integer lng) {
        this.mLng = lng;
    }

    public Integer getDistance() {
        return mDistance;
    }

    public void setDistance(Integer distance) {
        this.mDistance = distance;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public String getLocationDesc() {
        return mLocationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.mLocationDesc = locationDesc;
    }

    public String getChecksum() {
        return mChecksum;
    }

    public void setChecksum(String checksum) {
        this.mChecksum = checksum;
    }

    public String getShippingId() {
        return mShippingId;
    }

    public void setShippingId(String shippingId) {
        this.mShippingId = shippingId;
    }

    public Integer getCoDriverId() {
        return mCoDriverId;
    }

    public void setCoDriverId(Integer coDriverId) {
        this.mCoDriverId = coDriverId;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        this.mBoxId = boxId;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getTzOffset() {
        return mTzOffset;
    }

    public void setTzOffset(Integer tzOffset) {
        this.mTzOffset = tzOffset;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        this.mTimezone = timezone;
    }

    public Boolean getMiStatus() {
        return mMiStatus;
    }

    public void setMiStatus(Boolean miStatus) {
        this.mMiStatus = miStatus;
    }

    public Boolean getDdStatus() {
        return mDdStatus;
    }

    public void setDdStatus(Boolean ddStatus) {
        this.mDdStatus = ddStatus;
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
    public int hashCode() {
        return new HashCodeBuilder()
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
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ELDDriverStatus)) {
            return false;
        }
        ELDDriverStatus rhs = ((ELDDriverStatus) other);
        return new EqualsBuilder()
                .append(mRecordStatus, rhs.mRecordStatus)
                .append(mRecordOrigin, rhs.mRecordOrigin)
                .append(mEventType, rhs.mEventType)
                .append(mEventCode, rhs.mEventCode)
                .append(mEventTime, rhs.mEventTime)
                .append(mOdometer, rhs.mOdometer)
                .append(mEngineHours, rhs.mEngineHours)
                .append(mLat, rhs.mLat)
                .append(mLng, rhs.mLng)
                .append(mDistance, rhs.mDistance)
                .append(mComment, rhs.mComment)
                .append(mLocationDesc, rhs.mLocationDesc)
                .append(mChecksum, rhs.mChecksum)
                .append(mShippingId, rhs.mShippingId)
                .append(mCoDriverId, rhs.mCoDriverId)
                .append(mBoxId, rhs.mBoxId)
                .append(mId, rhs.mId).append(mTzOffset, rhs.mTzOffset)
                .append(mTimezone, rhs.mTimezone)
                .append(mMiStatus, rhs.mMiStatus)
                .append(mDdStatus, rhs.mDdStatus)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mRecordStatus);
        dest.writeValue(mRecordOrigin);
        dest.writeValue(mEventType);
        dest.writeValue(mEventCode);
        dest.writeValue(mEventTime);
        dest.writeValue(mOdometer);
        dest.writeValue(mEngineHours);
        dest.writeValue(mLat);
        dest.writeValue(mLng);
        dest.writeValue(mDistance);
        dest.writeValue(mComment);
        dest.writeValue(mLocationDesc);
        dest.writeValue(mChecksum);
        dest.writeValue(mShippingId);
        dest.writeValue(mCoDriverId);
        dest.writeValue(mBoxId);
        dest.writeValue(mId);
        dest.writeValue(mTzOffset);
        dest.writeValue(mTimezone);
        dest.writeValue(mMiStatus);
        dest.writeValue(mDdStatus);
    }

    public int describeContents() {
        return 0;
    }
}
