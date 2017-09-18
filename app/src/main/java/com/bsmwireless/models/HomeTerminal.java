package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class HomeTerminal implements Parcelable {

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
    public final static Parcelable.Creator<HomeTerminal> CREATOR = new Creator<HomeTerminal>() {

        @SuppressWarnings({
            "unchecked"
        })
        public HomeTerminal createFromParcel(Parcel in) {
            HomeTerminal instance = new HomeTerminal();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mTimezone = ((String) in.readValue((String.class.getClassLoader())));
            instance.mAddress = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public HomeTerminal[] newArray(int size) {
            return (new HomeTerminal[size]);
        }

    };

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
        final StringBuilder sb = new StringBuilder("HomeTerminal{");
        sb.append("mId=").append(mId);
        sb.append(", mName='").append(mName).append('\'');
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId)
                                    .append(mName)
                                    .append(mTimezone)
                                    .append(mAddress)
                                    .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof HomeTerminal)) {
            return false;
        }
        HomeTerminal rhs = ((HomeTerminal) other);
        return new EqualsBuilder().append(mId, rhs.mId)
                                  .append(mName, rhs.mName)
                                  .append(mTimezone, rhs.mTimezone)
                                  .append(mAddress, rhs.mAddress)
                                  .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mName);
        dest.writeValue(mTimezone);
        dest.writeValue(mAddress);
    }

    public int describeContents() {
        return  0;
    }

}
