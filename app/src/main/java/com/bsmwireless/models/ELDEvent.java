package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ELDEvent implements Parcelable, DutyTypeManager.DutyTypeCheckable {
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

    /**
     * Event Origin as defined by ELD 7.22, Table 7.
     */
    public enum EventOrigin {
        AUTOMATIC_RECORD(1),        // ELD 7.22 Table 7: "Automatically recorded by ELD"
        DRIVER(2),                  // ELD 7.22 Table 7: "Edited or entered by the Driver"
        NON_DRIVER(3),              // ELD 7.22 Table 7: "Edit requested by an Authenticated User other than the Driver"
        UNIDENTIFIED_DRIVER(4);     // ELD 7.22 Table 7: "Assumed from Unidentified Driver profile"

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

    public enum EnginePowerCode {
        POWER_UP(1),
        POWER_UP_REDUCE_DECISION(2),
        SHUT_DOWN(3),
        SHUT_DOWN_REDUCE_DECISION(4);

        private int mCode;

        EnginePowerCode(int type) {
            mCode = type;
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
    @SerializedName("malCode")
    @Expose
    private Malfunction mMalCode;

    public ELDEvent() {
    }

    private ELDEvent(Parcel in) {
        boolean notNull = in.readByte() == 1;
        if (notNull) {
            this.mStatus = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mOrigin = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mEventType = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mEventCode = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mEventTime = in.readLong();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mOdometer = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mEngineHours = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mLat = in.readDouble();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mLng = in.readDouble();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mDistance = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mComment = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mLocation = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mCheckSum = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mShippingId = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mCoDriverId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mBoxId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mVehicleId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mTzOffset = in.readDouble();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mTimezone = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mMobileTime = in.readLong();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mDriverId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mMalfunction = in.readByte() != 0;
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            this.mDiagnostic = in.readByte() != 0;
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mMalCode = Malfunction.createByCode(in.readString());
        }
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
        return mEventCode == null ? 0 : mEventCode;
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

    public Malfunction getMalCode() {
        return mMalCode;
    }

    public void setMalCode(Malfunction malCode) {
        this.mMalCode = malCode;
    }

    @Override
    public Boolean isActive() {
        return mStatus.equals(ELDEvent.StatusCode.ACTIVE.getValue());
    }

    @Override
    public Boolean isDutyEvent() {
        return mEventType.equals(ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()) ||
                mEventType.equals(ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue());
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
        sb.append(", mDistance=").append(mDistance);
        sb.append(", mComment=").append(mComment).append('\'');
        sb.append(", mLocation=").append(mLocation).append('\'');
        sb.append(", mCheckSum=").append(mCheckSum).append('\'');
        sb.append(", mShippingId=").append(mShippingId).append('\'');
        sb.append(", mCoDriverId=").append(mCoDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mVehicleId=").append(mVehicleId);
        sb.append(", mTzOffset=").append(mTzOffset);
        sb.append(", mTimezone=").append(mTimezone).append('\'');
        sb.append(", mMobileTime=").append(mMobileTime);
        sb.append(", mDriverId=").append(mDriverId);
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
                .append(mMalCode)
                .toHashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mStatus == null ? (byte) 0 : 1);
        if (this.mStatus != null) {
            dest.writeInt(this.mStatus);
        }

        dest.writeByte(this.mOrigin == null ? (byte) 0 : 1);
        if (this.mOrigin != null) {
            dest.writeInt(this.mOrigin);
        }

        dest.writeByte(this.mEventType == null ? (byte) 0 : 1);
        if (this.mEventType != null) {
            dest.writeInt(this.mEventType);
        }

        dest.writeByte(this.mEventCode == null ? (byte) 0 : 1);
        if (this.mEventCode != null) {
            dest.writeInt(this.mEventCode);
        }

        dest.writeByte(this.mEventTime == null ? (byte) 0 : 1);
        if (this.mEventTime != null) {
            dest.writeLong(this.mEventTime);
        }

        dest.writeByte(this.mOdometer == null ? (byte) 0 : 1);
        if (this.mOdometer != null) {
            dest.writeInt(this.mOdometer);
        }

        dest.writeByte(this.mEngineHours == null ? (byte) 0 : 1);
        if (this.mEngineHours != null) {
            dest.writeInt(this.mEngineHours);
        }

        dest.writeByte(this.mLat == null ? (byte) 0 : 1);
        if (this.mLat != null) {
            dest.writeDouble(this.mLat);
        }

        dest.writeByte(this.mLng == null ? (byte) 0 : 1);
        if (this.mLng != null) {
            dest.writeDouble(this.mLng);
        }

        dest.writeByte(this.mDistance == null ? (byte) 0 : 1);
        if (this.mDistance != null) {
            dest.writeInt(this.mDistance);
        }

        dest.writeByte(this.mComment == null ? (byte) 0 : 1);
        if (this.mComment != null) {
            dest.writeString(this.mComment);
        }

        dest.writeByte(this.mLocation == null ? (byte) 0 : 1);
        if (this.mLocation != null) {
            dest.writeString(this.mLocation);
        }

        dest.writeByte(this.mCheckSum == null ? (byte) 0 : 1);
        if (this.mCheckSum != null) {
            dest.writeString(this.mCheckSum);
        }

        dest.writeByte(this.mShippingId == null ? (byte) 0 : 1);
        if (this.mShippingId != null) {
            dest.writeString(this.mShippingId);
        }

        dest.writeByte(this.mCoDriverId == null ? (byte) 0 : 1);
        if (this.mCoDriverId != null) {
            dest.writeInt(this.mCoDriverId);
        }

        dest.writeByte(this.mBoxId == null ? (byte) 0 : 1);
        if (this.mBoxId != null) {
            dest.writeInt(this.mBoxId);
        }

        dest.writeByte(this.mVehicleId == null ? (byte) 0 : 1);
        if (this.mVehicleId != null) {
            dest.writeInt(this.mVehicleId);
        }

        dest.writeByte(this.mId == null ? (byte) 0 : 1);
        if (this.mId != null) {
            dest.writeInt(this.mId);
        }

        dest.writeByte(this.mTzOffset == null ? (byte) 0 : 1);
        if (this.mTzOffset != null) {
            dest.writeDouble(this.mTzOffset);
        }

        dest.writeByte(this.mTimezone == null ? (byte) 0 : 1);
        if (this.mTimezone != null) {
            dest.writeString(this.mTimezone);
        }

        dest.writeByte(this.mMobileTime == null ? (byte) 0 : 1);
        if (this.mMobileTime != null) {
            dest.writeLong(this.mMobileTime);
        }

        dest.writeByte(this.mDriverId == null ? (byte) 0 : 1);
        if (this.mDriverId != null) {
            dest.writeInt(this.mDriverId);
        }

        dest.writeByte(this.mMalfunction == null ? (byte) 0 : 1);
        if (this.mMalfunction != null) {
            dest.writeByte((byte) (mMalfunction ? 1 : 0));
        }

        dest.writeByte(this.mDiagnostic == null ? (byte) 0 : 1);
        if (this.mDiagnostic != null) {
            dest.writeByte((byte) (mDiagnostic ? 1 : 0));
        }
        dest.writeByte((byte) (mMalCode == null ? 0 : 1));
        if (mMalCode != null) {
            dest.writeString(mMalCode.getCode());
        }
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

    @Override
    public ELDEvent clone() {
        Parcel parcel = Parcel.obtain();
        parcel.writeValue(this);
        parcel.setDataPosition(0);
        ELDEvent copy = (ELDEvent) parcel.readValue(ELDEvent.class.getClassLoader());
        parcel.recycle();
        return copy;
    }
}
