package com.bsmwireless.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HomeTerminalInfo implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("address")
    @Expose
    private String mAddress;

    public HomeTerminalInfo() {
    }

    private HomeTerminalInfo(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
        this.mTimezone = in.readString();
        this.mAddress = in.readString();
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        this.mTimezone = timezone;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HomeTerminalInfo{");
        sb.append("mId=").append(mId);
        sb.append(", mName=").append(mName);
        sb.append(", mTimezone=").append(mTimezone);
        sb.append(", mAddress=").append(mAddress);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        // self check
        if (this == other) {
            return true;
        }
        // null check and type check (cast)
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        HomeTerminalInfo rhs = ((HomeTerminalInfo) other);
        // field comparison
        return new EqualsBuilder().append(mId, rhs.mId)
                .append(mName, rhs.mName)
                .append(mTimezone, rhs.mTimezone)
                .append(mAddress, rhs.mAddress)
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId)
                .append(mName)
                .append(mTimezone)
                .append(mAddress)
                .toHashCode();
    }

    public static final Creator<HomeTerminalInfo> CREATOR = new Creator<HomeTerminalInfo>() {
        @Override
        public HomeTerminalInfo createFromParcel(Parcel source) {
            return new HomeTerminalInfo(source);
        }

        @Override
        public HomeTerminalInfo[] newArray(int size) {
            return new HomeTerminalInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mTimezone);
        dest.writeString(this.mAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
