package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ClearNote implements Parcelable
{

    @SerializedName("cleared")
    @Expose
    private Boolean mCleared;
    @SerializedName("datetime")
    @Expose
    private String mDateTime;
    @SerializedName("note")
    @Expose
    private String mNote;
    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("drivername")
    @Expose
    private String mDriverName;
    @SerializedName("email")
    @Expose
    private String mEmail;

    public final static Parcelable.Creator<ClearNote> CREATOR = new Creator<ClearNote>() {

        @SuppressWarnings({
            "unchecked"
        })
        public ClearNote createFromParcel(Parcel in) {
            ClearNote instance = new ClearNote();
            instance.mCleared = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mDateTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.mNote = ((String) in.readValue((String.class.getClassLoader())));
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDriverName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mEmail = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ClearNote[] newArray(int size) {
            return (new ClearNote[size]);
        }

    };

    public Boolean getCleared() {
        return mCleared;
    }

    public void setCleared(Boolean cleared) {
        this.mCleared = cleared;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        this.mDateTime = dateTime;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public String getDriverName() {
        return mDriverName;
    }

    public void setDriverName(String driverName) {
        this.mDriverName = driverName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClearNote{");
        sb.append("cleared=").append(mCleared);
        sb.append(", datetime='").append(mDateTime).append('\'');
        sb.append(", note='").append(mNote).append('\'');
        sb.append(", driverid=").append(mDriverId);
        sb.append(", drivername='").append(mDriverName).append('\'');
        sb.append(", email='").append(mEmail).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mCleared).append(mDateTime).append(mNote).append(mDriverId).append(mDriverName).append(mEmail).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ClearNote)) {
            return false;
        }
        ClearNote rhs = ((ClearNote) other);
        return new EqualsBuilder().append(mCleared, rhs.mCleared).append(mDateTime, rhs.mDateTime).append(mNote, rhs.mNote).append(mDriverId, rhs.mDriverId).append(mDriverName, rhs.mDriverName).append(mEmail, rhs.mEmail).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mCleared);
        dest.writeValue(mDateTime);
        dest.writeValue(mNote);
        dest.writeValue(mDriverId);
        dest.writeValue(mDriverName);
        dest.writeValue(mEmail);
    }

    public int describeContents() {
        return  0;
    }
}
