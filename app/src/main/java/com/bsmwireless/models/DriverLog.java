package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DriverLog implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("codriver")
    @Expose
    private String mCoDriver;
    @SerializedName("boxid")
    @Expose
    private Integer mBoxId;
    @SerializedName("event")
    @Expose
    private Integer mEvent;
    @SerializedName("type")
    @Expose
    private Integer mType;
    @SerializedName("radiusrule")
    @Expose
    private Boolean mRadiusRule;
    @SerializedName("edited")
    @Expose
    private Boolean mEdited;
    @SerializedName("signed")
    @Expose
    private Boolean mSigned;
    @SerializedName("timezone")
    @Expose
    private Double mTimezone;
    @SerializedName("dst")
    @Expose
    private Boolean mDst;
    @SerializedName("logtime")
    @Expose
    private String mLogTime;
    @SerializedName("originaltime")
    @Expose
    private String mOriginalTime;
    @SerializedName("writetime")
    @Expose
    private String mWriteTime;
    @SerializedName("actionid")
    @Expose
    private Integer mActionId;
    @SerializedName("odometer")
    @Expose
    private Long mOdometer;
    @SerializedName("location")
    @Expose
    private Location mLocation;
    @SerializedName("comments")
    @Expose
    private String mComments;
    @SerializedName("appversion")
    @Expose
    private String mAppVersion;

    public final static Parcelable.Creator<DriverLog> CREATOR = new Creator<DriverLog>() {

        @SuppressWarnings({
            "unchecked"
        })
        public DriverLog createFromParcel(Parcel in) {
            DriverLog instance = new DriverLog();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mCoDriver = ((String) in.readValue((String.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEvent = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mType = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mRadiusRule = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mEdited = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mSigned = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mTimezone = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mDst = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mLogTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mOriginalTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mWriteTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mActionId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mOdometer = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mLocation = ((Location) in.readValue((Location.class.getClassLoader())));
            instance.mComments = ((String) in.readValue((String.class.getClassLoader())));
            instance.mAppVersion = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public DriverLog[] newArray(int size) {
            return (new DriverLog[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public String getCoDriver() {
        return mCoDriver;
    }

    public void setCoDriver(String coDriver) {
        this.mCoDriver = coDriver;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        this.mBoxId = boxId;
    }

    public Integer getEvent() {
        return mEvent;
    }

    public void setEvent(Integer event) {
        this.mEvent = event;
    }

    public Integer getType() {
        return mType;
    }

    public void setType(Integer type) {
        this.mType = type;
    }

    public Boolean getRadiusRule() {
        return mRadiusRule;
    }

    public void setRadiusRule(Boolean radiusRule) {
        this.mRadiusRule = radiusRule;
    }

    public Boolean getEdited() {
        return mEdited;
    }

    public void setEdited(Boolean edited) {
        this.mEdited = edited;
    }

    public Boolean getSigned() {
        return mSigned;
    }

    public void setSigned(Boolean signed) {
        this.mSigned = signed;
    }

    public Double getTimezone() {
        return mTimezone;
    }

    public void setTimezone(Double timezone) {
        this.mTimezone = timezone;
    }

    public Boolean getDst() {
        return mDst;
    }

    public void setDst(Boolean dst) {
        this.mDst = dst;
    }

    public String getLogTime() {
        return mLogTime;
    }

    public void setLogTime(String logTime) {
        this.mLogTime = logTime;
    }

    public String getOriginalTime() {
        return mOriginalTime;
    }

    public void setOriginalTime(String originalTime) {
        this.mOriginalTime = originalTime;
    }

    public String getWriteTime() {
        return mWriteTime;
    }

    public void setWriteTime(String writeTime) {
        this.mWriteTime = writeTime;
    }

    public Integer getActionId() {
        return mActionId;
    }

    public void setActionId(Integer actionId) {
        this.mActionId = actionId;
    }

    public Long getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Long odometer) {
        this.mOdometer = odometer;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        this.mLocation = location;
    }

    public String getComments() {
        return mComments;
    }

    public void setComments(String comments) {
        this.mComments = comments;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public void setAppVersion(String appVersion) {
        this.mAppVersion = appVersion;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DriverLog{");
        sb.append("mId=").append(mId);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mCoDriver='").append(mCoDriver).append('\'');
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mEvent=").append(mEvent);
        sb.append(", mType=").append(mType);
        sb.append(", mRadiusRule=").append(mRadiusRule);
        sb.append(", mEdited=").append(mEdited);
        sb.append(", mSigned=").append(mSigned);
        sb.append(", mTimezone=").append(mTimezone);
        sb.append(", mDst=").append(mDst);
        sb.append(", mLogTime='").append(mLogTime).append('\'');
        sb.append(", mOriginalTime='").append(mOriginalTime).append('\'');
        sb.append(", mWriteTime='").append(mWriteTime).append('\'');
        sb.append(", mActionId=").append(mActionId);
        sb.append(", mOdometer=").append(mOdometer);
        sb.append(", mLocation=").append(mLocation);
        sb.append(", mComments='").append(mComments).append('\'');
        sb.append(", mAppVersion='").append(mAppVersion).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).append(mDriverId).append(mCoDriver).append(mBoxId).append(mEvent).append(mType).append(mRadiusRule).append(mEdited).append(mSigned).append(mTimezone).append(mDst).append(mLogTime).append(mOriginalTime).append(mWriteTime).append(mActionId).append(mOdometer).append(mLocation).append(mComments).append(mAppVersion).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DriverLog)) {
            return false;
        }
        DriverLog rhs = ((DriverLog) other);
        return new EqualsBuilder().append(mId, rhs.mId).append(mDriverId, rhs.mDriverId).append(mCoDriver, rhs.mCoDriver).append(mBoxId, rhs.mBoxId).append(mEvent, rhs.mEvent).append(mType, rhs.mType).append(mRadiusRule, rhs.mRadiusRule).append(mEdited, rhs.mEdited).append(mSigned, rhs.mSigned).append(mTimezone, rhs.mTimezone).append(mDst, rhs.mDst).append(mLogTime, rhs.mLogTime).append(mOriginalTime, rhs.mOriginalTime).append(mWriteTime, rhs.mWriteTime).append(mActionId, rhs.mActionId).append(mOdometer, rhs.mOdometer).append(mLocation, rhs.mLocation).append(mComments, rhs.mComments).append(mAppVersion, rhs.mAppVersion).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mDriverId);
        dest.writeValue(mCoDriver);
        dest.writeValue(mBoxId);
        dest.writeValue(mEvent);
        dest.writeValue(mType);
        dest.writeValue(mRadiusRule);
        dest.writeValue(mEdited);
        dest.writeValue(mSigned);
        dest.writeValue(mTimezone);
        dest.writeValue(mDst);
        dest.writeValue(mLogTime);
        dest.writeValue(mOriginalTime);
        dest.writeValue(mWriteTime);
        dest.writeValue(mActionId);
        dest.writeValue(mOdometer);
        dest.writeValue(mLocation);
        dest.writeValue(mComments);
        dest.writeValue(mAppVersion);
    }

    public int describeContents() {
        return  0;
    }
}
