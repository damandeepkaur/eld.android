package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ELDEvent implements Parcelable {
    public enum EventType {
        DUTY_STATUS_CHANGING(1),
        INTERMEDIATE_LOG(2),
        CHANGE_IN_DRIVER_INDICATION(3),
        CERTIFICATION_OF_RECORDS(4),
        LOGIN_LOGOUT(5),
        ENGINE_POWER_CHANGING(6),
        DATA_DIAGNOSTIC(7);

        private int mType;

        EventType(int type) {
            mType = type;
        }

        public int getValue() {
            return mType;
        }
    }

    public enum StatusCode {
        ACTIVE(1),
        INACTIVE_CHANGED(2),
        INACTIVE_CHANGE_REQUESTED(3),
        INACTIVE_CHANGE_REJECTED(4);

        private int mCode;

        StatusCode(int code) {
            mCode = code;
        }

        public int getValue() {
            return mCode;
        }
    }

    public enum EventOrigin {
        AUTOMATIC_RECORD(1),
        AUTOMATIC_EDIT(2),
        MANUAL_ENTER(3),
        MANUAL_ACCEPT(4),
        UNIDENTIFIED_DRIVER(5);

        private int mOriginCode;

        EventOrigin(int code) { mOriginCode = code; }

        public int getValue() { return mOriginCode; }
    }

    public enum LoginLogoutCode {
        LOGIN(1),
        LOGOUT(2);

        private int mCode;

        LoginLogoutCode(int code) {
            mCode = code;
        }

        public int getValue() {
            return mCode;
        }
    }
    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("eventType")
    @Expose
    private Integer mEventType;
    @SerializedName("eventCode")
    @Expose
    private Integer mEventCode;
    @SerializedName("status")
    @Expose
    private Integer mStatus;
    @SerializedName("origin")
    @Expose
    private Integer mOrigin;
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
    @SerializedName("latLnFlag")
    @Expose
    private String mLatLnFlag;
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
    @SerializedName("boxId")
    @Expose
    private Integer mBoxId;
    @SerializedName("vehicleId")
    @Expose
    private Integer mVehicleId;
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
    @SerializedName("sequence")
    @Expose
    private Integer mSequence;
    @SerializedName("malfunction")
    @Expose
    private Boolean mMalfunction;
    @SerializedName("diagnostic")
    @Expose
    private Boolean mDiagnostic;
    @SerializedName("malCode")
    @Expose
    private String mMalCode;

    public ELDEvent() {
    }

    private ELDEvent(Parcel in) {
        this.mStatus = in.readInt();
        this.mOrigin = in.readInt();
        this.mEventType = in.readInt();
        this.mEventCode = in.readInt();
        this.mEventTime = in.readLong();
        this.mOdometer = in.readInt();
        this.mEngineHours = in.readInt();
        this.mLat = in.readDouble();
        this.mLng = in.readDouble();
        this.mLatLnFlag = in.readString();
        this.mDistance = in.readInt();
        this.mComment = in.readString();
        this.mLocation = in.readString();
        this.mCheckSum = in.readString();
        this.mBoxId = in.readInt();
        this.mVehicleId = in.readInt();
        this.mId = in.readInt();
        this.mTzOffset = in.readDouble();
        this.mTimezone = in.readString();
        this.mMobileTime = in.readLong();
        this.mDriverId = in.readInt();
        this.mSequence = in.readInt();
        this.mMalfunction = in.readByte() != 0;
        this.mDiagnostic = in.readByte() != 0;
        this.mMalCode = in.readString();
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

    public void setEventTime(Long eventTime) {
        mEventTime = eventTime;
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

    public String getLatLnFlag() {
        return mLatLnFlag;
    }

    public void setLatLnFlag(String latLnFlag) {
        this.mLatLnFlag = latLnFlag;
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

    public Integer getSequence() {
        return mSequence;
    }

    public void setSequence(Integer sequence) {
        this.mSequence = sequence;
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

    public String getMalCode() {
        return mMalCode;
    }

    public void setMalCode(String malCode) {
        this.mMalCode = malCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ELDEvent{");
        sb.append("mId=").append(mId);
        sb.append(", mStatus=").append(mStatus).append('\'');
        sb.append(", mOrigin=").append(mOrigin);
        sb.append(", mEventType=").append(mEventType);
        sb.append(", mEventCode=").append(mEventCode);
        sb.append(", mEventTime=").append(mEventTime);
        sb.append(", mOdometer='").append(mOdometer);
        sb.append(", mEngineHours=").append(mEngineHours);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLng='").append(mLng);
        sb.append(", mLatLnFlag='").append(mLatLnFlag);
        sb.append(", mDistance=").append(mDistance);
        sb.append(", mComment=").append(mComment).append('\'');
        sb.append(", mLocation=").append(mLocation).append('\'');
        sb.append(", mCheckSum=").append(mCheckSum).append('\'');
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mVehicleId=").append(mVehicleId);
        sb.append(", mTzOffset=").append(mTzOffset);
        sb.append(", mTimezone=").append(mTimezone).append('\'');
        sb.append(", mMobileTime=").append(mMobileTime);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mSequence=").append(mSequence);
        sb.append(", mMalfunction=").append(mMalfunction);
        sb.append(", mDiagnostic=").append(mDiagnostic);
        sb.append(", mMalCode=").append(mMalCode);
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
        ELDEvent rhs = ((ELDEvent) other);
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
                .append(mLatLnFlag, rhs.mLatLnFlag)
                .append(mDistance, rhs.mDistance)
                .append(mComment, rhs.mComment)
                .append(mLocation, rhs.mLocation)
                .append(mCheckSum, rhs.mCheckSum)
                .append(mBoxId, rhs.mBoxId)
                .append(mVehicleId, rhs.mVehicleId)
                .append(mId, rhs.mId)
                .append(mTzOffset, rhs.mTzOffset)
                .append(mTimezone, rhs.mTimezone)
                .append(mMobileTime, rhs.mMobileTime)
                .append(mDriverId, rhs.mDriverId)
                .append(mSequence, rhs.mSequence)
                .append(mMalfunction, rhs.mMalfunction)
                .append(mDiagnostic, rhs.mDiagnostic)
                .append(mMalCode, rhs.mMalCode)
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
                .append(mLatLnFlag)
                .append(mDistance)
                .append(mComment)
                .append(mLocation)
                .append(mCheckSum)
                .append(mBoxId)
                .append(mVehicleId)
                .append(mId)
                .append(mTzOffset)
                .append(mTimezone)
                .append(mMobileTime)
                .append(mDriverId)
                .append(mSequence)
                .append(mMalfunction)
                .append(mDiagnostic)
                .append(mMalCode)
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
        dest.writeString(this.mLatLnFlag);
        dest.writeInt(this.mDistance);
        dest.writeString(this.mComment);
        dest.writeString(this.mLocation);
        dest.writeString(this.mCheckSum);
        dest.writeInt(this.mBoxId);
        dest.writeInt(this.mVehicleId);
        dest.writeInt(this.mId);
        dest.writeDouble(this.mTzOffset);
        dest.writeString(this.mTimezone);
        dest.writeLong(this.mMobileTime);
        dest.writeInt(this.mDriverId);
        dest.writeInt(this.mSequence);
        dest.writeByte((byte) (mMalfunction ? 1 : 0));
        dest.writeByte((byte) (mDiagnostic ? 1 : 0));
        dest.writeString(this.mMalCode);
    }

    public static final Creator<ELDEvent> CREATOR = new Creator<ELDEvent>() {
        @Override
        public ELDEvent createFromParcel(Parcel source) {
            return new ELDEvent(source);
        }

        @Override
        public ELDEvent[] newArray(int size) {
            return new ELDEvent[size];
        }
    };

}
