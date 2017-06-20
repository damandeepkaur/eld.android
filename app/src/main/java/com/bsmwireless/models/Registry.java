package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Registry implements Parcelable {

    @SerializedName("domain")
    @Expose
    private String mDomain;
    @SerializedName("user")
    @Expose
    private String mUser;
    @SerializedName("password")
    @Expose
    private String mPassword;

    public final static Parcelable.Creator<Registry> CREATOR = new Creator<Registry>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Registry createFromParcel(Parcel in) {
            Registry instance = new Registry();
            instance.mDomain = ((String) in.readValue((String.class.getClassLoader())));
            instance.mUser = ((String) in.readValue((String.class.getClassLoader())));
            instance.mPassword = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Registry[] newArray(int size) {
            return (new Registry[size]);
        }

    };

    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String domain) {
        this.mDomain = domain;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        this.mUser = user;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Registry{");
        sb.append("mDomain='").append(mDomain).append('\'');
        sb.append(", mUser='").append(mUser).append('\'');
        sb.append(", mPassword='").append(mPassword).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDomain).append(mUser).append(mPassword).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Registry)) {
            return false;
        }
        Registry rhs = ((Registry) other);
        return new EqualsBuilder().append(mDomain, rhs.mDomain).append(mUser, rhs.mUser).append(mPassword, rhs.mPassword).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mDomain);
        dest.writeValue(mUser);
        dest.writeValue(mPassword);
    }

    public int describeContents() {
        return  0;
    }
}
