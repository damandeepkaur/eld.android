package com.bsmwireless.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class LoginModel {
    @SerializedName("username")
    private String mUsername;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("domain")
    private String mDomain;

    @SerializedName("driverType")
    private Integer mDriverType;

    @SerializedName("lastUpdated")
    private long mLastUpdated;

    @SerializedName("appInfo")
    private String mAppInfo;

    public LoginModel() {
        mAppInfo = new Gson().toJson(new AppInfo());
    }

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

    public Integer getDriverType() {
        return mDriverType;
    }

    public void setDriverType(Integer driverType) {
        mDriverType = driverType;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        mLastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LoginModel loginModel = (LoginModel) o;

        return new EqualsBuilder()
                .append(mLastUpdated, loginModel.mLastUpdated)
                .append(mUsername, loginModel.mUsername)
                .append(mPassword, loginModel.mPassword)
                .append(mDomain, loginModel.mDomain)
                .append(mDriverType, loginModel.mDriverType)
                .append(mAppInfo, loginModel.mAppInfo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mUsername)
                .append(mPassword)
                .append(mDomain)
                .append(mDriverType)
                .append(mLastUpdated)
                .append(mAppInfo)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginModel{");
        sb.append("mUsername='").append(mUsername).append('\'');
        sb.append(", mPassword='").append(mPassword).append('\'');
        sb.append(", mDomain='").append(mDomain).append('\'');
        sb.append(", mDriverType=").append(mDriverType);
        sb.append(", mLastUpdated=").append(mLastUpdated);
        sb.append(", mAppInfo=").append(mAppInfo);
        sb.append('}');
        return sb.toString();
    }
}
