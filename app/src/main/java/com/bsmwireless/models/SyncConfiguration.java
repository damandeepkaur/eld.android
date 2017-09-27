package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class SyncConfiguration implements Parcelable {
    public final static Parcelable.Creator<SyncConfiguration> CREATOR = new Creator<SyncConfiguration>() {

        @SuppressWarnings({
                "unchecked"
        })
        public SyncConfiguration createFromParcel(Parcel in) {
            SyncConfiguration instance = new SyncConfiguration();
            instance.mName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mValue = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public SyncConfiguration[] newArray(int size) {
            return (new SyncConfiguration[size]);
        }

    };

    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("value")
    @Expose
    private String mValue;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SyncConfiguration{");
        sb.append("mName='").append(mName).append('\'');
        sb.append(", mValue='").append(mValue).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mName).append(mValue).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SyncConfiguration)) {
            return false;
        }
        SyncConfiguration rhs = ((SyncConfiguration) other);
        return new EqualsBuilder().append(mName, rhs.mName).append(mValue, rhs.mValue).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mName);
        dest.writeValue(mValue);
    }

    public int describeContents() {
        return 0;
    }

    public enum Type {
        CYCLE("duty.cycle"),
        EXCEPT("hos.except"),
        MODE("special.mode"),
        REGISTRATION("eld.bsm.registrationid");

        private String mName;

        Type(String type) {
            mName = type;
        }

        public String getName() {
            return mName;
        }
    }

}
