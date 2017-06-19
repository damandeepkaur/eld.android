package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class HOSAlert implements Parcelable {

    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("boxid")
    @Expose
    private Integer mBoxId;
    @SerializedName("datetime")
    @Expose
    private Long mDateTime;
    @SerializedName("email")
    @Expose
    private String mEmail;
    @SerializedName("notes")
    @Expose
    private String mNotes;

    public final static Parcelable.Creator<HOSAlert> CREATOR = new Creator<HOSAlert>() {

        @SuppressWarnings({
            "unchecked"
        })
        public HOSAlert createFromParcel(Parcel in) {
            HOSAlert instance = new HOSAlert();
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mBoxId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDateTime = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mEmail = ((String) in.readValue((String.class.getClassLoader())));
            instance.mNotes = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public HOSAlert[] newArray(int size) {
            return (new HOSAlert[size]);
        }

    };

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

    public Long getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Long dateTime) {
        this.mDateTime = dateTime;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        this.mNotes = notes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HOSAlert{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mBoxId=").append(mBoxId);
        sb.append(", mDateTime=").append(mDateTime);
        sb.append(", mEmail='").append(mEmail).append('\'');
        sb.append(", mNotes='").append(mNotes).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDriverId).append(mBoxId).append(mDateTime).append(mEmail).append(mNotes).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof HOSAlert)) {
            return false;
        }
        HOSAlert rhs = ((HOSAlert) other);
        return new EqualsBuilder().append(mDriverId, rhs.mDriverId).append(mBoxId, rhs.mBoxId).append(mDateTime, rhs.mDateTime).append(mEmail, rhs.mEmail).append(mNotes, rhs.mNotes).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mDriverId);
        dest.writeValue(mBoxId);
        dest.writeValue(mDateTime);
        dest.writeValue(mEmail);
        dest.writeValue(mNotes);
    }

    public int describeContents() {
        return  0;
    }
}
