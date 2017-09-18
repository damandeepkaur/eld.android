package com.bsmwireless.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class EmailReport implements Parcelable {

    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("email")
    @Expose
    private String mEmail;
    @SerializedName("from")
    @Expose
    private Long mFrom;
    @SerializedName("to")
    @Expose
    private Long mTo;
    @SerializedName("types")
    @Expose
    private List<Integer> mTypes = null;

    public final static Parcelable.Creator<EmailReport> CREATOR = new Creator<EmailReport>() {

        @SuppressWarnings({
            "unchecked"
        })
        public EmailReport createFromParcel(Parcel in) {
            EmailReport instance = new EmailReport();
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mEmail = ((String) in.readValue((String.class.getClassLoader())));
            instance.mFrom = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mTo = ((Long) in.readValue((Long.class.getClassLoader())));
            in.readList(instance.mTypes, (java.lang.Integer.class.getClassLoader()));
            return instance;
        }

        public EmailReport[] newArray(int size) {
            return (new EmailReport[size]);
        }

    };

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public Long getFrom() {
        return mFrom;
    }

    public void setFrom(Long from) {
        this.mFrom = from;
    }

    public Long getTo() {
        return mTo;
    }

    public void setTo(Long to) {
        this.mTo = to;
    }

    public List<Integer> getTypes() {
        return mTypes;
    }

    public void setTypes(List<Integer> types) {
        this.mTypes = types;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EmailReport{");
        sb.append("mDriverId=").append(mDriverId);
        sb.append(", mEmail='").append(mEmail).append('\'');
        sb.append(", mFrom=").append(mFrom);
        sb.append(", mTo=").append(mTo);
        sb.append(", mTypes=").append(mTypes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDriverId).append(mEmail).append(mFrom).append(mTo).append(mTypes).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EmailReport)) {
            return false;
        }
        EmailReport rhs = ((EmailReport) other);
        return new EqualsBuilder().append(mDriverId, rhs.mDriverId).append(mEmail, rhs.mEmail).append(mFrom, rhs.mFrom).append(mTo, rhs.mTo).append(mTypes, rhs.mTypes).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mDriverId);
        dest.writeValue(mEmail);
        dest.writeValue(mFrom);
        dest.writeValue(mTo);
        dest.writeList(mTypes);
    }

    public int describeContents() {
        return  0;
    }
}
