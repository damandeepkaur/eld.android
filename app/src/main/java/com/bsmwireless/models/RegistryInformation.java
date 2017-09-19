package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class RegistryInformation implements Parcelable {

    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("ip")
    @Expose
    private String mIp;
    @SerializedName("port")
    @Expose
    private Integer mPort;

    public final static Parcelable.Creator<RegistryInformation> CREATOR = new Creator<RegistryInformation>() {

        @SuppressWarnings({
            "unchecked"
        })
        public RegistryInformation createFromParcel(Parcel in) {
            RegistryInformation instance = new RegistryInformation();
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mIp = ((String) in.readValue((String.class.getClassLoader())));
            instance.mPort = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public RegistryInformation[] newArray(int size) {
            return (new RegistryInformation[size]);
        }

    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        this.mIp = ip;
    }

    public Integer getPort() {
        return mPort;
    }

    public void setPort(Integer port) {
        this.mPort = port;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryInformation{");
        sb.append("mName='").append(mName).append('\'');
        sb.append(", mIp='").append(mIp).append('\'');
        sb.append(", mPort=").append(mPort);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mName).append(mIp).append(mPort).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RegistryInformation)) {
            return false;
        }
        RegistryInformation rhs = ((RegistryInformation) other);
        return new EqualsBuilder().append(mName, rhs.mName).append(mIp, rhs.mIp).append(mPort, rhs.mPort).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mName);
        dest.writeValue(mIp);
        dest.writeValue(mPort);
    }

    public int describeContents() {
        return  0;
    }
}
