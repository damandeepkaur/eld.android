package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class ReportTrailer implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("license")
    @Expose
    private String mLicense;
    @SerializedName("state")
    @Expose
    private String mState;

    public final static Parcelable.Creator<ReportTrailer> CREATOR = new Creator<ReportTrailer>() {

        @SuppressWarnings({
            "unchecked"
        })
        public ReportTrailer createFromParcel(Parcel in) {
            ReportTrailer instance = new ReportTrailer();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mState = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ReportTrailer[] newArray(int size) {
            return (new ReportTrailer[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        this.mLicense = license;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        this.mState = state;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReportTrailer{");
        sb.append("mId=").append(mId);
        sb.append(", mName='").append(mName).append('\'');
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mState='").append(mState).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).append(mName).append(mLicense).append(mState).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ReportTrailer)) {
            return false;
        }
        ReportTrailer rhs = ((ReportTrailer) other);
        return new EqualsBuilder().append(mId, rhs.mId).append(mName, rhs.mName).append(mLicense, rhs.mLicense).append(mState, rhs.mState).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mName);
        dest.writeValue(mLicense);
        dest.writeValue(mState);
    }

    public int describeContents() {
        return  0;
    }
}
