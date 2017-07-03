package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ELDDriverStatus implements Parcelable {

    @SerializedName("status")
    @Expose
    private Integer mStatus;
    @SerializedName("origin")
    @Expose
    private Integer mOrigin;
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
    @SerializedName("location")
    @Expose
    private String mLocation;
    @SerializedName("checksum")
    @Expose
    private String mCheckSum;
    @SerializedName("shippingId")
    @Expose
    private String mShippingId;
    @SerializedName("coDriverId")
    @Expose
    private Integer mCoDriverId;
    @SerializedName("boxId")
    @Expose
    private Integer mBoxId;
    @SerializedName("vehicleId")
    @Expose
    private Integer mVehicleId;
    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("tzOffset")
    @Expose
    private Double mTzOffset;
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("mobileTime")
    @Expose
    private Long mMobileTime;
    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("malfunction")
    @Expose
    private Boolean mMalfunction;
    @SerializedName("diagnostic")
    @Expose
    private Boolean mDiagnostic;

    public ELDDriverStatus() {}

    private ELDDriverStatus(Parcel in) {
        this.mStatus = in.readInt();
        this.mOrigin = in.readInt();
        this.mEventType = in.readInt();
        this.mEventCode = in.readInt();
        this.mEventTime = in.readLong();
        this.mOdometer = in.readInt();
        this.mEngineHours = in.readInt();
        this.mLat = in.readDouble();
        this.mLng = in.readDouble();
        this.mDistance = in.readInt();
        this.mComment = in.readString();
        this.mLocation = in.readString();
        this.mCheckSum = in.readString();
        this.mShippingId = in.readString();
        this.mCoDriverId = in.readInt();
        this.mBoxId = in.readInt();
        this.mVehicleId = in.readInt();
        this.mId = in.readInt();
        this.mTzOffset = in.readDouble();
        this.mTimezone = in.readString();
        this.mMobileTime = in.readLong();
        this.mDriverId = in.readInt();
        this.mMalfunction = in.readByte() != 0;
        this.mDiagnostic = in.readByte() != 0;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(Integer status) {
        this.mStatus = status;
    }

    public Integer getOrigin() {
        return mOrigin;
    }

    public void setOrigin(Integer origin) {
        this.mOrigin = origin;
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

    public Long getEventTime() {
        return mEventTime;
    }

    public void setEventCode(Long eventTime) {
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

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public String getCheckSum() {
        return mCheckSum;
    }

    public void setCheckSum(String checkSum) {
        this.mCheckSum = checkSum;
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

    public Integer getVehicleId() {
        return mVehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.mVehicleId = vehicleId;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Double getTzOffset() {
        return mTzOffset;
    }

    public void setTzOffset(Double tzOffset) {
        this.mTzOffset = tzOffset;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        this.mTimezone = timezone;
    }

    public Long getMobileTime() {
        return mMobileTime;
    }

    public void setMobileTime(Long mobileTime) {
        this.mMobileTime = mobileTime;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Boolean getMalfunction() {
        return mMalfunction;
    }

    public void setMalfunction(Boolean malfunction) {
        this.mMalfunction = malfunction;
    }

    public Boolean getDiagnostic() {
        return mDiagnostic;
    }

    public void setDiagnostic(Boolean diagnostic) {
        this.mDiagnostic = diagnostic;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ELDDriverStatus{");
        sb.append("mStatus=").append(mStatus).append('\'');
        sb.append(", mOrigin=").append(mOrigin);
        sb.append(", mEventType=").append(mEventType);
        sb.append(", mEventCode=").append(mEventCode);
        sb.append(", mEventTime=").append(mEventTime);
        sb.append(", mOdometer='").append(mOdometer);
        sb.append(", mEngineHours=").append(mEngineHours);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLng='").append(mLng);
        sb.append(", mDistance=").append(mDistance);
        sb.append(", mComment=").append(mComment).append('\'');
        sb.append(", mLocation=").append(mLocation).append('\'');
        sb.append(", mCheckSum=").append(mCheckSum).append('\'');
        sb.append(", mShippingId=").append(mShippingId).append('\'');
        sb.append(", mCoDriverId=").append(mCoDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mVehicleId=").append(mVehicleId);
        sb.append(", mId=").append(mId);
        sb.append(", mTzOffset=").append(mTzOffset);
        sb.append(", mTimezone=").append(mTimezone).append('\'');
        sb.append(", mMobileTime=").append(mMobileTime);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mMalfunction=").append(mMalfunction);
        sb.append(", mDiagnostic=").append(mDiagnostic);
        sb.append('}');
        return sb.toString();
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
        ELDDriverStatus rhs = ((ELDDriverStatus) other);
        // field comparison
        return new EqualsBuilder().append(mStatus, rhs.mStatus)
                .append(mOrigin, rhs.mOrigin)
                .append(mEventType, rhs.mEventType)
                .append(mEventCode, rhs.mEventCode)
                .append(mEventTime, rhs.mEventTime)
                .append(mOdometer, rhs.mOdometer)
                .append(mEngineHours, rhs.mEngineHours)
                .append(mLat, rhs.mLat)
                .append(mLng, rhs.mLng)
                .append(mDistance, rhs.mDistance)
                .append(mComment, rhs.mComment)
                .append(mLocation, rhs.mLocation)
                .append(mCheckSum, rhs.mCheckSum)
                .append(mShippingId, rhs.mShippingId)
                .append(mCoDriverId, rhs.mCoDriverId)
                .append(mBoxId, rhs.mBoxId)
                .append(mVehicleId, rhs.mVehicleId)
                .append(mId, rhs.mId)
                .append(mTzOffset, rhs.mTzOffset)
                .append(mTimezone, rhs.mTimezone)
                .append(mMobileTime, rhs.mMobileTime)
                .append(mDriverId, rhs.mDriverId)
                .append(mMalfunction, rhs.mMalfunction)
                .append(mDiagnostic, rhs.mDiagnostic)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mStatus)
                .append(mOrigin)
                .append(mEventType)
                .append(mEventCode)
                .append(mEventTime)
                .append(mOdometer)
                .append(mEngineHours)
                .append(mLat)
                .append(mLng)
                .append(mDistance)
                .append(mComment)
                .append(mLocation)
                .append(mCheckSum)
                .append(mShippingId)
                .append(mCoDriverId)
                .append(mBoxId)
                .append(mVehicleId)
                .append(mId)
                .append(mTzOffset)
                .append(mTimezone)
                .append(mMobileTime)
                .append(mDriverId)
                .append(mMalfunction)
                .append(mDiagnostic)
                .toHashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatus);
        dest.writeInt(this.mOrigin);
        dest.writeInt(this.mEventType);
        dest.writeInt(this.mEventCode);
        dest.writeLong(this.mEventTime);
        dest.writeInt(this.mOdometer);
        dest.writeInt(this.mEngineHours);
        dest.writeDouble(this.mLat);
        dest.writeDouble(this.mLng);
        dest.writeInt(this.mDistance);
        dest.writeString(this.mComment);
        dest.writeString(this.mLocation);
        dest.writeString(this.mCheckSum);
        dest.writeString(this.mShippingId);
        dest.writeInt(this.mCoDriverId);
        dest.writeInt(this.mBoxId);
        dest.writeInt(this.mVehicleId);
        dest.writeInt(this.mId);
        dest.writeDouble(this.mTzOffset);
        dest.writeString(this.mTimezone);
        dest.writeLong(this.mMobileTime);
        dest.writeInt(this.mDriverId);
        dest.writeByte((byte) (mMalfunction ? 1 : 0));
        dest.writeByte((byte) (mDiagnostic ? 1 : 0));
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
