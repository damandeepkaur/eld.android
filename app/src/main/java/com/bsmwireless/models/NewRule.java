package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class NewRule implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("driverid")
    @Expose
    private Integer mDriverId;
    @SerializedName("selecttime")
    @Expose
    private Long mSelectTime;
    @SerializedName("ruleexception")
    @Expose
    private Integer mRuleException;
    @SerializedName("country")
    @Expose
    private String mCountry;

    public final static Parcelable.Creator<NewRule> CREATOR = new Creator<NewRule>() {

        @SuppressWarnings({
            "unchecked"
        })
        public NewRule createFromParcel(Parcel in) {
            NewRule instance = new NewRule();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mDriverId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mSelectTime = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.mRuleException = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mCountry = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public NewRule[] newArray(int size) {
            return (new NewRule[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getDriverId() {
        return mDriverId;
    }

    public void setDriverId(Integer driverId) {
        this.mDriverId = driverId;
    }

    public Long getSelectTime() {
        return mSelectTime;
    }

    public void setSelectTime(Long selectTime) {
        this.mSelectTime = selectTime;
    }

    public Integer getRuleException() {
        return mRuleException;
    }

    public void setRuleException(Integer ruleException) {
        this.mRuleException = ruleException;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NewRule{");
        sb.append("mId=").append(mId);
        sb.append(", mDriverId=").append(mDriverId);
        sb.append(", mSelectTime=").append(mSelectTime);
        sb.append(", mRuleException=").append(mRuleException);
        sb.append(", mCountry='").append(mCountry).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).append(mDriverId).append(mSelectTime).append(mRuleException).append(mCountry).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NewRule)) {
            return false;
        }
        NewRule rhs = ((NewRule) other);
        return new EqualsBuilder().append(mId, rhs.mId).append(mDriverId, rhs.mDriverId).append(mSelectTime, rhs.mSelectTime).append(mRuleException, rhs.mRuleException).append(mCountry, rhs.mCountry).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mDriverId);
        dest.writeValue(mSelectTime);
        dest.writeValue(mRuleException);
        dest.writeValue(mCountry);
    }

    public int describeContents() {
        return  0;
    }
}
