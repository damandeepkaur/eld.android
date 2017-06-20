package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Event implements Parcelable {

    @SerializedName("type")
    @Expose
    private Integer mType;
    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("boxid")
    @Expose
    private Integer mBoxId;
    @SerializedName("eventcontent")
    @Expose
    private String mEventContent;
    @SerializedName("description")
    @Expose
    private String mDescription;
    @SerializedName("logtime")
    @Expose
    private Long mLogTime;
    @SerializedName("mdtdatetime")
    @Expose
    private String mMdtDateTime;
    @SerializedName("odometer")
    @Expose
    private Long mOdometer;
    @SerializedName("timezone")
    @Expose
    private Double mTimezone;
    @SerializedName("dst")
    @Expose
    private Boolean mDst;
    @SerializedName("code")
    @Expose
    private Integer mCode;
    @SerializedName("diagnostic")
    @Expose
    private Integer mDiagnostic;
    @SerializedName("enginetime")
    @Expose
    private Integer mEngineTime;

    public final static Parcelable.Creator<Event> CREATOR = new Creator<Event>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Event createFromParcel(Parcel in) {
            Event instance = new Event();
            instance.mType = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEventContent = ((String) in.readValue((String.class.getClassLoader())));
            instance.mDescription = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLogTime = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mMdtDateTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mOdometer = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mTimezone = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mDst = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mCode = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDiagnostic = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEngineTime = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Event[] newArray(int size) {
            return (new Event[size]);
        }

    };

    public Integer getType() {
        return mType;
    }

    public void setType(Integer type) {
        this.mType = type;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        this.mBoxId = boxId;
    }

    public String getEventContent() {
        return mEventContent;
    }

    public void setEventContent(String eventContent) {
        this.mEventContent = eventContent;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public Long getLogTime() {
        return mLogTime;
    }

    public void setLogTime(Long logTime) {
        this.mLogTime = logTime;
    }

    public String getMdtDateTime() {
        return mMdtDateTime;
    }

    public void setMdtDateTime(String mdtDateTime) {
        this.mMdtDateTime = mdtDateTime;
    }

    public Long getOdometer() {
        return mOdometer;
    }

    public void setOdometer(Long odometer) {
        this.mOdometer = odometer;
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

    public Integer getCode() {
        return mCode;
    }

    public void setCode(Integer code) {
        this.mCode = code;
    }

    public Integer getDiagnostic() {
        return mDiagnostic;
    }

    public void setDiagnostic(Integer diagnostic) {
        this.mDiagnostic = diagnostic;
    }

    public Integer getEngineTime() {
        return mEngineTime;
    }

    public void setEngineTime(Integer engineTime) {
        this.mEngineTime = engineTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event{");
        sb.append("mType=").append(mType);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mEventContent='").append(mEventContent).append('\'');
        sb.append(", mDescription='").append(mDescription).append('\'');
        sb.append(", mLogTime=").append(mLogTime);
        sb.append(", mMdtDateTime='").append(mMdtDateTime).append('\'');
        sb.append(", mOdometer=").append(mOdometer);
        sb.append(", mTimezone=").append(mTimezone);
        sb.append(", mDst=").append(mDst);
        sb.append(", mCode=").append(mCode);
        sb.append(", mDiagnostic=").append(mDiagnostic);
        sb.append(", mEngineTime=").append(mEngineTime);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mType)
                .append(mDriverId)
                .append(mBoxId)
                .append(mEventContent)
                .append(mDescription)
                .append(mLogTime)
                .append(mMdtDateTime)
                .append(mOdometer)
                .append(mTimezone)
                .append(mDst)
                .append(mCode)
                .append(mDiagnostic)
                .append(mEngineTime)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Event)) {
            return false;
        }
        Event rhs = ((Event) other);
        return new EqualsBuilder().append(mType, rhs.mType)
                .append(mDriverId, rhs.mDriverId)
                .append(mBoxId, rhs.mBoxId)
                .append(mEventContent, rhs.mEventContent)
                .append(mDescription, rhs.mDescription)
                .append(mLogTime, rhs.mLogTime)
                .append(mMdtDateTime, rhs.mMdtDateTime)
                .append(mOdometer, rhs.mOdometer)
                .append(mTimezone, rhs.mTimezone)
                .append(mDst, rhs.mDst)
                .append(mCode, rhs.mCode)
                .append(mDiagnostic, rhs.mDiagnostic)
                .append(mEngineTime, rhs.mEngineTime)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mType);
        dest.writeValue(mDriverId);
        dest.writeValue(mBoxId);
        dest.writeValue(mEventContent);
        dest.writeValue(mDescription);
        dest.writeValue(mLogTime);
        dest.writeValue(mMdtDateTime);
        dest.writeValue(mOdometer);
        dest.writeValue(mTimezone);
        dest.writeValue(mDst);
        dest.writeValue(mCode);
        dest.writeValue(mDiagnostic);
        dest.writeValue(mEngineTime);
    }

    public int describeContents() {
        return  0;
    }
}
