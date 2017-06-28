package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LogoutData implements Parcelable {
    @SerializedName("recordStatus")
    private String mRecordStatus;

    @SerializedName("recordOrigin")
    private int mRecordOrigin;

    @SerializedName("eventType")
    private int mEventType;

    @SerializedName("eventCode")
    private int mEventCode;

    @SerializedName("eventTime")
    private long mEventTime;

    @SerializedName("odometer")
    private int mOdometer;

    @SerializedName("engineHours")
    private int mEngineHours;

    @SerializedName("lat")
    private int mLat;

    @SerializedName("lng")
    private int mLon;

    @SerializedName("distance")
    private int mDistance;

    @SerializedName("comment")
    private String mComment;

    @SerializedName("locationDesc")
    private String mLocation;

    @SerializedName("checksum")
    private String mChecksum;

    @SerializedName("shippingId")
    private String mShippingId;

    @SerializedName("coDriverId")
    private int mCoDriverId;

    @SerializedName("boxId")
    private int mBoxId;

    @SerializedName("id")
    private int mId;

    @SerializedName("tzOffset")
    private double mTimeZoneOffset;

    @SerializedName("timezone")
    private String mTimeZone;

    @SerializedName("miStatus")
    private boolean mMiStatus;

    @SerializedName("ddStatus")
    private boolean mDdStatus;

    public LogoutData() {
    }

    public String getRecordStatus() {
        return mRecordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        mRecordStatus = recordStatus;
    }

    public int getRecordOrigin() {
        return mRecordOrigin;
    }

    public void setRecordOrigin(int recordOrigin) {
        mRecordOrigin = recordOrigin;
    }

    public int getEventType() {
        return mEventType;
    }

    public void setEventType(int eventType) {
        mEventType = eventType;
    }

    public int getEventCode() {
        return mEventCode;
    }

    public void setEventCode(int eventCode) {
        mEventCode = eventCode;
    }

    public long getEventTime() {
        return mEventTime;
    }

    public void setEventTime(long eventTime) {
        mEventTime = eventTime;
    }

    public int getOdometer() {
        return mOdometer;
    }

    public void setOdometer(int odometer) {
        mOdometer = odometer;
    }

    public int getEngineHours() {
        return mEngineHours;
    }

    public void setEngineHours(int engineHours) {
        mEngineHours = engineHours;
    }

    public int getLat() {
        return mLat;
    }

    public void setLat(int lat) {
        mLat = lat;
    }

    public int getLon() {
        return mLon;
    }

    public void setLon(int lon) {
        mLon = lon;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
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

    public int getCoDriverId() {
        return mCoDriverId;
    }

    public void setCoDriverId(int coDriverId) {
        mCoDriverId = coDriverId;
    }

    public int getBoxId() {
        return mBoxId;
    }

    public void setBoxId(int boxId) {
        mBoxId = boxId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public double getTimeZoneOffset() {
        return mTimeZoneOffset;
    }

    public void setTimeZoneOffset(double timeZoneOffset) {
        mTimeZoneOffset = timeZoneOffset;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public boolean isMiStatus() {
        return mMiStatus;
    }

    public void setMiStatus(boolean miStatus) {
        mMiStatus = miStatus;
    }

    public boolean isDdStatus() {
        return mDdStatus;
    }

    public void setDdStatus(boolean ddStatus) {
        mDdStatus = ddStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mRecordStatus);
        dest.writeInt(this.mRecordOrigin);
        dest.writeInt(this.mEventType);
        dest.writeInt(this.mEventCode);
        dest.writeLong(this.mEventTime);
        dest.writeInt(this.mOdometer);
        dest.writeInt(this.mEngineHours);
        dest.writeInt(this.mLat);
        dest.writeInt(this.mLon);
        dest.writeInt(this.mDistance);
        dest.writeString(this.mComment);
        dest.writeString(this.mLocation);
        dest.writeString(this.mChecksum);
        dest.writeString(this.mShippingId);
        dest.writeInt(this.mCoDriverId);
        dest.writeInt(this.mBoxId);
        dest.writeInt(this.mId);
        dest.writeDouble(this.mTimeZoneOffset);
        dest.writeString(this.mTimeZone);
        dest.writeByte(this.mMiStatus ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mDdStatus ? (byte) 1 : (byte) 0);
    }

    protected LogoutData(Parcel in) {
        this.mRecordStatus = in.readString();
        this.mRecordOrigin = in.readInt();
        this.mEventType = in.readInt();
        this.mEventCode = in.readInt();
        this.mEventTime = in.readLong();
        this.mOdometer = in.readInt();
        this.mEngineHours = in.readInt();
        this.mLat = in.readInt();
        this.mLon = in.readInt();
        this.mDistance = in.readInt();
        this.mComment = in.readString();
        this.mLocation = in.readString();
        this.mChecksum = in.readString();
        this.mShippingId = in.readString();
        this.mCoDriverId = in.readInt();
        this.mBoxId = in.readInt();
        this.mId = in.readInt();
        this.mTimeZoneOffset = in.readDouble();
        this.mTimeZone = in.readString();
        this.mMiStatus = in.readByte() != 0;
        this.mDdStatus = in.readByte() != 0;
    }

    public static final Parcelable.Creator<LogoutData> CREATOR = new Parcelable.Creator<LogoutData>() {
        @Override
        public LogoutData createFromParcel(Parcel source) {
            return new LogoutData(source);
        }

        @Override
        public LogoutData[] newArray(int size) {
            return new LogoutData[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LogoutData that = (LogoutData) o;

        return new EqualsBuilder()
                .append(mRecordOrigin, that.mRecordOrigin)
                .append(mEventType, that.mEventType)
                .append(mEventCode, that.mEventCode)
                .append(mEventTime, that.mEventTime)
                .append(mOdometer, that.mOdometer)
                .append(mEngineHours, that.mEngineHours)
                .append(mLat, that.mLat)
                .append(mLon, that.mLon)
                .append(mDistance, that.mDistance)
                .append(mCoDriverId, that.mCoDriverId)
                .append(mBoxId, that.mBoxId)
                .append(mId, that.mId)
                .append(mTimeZoneOffset, that.mTimeZoneOffset)
                .append(mMiStatus, that.mMiStatus)
                .append(mDdStatus, that.mDdStatus)
                .append(mRecordStatus, that.mRecordStatus)
                .append(mComment, that.mComment)
                .append(mLocation, that.mLocation)
                .append(mChecksum, that.mChecksum)
                .append(mShippingId, that.mShippingId)
                .append(mTimeZone, that.mTimeZone)
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
                .append(mLon)
                .append(mDistance)
                .append(mComment)
                .append(mLocation)
                .append(mChecksum)
                .append(mShippingId)
                .append(mCoDriverId)
                .append(mBoxId)
                .append(mId)
                .append(mTimeZoneOffset)
                .append(mTimeZone)
                .append(mMiStatus)
                .append(mDdStatus)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LogoutData{");
        sb.append("mRecordStatus='").append(mRecordStatus).append('\'');
        sb.append(", mRecordOrigin=").append(mRecordOrigin);
        sb.append(", mEventType=").append(mEventType);
        sb.append(", mEventCode=").append(mEventCode);
        sb.append(", mEventTime=").append(mEventTime);
        sb.append(", mOdometer=").append(mOdometer);
        sb.append(", mEngineHours=").append(mEngineHours);
        sb.append(", mLat=").append(mLat);
        sb.append(", mLon=").append(mLon);
        sb.append(", mDistance=").append(mDistance);
        sb.append(", mComment='").append(mComment).append('\'');
        sb.append(", mLocation='").append(mLocation).append('\'');
        sb.append(", mChecksum='").append(mChecksum).append('\'');
        sb.append(", mShippingId='").append(mShippingId).append('\'');
        sb.append(", mCoDriverId=").append(mCoDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mId=").append(mId);
        sb.append(", mTimeZoneOffset=").append(mTimeZoneOffset);
        sb.append(", mTimeZone='").append(mTimeZone).append('\'');
        sb.append(", mMiStatus=").append(mMiStatus);
        sb.append(", mDdStatus=").append(mDdStatus);
        sb.append('}');
        return sb.toString();
    }
}
