package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Carrier implements Parcelable {

    @SerializedName("dot")
    @Expose
    private String mDot;
    @SerializedName("orgId")
    @Expose
    private Integer mOrgId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("address")
    @Expose
    private String mAddress;
    @SerializedName("lastmodified")
    @Expose
    private Long mLastModified;
    public final static Parcelable.Creator<Carrier> CREATOR = new Creator<Carrier>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Carrier createFromParcel(Parcel in) {
            Carrier instance = new Carrier();
            instance.mDot = ((String) in.readValue((String.class.getClassLoader())));
            instance.mOrgId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mAddress = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLastModified = ((Long) in.readValue((Long.class.getClassLoader())));
            return instance;
        }

        public Carrier[] newArray(int size) {
            return (new Carrier[size]);
        }

    };

    public String getDot() {
        return mDot;
    }

    public void setDot(String dot) {
        this.mDot = dot;
    }

    public Integer getOrgId() {
        return mOrgId;
    }

    public void setOrgId(Integer orgId) {
        this.mOrgId = orgId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public Long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(Long lastModified) {
        this.mLastModified = lastModified;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Carrier{");
        sb.append("mDot='").append(mDot).append('\'');
        sb.append(", mOrgId=").append(mOrgId);
        sb.append(", mName='").append(mName).append('\'');
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append(", mLastmodified=").append(mLastModified);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDot).append(mOrgId).append(mName).append(mAddress).append(mLastModified).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Carrier)) {
            return false;
        }
        Carrier rhs = ((Carrier) other);
        return new EqualsBuilder().append(mDot, rhs.mDot).append(mOrgId, rhs.mOrgId).append(mName, rhs.mName).append(mAddress, rhs.mAddress).append(mLastModified, rhs.mLastModified).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mDot);
        dest.writeValue(mOrgId);
        dest.writeValue(mName);
        dest.writeValue(mAddress);
        dest.writeValue(mLastModified);
    }

    public int describeContents() {
        return  0;
    }

}
