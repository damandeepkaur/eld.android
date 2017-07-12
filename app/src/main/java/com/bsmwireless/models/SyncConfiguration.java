package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SyncConfiguration implements Parcelable {
    @SerializedName("name")
    private String mName;

    @SerializedName("value")
    private String mValue;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mValue);
    }

    public SyncConfiguration() {
    }

    protected SyncConfiguration(Parcel in) {
        this.mName = in.readString();
        this.mValue = in.readString();
    }

    public static final Parcelable.Creator<SyncConfiguration> CREATOR = new Parcelable.Creator<SyncConfiguration>() {
        @Override
        public SyncConfiguration createFromParcel(Parcel source) {
            return new SyncConfiguration(source);
        }

        @Override
        public SyncConfiguration[] newArray(int size) {
            return new SyncConfiguration[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SyncConfiguration setting = (SyncConfiguration) o;

        return new EqualsBuilder()
                .append(mName, setting.mName)
                .append(mValue, setting.mValue)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mName)
                .append(mValue)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SyncConfiguration{");
        sb.append("mName='").append(mName).append('\'');
        sb.append(", mValue='").append(mValue).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
