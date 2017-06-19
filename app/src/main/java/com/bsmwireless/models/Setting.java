package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Setting implements Parcelable {
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

    public Setting() {
    }

    protected Setting(Parcel in) {
        this.mName = in.readString();
        this.mValue = in.readString();
    }

    public static final Parcelable.Creator<Setting> CREATOR = new Parcelable.Creator<Setting>() {
        @Override
        public Setting createFromParcel(Parcel source) {
            return new Setting(source);
        }

        @Override
        public Setting[] newArray(int size) {
            return new Setting[size];
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

        Setting setting = (Setting) o;

        if (mName != null ? !mName.equals(setting.mName) : setting.mName != null) return false;
        return mValue != null ? mValue.equals(setting.mValue) : setting.mValue == null;

    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + (mValue != null ? mValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Setting{");
        sb.append("mName='").append(mName).append('\'');
        sb.append(", mValue='").append(mValue).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
