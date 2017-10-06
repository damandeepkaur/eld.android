package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.widgets.alerts.DutyType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.bsmwireless.models.ELDEvent.EventType.DUTY_STATUS_CHANGING;

public final class ELDEvent implements Parcelable, DutyTypeManager.DutyTypeCheckable {
    public static String getEvent(int type, int code) {
        ELDEvent.EventType eventType = ELDEvent.EventType.getEventByType(type);

        switch (eventType) {
            case DUTY_STATUS_CHANGING:
            case CHANGE_IN_DRIVER_INDICATION:
                return DutyType.getDutyTypeByCode(type, code).name();

            case LOGIN_LOGOUT:
                return ELDEvent.LoginLogoutCode.getLoginLogoutByCode(code).name();

            case ENGINE_POWER_CHANGING:
                return ELDEvent.EnginePowerCode.getEnginePowerByCode(code).name();

            case DATA_DIAGNOSTIC:
                return ELDEvent.MalfunctionCode.getMalfunctionByCode(code).name();

            default:
                return eventType.name();
        }
    }

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

        public static EventType getEventByType(int type) {
            for (EventType t : EventType.values()) {
                if (t.mType == type) {
                    return t;
                }
            }
            return EventType.INTERMEDIATE_LOG;
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

        private Integer mOriginCode;

        EventOrigin(int code) { mOriginCode = code; }

        public Integer getValue() { return mOriginCode; }

        public static EventOrigin getOriginByCode(int code) {
            for (EventOrigin t : EventOrigin.values()) {
                if (t.mOriginCode == code) {
                    return t;
                }
            }
            return EventOrigin.UNIDENTIFIED_DRIVER;
        }
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

        public static LoginLogoutCode getLoginLogoutByCode(int code) {
            for (LoginLogoutCode t : LoginLogoutCode.values()) {
                if (t.mCode == code) {
                    return t;
                }
            }
            return LoginLogoutCode.LOGIN;
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

        public static EnginePowerCode getEnginePowerByCode(int code) {
            for (EnginePowerCode t : EnginePowerCode.values()) {
                if (t.mCode == code) {
                    return t;
                }
            }
            return EnginePowerCode.POWER_UP;
        }
    }

    public enum MalfunctionCode {

        MALFUNCTION_LOGGED(1),
        MALFUNCTION_CLEARED(2),
        DIAGNOSTIC_LOGGED(3),
        DIAGNOSTIC_CLEARED(4);
        private int mCode;

        MalfunctionCode(int code) {
            mCode = code;
        }

        public int getCode() {
            return mCode;
        }

        public static MalfunctionCode getMalfunctionByCode(int code) {
            for (MalfunctionCode t : MalfunctionCode.values()) {
                if (t.mCode == code) {
                    return t;
                }
            }
            return MALFUNCTION_LOGGED;
        }
    }

    public enum LatLngFlag {
        FLAG_NONE(""),
        FLAG_X("X"),
        FLAG_M("M"),
        FLAG_E("E");

        private final String mCode;

        LatLngFlag(String code) {
            mCode = code;
        }

        public String getCode() {
            return mCode;
        }

        public static LatLngFlag createByCode(String code) {
            for (LatLngFlag flag : values()) {
                if (flag.mCode.equalsIgnoreCase(code)) return flag;
            }
            return FLAG_NONE;
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
    @SerializedName("logsheet")
    @Expose
    private Long mLogSheet;
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
    private LatLngFlag mLatLngFlag;
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
    @SerializedName("appInfo")
    @Expose
    private String mAppInfo;

    public ELDEvent() {
    }

    private ELDEvent(Parcel in) {
        boolean notNull = in.readByte() == 1;
        if (notNull) {
            mStatus = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mOrigin = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mEventType = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mEventCode = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mEventTime = in.readLong();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mLogSheet = in.readLong();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mOdometer = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mEngineHours = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mLat = in.readDouble();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mLng = in.readDouble();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mLatLngFlag = LatLngFlag.createByCode(in.readString());
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mDistance = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mComment = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mLocation = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mCheckSum = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mShippingId = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mCoDriverId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mBoxId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mVehicleId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mTzOffset = in.readDouble();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mTimezone = in.readString();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mMobileTime = in.readLong();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mDriverId = in.readInt();
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mMalfunction = in.readByte() != 0;
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mDiagnostic = in.readByte() != 0;
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mMalCode = Malfunction.createByCode(in.readString());
        }
        notNull = in.readByte() == 1;
        if (notNull) {
            mAppInfo = in.readString();
        }
    }

    public Integer getStatus() {
        return mStatus;
    }

    public ELDEvent setStatus(Integer status) {
        mStatus = status;
        return this;
    }

    public Integer getOrigin() {
        return mOrigin;
    }

    public void setOrigin(Integer origin) {
        mOrigin = origin;
    }

    public Integer getEventType() {
        return mEventType;
    }

    public void setEventType(Integer eventType) {
        mEventType = eventType;
    }

    public Integer getEventCode() {
        return mEventCode == null ? 0 : mEventCode;
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

    public Long getLogSheet() {
        return mLogSheet;
    }

    public void setLogSheet(Long logSheet) {
        mLogSheet = logSheet;
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

    public LatLngFlag getLatLngFlag() {
        return mLatLngFlag;
    }

    public void setLng(Double lng) {
        mLng = lng;
    }

    public void setLatLngFlag(LatLngFlag latLngFlag) {
        mLatLngFlag = latLngFlag;
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

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getCheckSum() {
        return mCheckSum;
    }

    public void setCheckSum(String checkSum) {
        mCheckSum = checkSum;
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

    public Integer getVehicleId() {
        return mVehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        mVehicleId = vehicleId;
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

    public Long getMobileTime() {
        return mMobileTime;
    }

    public void setMobileTime(Long mobileTime) {
        mMobileTime = mobileTime;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        mDriverId = driverId;
    }

    public Boolean getMalfunction() {
        return mMalfunction;
    }

    public void setMalfunction(Boolean malfunction) {
        mMalfunction = malfunction;
    }

    public Boolean getDiagnostic() {
        return mDiagnostic;
    }

    public void setDiagnostic(Boolean diagnostic) {
        mDiagnostic = diagnostic;
    }

    public Malfunction getMalCode() {
        return mMalCode;
    }

    public void setMalCode(Malfunction malCode) {
        mMalCode = malCode;
    }

    public String getAppInfo() {
        return mAppInfo;
    }

    public void setAppInfo(String appInfo) {
        mAppInfo = appInfo;
    }

    @Override
    public Boolean isActive() {
        return mStatus.equals(ELDEvent.StatusCode.ACTIVE.getValue());
    }

    @Override
    public Boolean isDutyEvent() {
        return mEventType.equals(DUTY_STATUS_CHANGING.getValue()) ||
                mEventType.equals(ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue());
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
                .append(mLogSheet, rhs.mLogSheet)
                .append(mOdometer, rhs.mOdometer)
                .append(mEngineHours, rhs.mEngineHours)
                .append(mLat, rhs.mLat)
                .append(mLng, rhs.mLng)
                .append(mLatLngFlag, rhs.mLatLngFlag)
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
                .append(mAppInfo, rhs.mAppInfo)
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
                .append(mLogSheet)
                .append(mOdometer)
                .append(mEngineHours)
                .append(mLat)
                .append(mLng)
                .append(mLatLngFlag)
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
                .append(mAppInfo)
                .toHashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mStatus == null ? (byte) 0 : 1);
        if (mStatus != null) {
            dest.writeInt(mStatus);
        }

        dest.writeByte(mOrigin == null ? (byte) 0 : 1);
        if (mOrigin != null) {
            dest.writeInt(mOrigin);
        }

        dest.writeByte(mEventType == null ? (byte) 0 : 1);
        if (mEventType != null) {
            dest.writeInt(mEventType);
        }

        dest.writeByte(mEventCode == null ? (byte) 0 : 1);
        if (mEventCode != null) {
            dest.writeInt(mEventCode);
        }

        dest.writeByte(mEventTime == null ? (byte) 0 : 1);
        if (mEventTime != null) {
            dest.writeLong(mEventTime);
        }

        dest.writeByte(mLogSheet == null ? (byte) 0 : 1);
        if (mLogSheet != null) {
            dest.writeLong(mLogSheet);
        }

        dest.writeByte(mOdometer == null ? (byte) 0 : 1);
        if (mOdometer != null) {
            dest.writeInt(mOdometer);
        }

        dest.writeByte(mEngineHours == null ? (byte) 0 : 1);
        if (mEngineHours != null) {
            dest.writeInt(mEngineHours);
        }

        dest.writeByte(mLat == null ? (byte) 0 : 1);
        if (mLat != null) {
            dest.writeDouble(mLat);
        }

        dest.writeByte(mLng == null ? (byte) 0 : 1);
        if (mLng != null) {
            dest.writeDouble(mLng);
        }

        dest.writeByte(mLatLngFlag == null ? (byte) 0 : 1);
        if (mLatLngFlag != null) {
            dest.writeString(mLatLngFlag.mCode);
        }

        dest.writeByte(mDistance == null ? (byte) 0 : 1);
        if (mDistance != null) {
            dest.writeInt(mDistance);
        }

        dest.writeByte(mComment == null ? (byte) 0 : 1);
        if (mComment != null) {
            dest.writeString(mComment);
        }

        dest.writeByte(mLocation == null ? (byte) 0 : 1);
        if (mLocation != null) {
            dest.writeString(mLocation);
        }

        dest.writeByte(mCheckSum == null ? (byte) 0 : 1);
        if (mCheckSum != null) {
            dest.writeString(mCheckSum);
        }

        dest.writeByte(mShippingId == null ? (byte) 0 : 1);
        if (mShippingId != null) {
            dest.writeString(mShippingId);
        }

        dest.writeByte(mCoDriverId == null ? (byte) 0 : 1);
        if (mCoDriverId != null) {
            dest.writeInt(mCoDriverId);
        }

        dest.writeByte(mBoxId == null ? (byte) 0 : 1);
        if (mBoxId != null) {
            dest.writeInt(mBoxId);
        }

        dest.writeByte(mVehicleId == null ? (byte) 0 : 1);
        if (mVehicleId != null) {
            dest.writeInt(mVehicleId);
        }

        dest.writeByte(mId == null ? (byte) 0 : 1);
        if (mId != null) {
            dest.writeInt(mId);
        }

        dest.writeByte(mTzOffset == null ? (byte) 0 : 1);
        if (mTzOffset != null) {
            dest.writeDouble(mTzOffset);
        }

        dest.writeByte(mTimezone == null ? (byte) 0 : 1);
        if (mTimezone != null) {
            dest.writeString(mTimezone);
        }

        dest.writeByte(mMobileTime == null ? (byte) 0 : 1);
        if (mMobileTime != null) {
            dest.writeLong(mMobileTime);
        }

        dest.writeByte(mDriverId == null ? (byte) 0 : 1);
        if (mDriverId != null) {
            dest.writeInt(mDriverId);
        }

        dest.writeByte(mMalfunction == null ? (byte) 0 : 1);
        if (mMalfunction != null) {
            dest.writeByte((byte) (mMalfunction ? 1 : 0));
        }

        dest.writeByte(mDiagnostic == null ? (byte) 0 : 1);
        if (mDiagnostic != null) {
            dest.writeByte((byte) (mDiagnostic ? 1 : 0));
        }
        dest.writeByte((byte) (mMalCode == null ? 0 : 1));
        if (mMalCode != null) {
            dest.writeString(mMalCode.getCode());
        }

        dest.writeByte(mAppInfo == null ? (byte) 0 : 1);
        if (mAppInfo != null) {
            dest.writeString(mAppInfo);
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
    public final ELDEvent clone() {
        Parcel parcel = Parcel.obtain();
        parcel.writeValue(this);
        parcel.setDataPosition(0);
        ELDEvent copy = (ELDEvent) parcel.readValue(ELDEvent.class.getClassLoader());
        parcel.recycle();
        return copy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ELDEvent{");
        sb.append("mId=").append(mId);
        sb.append(", mEventType=").append(mEventType);
        sb.append(", mEventCode=").append(mEventCode);
        sb.append(", mStatus=").append(mStatus);
        sb.append(", mOrigin=").append(mOrigin);
        sb.append(", mEventTime=").append(mEventTime);
        sb.append(", mLogSheet=").append(mLogSheet);
        sb.append(", mOdometer=").append(mOdometer);
        sb.append(", mEngineHours=").append(mEngineHours);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLng=").append(mLng);
        sb.append(", mLatLngFlag").append(mLatLngFlag);
        sb.append(", mDistance=").append(mDistance);
        sb.append(", mComment='").append(mComment).append('\'');
        sb.append(", mLocation='").append(mLocation).append('\'');
        sb.append(", mCheckSum='").append(mCheckSum).append('\'');
        sb.append(", mShippingId='").append(mShippingId).append('\'');
        sb.append(", mCoDriverId=").append(mCoDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mVehicleId=").append(mVehicleId);
        sb.append(", mTzOffset=").append(mTzOffset);
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mMobileTime=").append(mMobileTime);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mMalfunction=").append(mMalfunction);
        sb.append(", mDiagnostic=").append(mDiagnostic);
        sb.append(", mMalCode=").append(mMalCode);
        sb.append(", mAppInfo=").append(mAppInfo);
        sb.append('}');
        return sb.toString();
    }
}
