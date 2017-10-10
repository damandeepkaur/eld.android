package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class ELDUpdate implements Parcelable {

    @SerializedName("type")
    @Expose
    private Integer mType;
    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("mobileTime")
    @Expose
    private Long mMobileTime;
    @SerializedName("accept")
    @Expose
    private Boolean mAccept;

    public final static Parcelable.Creator<ELDUpdate> CREATOR = new Creator<ELDUpdate>() {

        @SuppressWarnings({
            "unchecked"
        })
        public ELDUpdate createFromParcel(Parcel in) {
            return new ELDUpdate(in);
        }

        public ELDUpdate[] newArray(int size) {
            return (new ELDUpdate[size]);
        }

    };

    public ELDUpdate() {}

    public ELDUpdate(ELDEvent event) {
        mType = event.getEventCode();
        mId = event.getId();
        mTimezone = event.getTimezone();
        mMobileTime = event.getMobileTime();
        mAccept = event.getDiagnostic();
    }

    public ELDUpdate(Parcel in) {
        mType = in.readInt();
        mId = in.readInt();
        mTimezone = in.readString();
        mMobileTime = in.readLong();
        mAccept = in.readByte() != 0;
    }

    public Integer getType() {
        return mType;
    }

    public ELDUpdate setType(Integer type) {
        mType = type;
        return this;
    }

    public Integer getId() {
        return mId;
    }

    public ELDUpdate setId(Integer id) {
        mId = id;
        return this;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public ELDUpdate setTimezone(String timezone) {
        mTimezone = timezone;
        return this;
    }

    public Long getMobileTime() {
        return mMobileTime;
    }

    public ELDUpdate setMobileTime(Long mobileTime) {
        mMobileTime = mobileTime;
        return this;
    }

    public Boolean getAccept() {
        return mAccept;
    }

    public ELDUpdate setAccept(Boolean accept) {
        mAccept = accept;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ELDUpdate{");
        sb.append("mType=").append(mType);
        sb.append(", mId=").append(mId);
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mMobileTime=").append(mMobileTime);
        sb.append(", mAccept=").append(mAccept);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(mType)
                .append(mId)
                .append(mTimezone)
                .append(mMobileTime)
                .append(mAccept)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ELDUpdate)) {
            return false;
        }
        ELDUpdate rhs = ((ELDUpdate) other);
        return new EqualsBuilder()
                .append(mType, rhs.mType)
                .append(mId, rhs.mId)
                .append(mTimezone, rhs.mTimezone)
                .append(mMobileTime, rhs.mMobileTime)
                .append(mAccept, rhs.mAccept)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mType);
        dest.writeInt(mId);
        dest.writeString(mTimezone);
        dest.writeLong(mMobileTime);
        dest.writeByte((byte) (mAccept ? 1 : 0));
    }

    public int describeContents() {
        return  0;
    }

}
