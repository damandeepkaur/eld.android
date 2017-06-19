package com.bsmwireless.models;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LoginRequest {
    @SerializedName("username")
    private String mUsername;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("domain")
    private String mDomain;

    @SerializedName("lastUpdated")
    private long mLastUpdated;

    @SerializedName("appVersion")
    private String mAppVersion;

    @SerializedName("deviceType")
    private String mDeviceType;

    @SerializedName("osVersion")
    private String mOsVersion;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String domain) {
        mDomain = domain;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        mLastUpdated = lastUpdated;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public void setAppVersion(String appVersion) {
        mAppVersion = appVersion;
    }

    public String getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(String deviceType) {
        mDeviceType = deviceType;
    }

    public String getOsVersion() {
        return mOsVersion;
    }

    public void setOsVersion(String osVersion) {
        mOsVersion = osVersion;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginRequest{");
        sb.append("mUsername='").append(mUsername).append('\'');
        sb.append(", mPassword='").append(mPassword).append('\'');
        sb.append(", mDomain='").append(mDomain).append('\'');
        sb.append(", mLastUpdated=").append(mLastUpdated);
        sb.append(", mAppVersion='").append(mAppVersion).append('\'');
        sb.append(", mDeviceType='").append(mDeviceType).append('\'');
        sb.append(", mOsVersion='").append(mOsVersion).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof LoginRequest)) return false;

        LoginRequest that = (LoginRequest) o;

        return new EqualsBuilder()
                .append(mLastUpdated, that.mLastUpdated)
                .append(mUsername, that.mUsername)
                .append(mPassword, that.mPassword)
                .append(mDomain, that.mDomain)
                .append(mAppVersion, that.mAppVersion)
                .append(mDeviceType, that.mDeviceType)
                .append(mOsVersion, that.mOsVersion)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mUsername)
                .append(mPassword)
                .append(mDomain)
                .append(mLastUpdated)
                .append(mAppVersion)
                .append(mDeviceType)
                .append(mOsVersion)
                .toHashCode();
    }
}
