package com.bsmwireless.models;

import android.os.Build;

import com.bsmwireless.common.Constants;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import app.bsmuniversal.com.BuildConfig;

public class AppInfo {
    @SerializedName("version")
    private String mVersion;

    @SerializedName("os")
    private String mOs;

    @SerializedName("deviceType")
    private String mDeviceType;

    @SerializedName("deviceModel")
    private String mDeviceModel;

    @SerializedName("serialNo")
    private String mSerialNumber;

    public AppInfo() {
        mVersion = BuildConfig.VERSION_NAME;
        mOs = Build.VERSION.RELEASE;
        mDeviceType = Constants.DEVICE_TYPE;
        mDeviceModel = Build.MANUFACTURER + " " + Build.MODEL;
        mSerialNumber = Build.SERIAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AppInfo appInfo = (AppInfo) o;

        return new EqualsBuilder()
                .append(mVersion, appInfo.mVersion)
                .append(mOs, appInfo.mOs)
                .append(mDeviceType, appInfo.mDeviceType)
                .append(mDeviceModel, appInfo.mDeviceModel)
                .append(mSerialNumber, appInfo.mSerialNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mVersion)
                .append(mOs)
                .append(mDeviceType)
                .append(mDeviceModel)
                .append(mSerialNumber)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppInfo{");
        sb.append("mVersion='").append(mVersion).append('\'');
        sb.append(", mOs='").append(mOs).append('\'');
        sb.append(", mDeviceType='").append(mDeviceType).append('\'');
        sb.append(", mDeviceModel='").append(mDeviceModel).append('\'');
        sb.append(", mSerialNumber='").append(mSerialNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
