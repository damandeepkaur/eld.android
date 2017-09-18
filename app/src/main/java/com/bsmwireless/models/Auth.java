package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Auth implements Parcelable {

    @SerializedName("token")
    @Expose
    private String mToken;
    @SerializedName("cluster")
    @Expose
    private String mCluster;
    @SerializedName("orgId")
    @Expose
    private Integer mOrgId;
    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;
    @SerializedName("expire")
    @Expose
    private Long mExpire;
    public final static Parcelable.Creator<Auth> CREATOR = new Creator<Auth>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Auth createFromParcel(Parcel in) {
            Auth instance = new Auth();
            instance.mToken = ((String) in.readValue((String.class.getClassLoader())));
            instance.mCluster = ((String) in.readValue((String.class.getClassLoader())));
            instance.mOrgId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mExpire = ((Long) in.readValue((Long.class.getClassLoader())));
            return instance;
        }

        public Auth[] newArray(int size) {
            return (new Auth[size]);
        }

    };

    public Auth(int driverId) {
        mDriverId = driverId;
    }

    public Auth() {
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getCluster() {
        return mCluster;
    }

    public void setCluster(String cluster) {
        this.mCluster = cluster;
    }

    public Integer getOrgId() {
        return mOrgId;
    }

    public void setOrgId(Integer orgId) {
        this.mOrgId = orgId;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Long getExpire() {
        return mExpire;
    }

    public void setExpire(Long expire) {
        this.mExpire = expire;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Auth{");
        sb.append("mToken='").append(mToken).append('\'');
        sb.append(", mCluster='").append(mCluster).append('\'');
        sb.append(", mOrgId=").append(mOrgId);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mExpire=").append(mExpire);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mToken).append(mCluster).append(mOrgId).append(mDriverId).append(mExpire).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Auth)) {
            return false;
        }
        Auth rhs = ((Auth) other);
        return new EqualsBuilder().append(mToken, rhs.mToken).append(mCluster, rhs.mCluster).append(mOrgId, rhs.mOrgId).append(mDriverId, rhs.mDriverId).append(mExpire, rhs.mExpire).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mToken);
        dest.writeValue(mCluster);
        dest.writeValue(mOrgId);
        dest.writeValue(mDriverId);
        dest.writeValue(mExpire);
    }

    public int describeContents() {
        return  0;
    }

}
