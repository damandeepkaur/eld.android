package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LogSheetHeader implements Parcelable {

    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("logday")
    @Expose
    private Long mLogDay;
    @SerializedName("vehicleId")
    @Expose
    private Integer mVehicleId;
    @SerializedName("boxId")
    @Expose
    private Integer mBoxId;
    @SerializedName("startOfDay")
    @Expose
    private Long mStartOfDay;
    @SerializedName("shippingId")
    @Expose
    private String mShippingId;
    @SerializedName("trailerIds")
    @Expose
    private String mTrailerIds;
    @SerializedName("codriverIds")
    @Expose
    private String mCoDriverIds;
    @SerializedName("comment")
    @Expose
    private String mComment;
    @SerializedName("dutyCycle")
    @Expose
    private String mDutyCycle;
    @SerializedName("home")
    @Expose
    private HomeTerminal mHomeTerminal;
    @SerializedName("additions")
    @Expose
    private String mAdditions;
    @SerializedName("signed")
    @Expose
    private Boolean mSigned;

    public LogSheetHeader() {}

    private LogSheetHeader(Parcel in) {
        this.mDriverId = in.readInt();
        this.mLogDay = in.readLong();
        this.mVehicleId = in.readInt();
        this.mBoxId = in.readInt();
        this.mStartOfDay = in.readLong();
        this.mShippingId = in.readString();
        this.mTrailerIds = in.readString();
        this.mCoDriverIds = in.readString();
        this.mComment = in.readString();
        this.mDutyCycle = in.readString();
        this.mHomeTerminal = in.readParcelable(HomeTerminal.class.getClassLoader());
        this.mAdditions = in.readString();
        this.mSigned = in.readByte() != 0;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Long getLogDay() {
        return mLogDay;
    }

    public void setLogDay(Long logDay) {
        this.mLogDay = logDay;
    }

    public Integer getVehicleId() {
        return mVehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.mVehicleId = vehicleId;
    }

    public Integer getBoxId() {
        return mBoxId;
    }

    public void setBoxId(Integer boxId) {
        this.mBoxId = boxId;
    }

    public Long getStartOfDay() {
        return mStartOfDay;
    }

    public void setStartOfDay(Long startOfDay) {
        this.mStartOfDay = startOfDay;
    }

    public String getShippingId() {
        return mShippingId;
    }

    public void setShippingId(String shippingId) {
        this.mShippingId = shippingId;
    }

    public String getTrailerIds() {
        return mTrailerIds;
    }

    public void setTrailerIds(String trailerIds) {
        this.mTrailerIds = trailerIds;
    }

    public String getCoDriverIds() {
        return mCoDriverIds;
    }

    public void setCoDriverIds(String coDriverIds) {
        this.mCoDriverIds = coDriverIds;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public String getDutyCycle() {
        return mDutyCycle;
    }

    public void setDutyCycle(String dutyCycle) {
        this.mDutyCycle = dutyCycle;
    }

    public HomeTerminal getHomeTerminal() {
        return mHomeTerminal;
    }

    public void setHomeTerminal(HomeTerminal homeTerminal) {
        this.mHomeTerminal = homeTerminal;
    }

    public String getAdditions() {
        return mAdditions;
    }

    public void setAdditions(String additions) {
        this.mAdditions = additions;
    }

    public Boolean getSigned() {
        return mSigned;
    }

    public void setSigned(Boolean signed) {
        mSigned = signed;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LogSheetHeader{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mLogDay=").append(mLogDay);
        sb.append(", mVehicleId=").append(mVehicleId);
        sb.append(", mBoxId='").append(mBoxId);
        sb.append(", mStartOfDay=").append(mStartOfDay);
        sb.append(", mShippingId=").append(mShippingId);
        sb.append(", mTrailerIds='").append(mTrailerIds);
        sb.append(", mCoDriverIds=").append(mCoDriverIds);
        sb.append(", mComment=").append(mComment);
        sb.append(", mDutyCycle=").append(mDutyCycle);
        sb.append(", mHomeTerminal=").append(mHomeTerminal);
        sb.append(", mAdditions=").append(mAdditions);
        sb.append(", mSigned=").append(mSigned);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDriverId)
                .append(mLogDay)
                .append(mVehicleId)
                .append(mBoxId)
                .append(mStartOfDay)
                .append(mShippingId)
                .append(mTrailerIds)
                .append(mCoDriverIds)
                .append(mComment)
                .append(mDutyCycle)
                .append(mHomeTerminal)
                .append(mAdditions)
                .append(mSigned)
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
        LogSheetHeader rhs = ((LogSheetHeader) other);
        // field comparison
        return new EqualsBuilder().append(mDriverId, rhs.mDriverId)
                .append(mLogDay, rhs.mLogDay)
                .append(mVehicleId, rhs.mVehicleId)
                .append(mBoxId, rhs.mBoxId)
                .append(mStartOfDay, rhs.mStartOfDay)
                .append(mShippingId, rhs.mShippingId)
                .append(mTrailerIds, rhs.mTrailerIds)
                .append(mCoDriverIds, rhs.mCoDriverIds)
                .append(mComment, rhs.mComment)
                .append(mDutyCycle, rhs.mDutyCycle)
                .append(mHomeTerminal, rhs.mHomeTerminal)
                .append(mAdditions, rhs.mAdditions)
                .append(mSigned, rhs.mSigned)
                .isEquals();
    }

    public static final Parcelable.Creator<LogSheetHeader> CREATOR = new Parcelable.Creator<LogSheetHeader>() {
        @Override
        public LogSheetHeader createFromParcel(Parcel source) {
            return new LogSheetHeader(source);
        }

        @Override
        public LogSheetHeader[] newArray(int size) {
            return new LogSheetHeader[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDriverId);
        dest.writeLong(this.mLogDay);
        dest.writeInt(this.mVehicleId);
        dest.writeInt(this.mBoxId);
        dest.writeLong(this.mStartOfDay);
        dest.writeString(this.mShippingId);
        dest.writeString(this.mTrailerIds);
        dest.writeString(this.mCoDriverIds);
        dest.writeString(this.mComment);
        dest.writeString(this.mDutyCycle);
        dest.writeParcelable(this.mHomeTerminal, flags);
        dest.writeString(this.mAdditions);
        dest.writeByte((byte) (mSigned ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

