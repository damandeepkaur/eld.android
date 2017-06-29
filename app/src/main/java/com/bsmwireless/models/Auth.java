
package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Auth implements Parcelable {

    @SerializedName("token")
    @Expose
    private String mToken;

    @SerializedName("cluster")
    @Expose
    private String mCluster;

    @SerializedName("domain")
    @Expose
    private String mDomain;

    @SerializedName("driverId")
    @Expose
    private Integer mDriverId;

    @SerializedName("orgId")
    @Expose
    private Integer mOrgId;

    @SerializedName("tokenExpire")
    @Expose
    private long mTokenExpire;

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

    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String domain) {
        this.mDomain = domain;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Integer getOrgId() {
        return mOrgId;
    }

    public void setOrgId(Integer orgId) {
        this.mOrgId = orgId;
    }

    public long getTokenExpire() {
        return mTokenExpire;
    }

    public void setTokenExpire(long tokenExpire) {
        mTokenExpire = tokenExpire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Auth auth = (Auth) o;

        return new EqualsBuilder()
                .append(mTokenExpire, auth.mTokenExpire)
                .append(mToken, auth.mToken)
                .append(mCluster, auth.mCluster)
                .append(mDomain, auth.mDomain)
                .append(mDriverId, auth.mDriverId)
                .append(mOrgId, auth.mOrgId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mToken)
                .append(mCluster)
                .append(mDomain)
                .append(mDriverId)
                .append(mOrgId)
                .append(mTokenExpire)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Auth{");
        sb.append("mToken='").append(mToken).append('\'');
        sb.append(", mCluster='").append(mCluster).append('\'');
        sb.append(", mDomain='").append(mDomain).append('\'');
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mOrgId=").append(mOrgId);
        sb.append(", mTokenExpire=").append(mTokenExpire);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mToken);
        dest.writeString(this.mCluster);
        dest.writeString(this.mDomain);
        dest.writeValue(this.mDriverId);
        dest.writeValue(this.mOrgId);
        dest.writeLong(this.mTokenExpire);
    }

    protected Auth(Parcel in) {
        this.mToken = in.readString();
        this.mCluster = in.readString();
        this.mDomain = in.readString();
        this.mDriverId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mOrgId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mTokenExpire = in.readLong();
    }

    public static final Creator<Auth> CREATOR = new Creator<Auth>() {
        @Override
        public Auth createFromParcel(Parcel source) {
            return new Auth(source);
        }

        @Override
        public Auth[] newArray(int size) {
            return new Auth[size];
        }
    };
}
