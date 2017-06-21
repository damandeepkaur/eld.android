package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Driver implements Parcelable {

    @SerializedName("username")
    @Expose
    private String mUserName;
    @SerializedName("password")
    @Expose
    private String mPassword;
    @SerializedName("firstname")
    @Expose
    private String mFirstName;
    @SerializedName("midname")
    @Expose
    private String mMidName;
    @SerializedName("lastname")
    @Expose
    private String mLastName;
    @SerializedName("cycle")
    @Expose
    private Integer mCycle;
    @SerializedName("ruleexception")
    @Expose
    private String mRuleException;
    @SerializedName("address")
    @Expose
    private String mAddress;
    @SerializedName("city")
    @Expose
    private String mCity;
    @SerializedName("state")
    @Expose
    private String mState;
    @SerializedName("country")
    @Expose
    private String mCountry;
    @SerializedName("signature")
    @Expose
    private String mSignature;
    @SerializedName("timezone")
    @Expose
    private Double mTimezone;
    @SerializedName("dst")
    @Expose
    private Boolean mDst;
    @SerializedName("license")
    @Expose
    private String mLicense;
    @SerializedName("email")
    @Expose
    private String mEmail;
    @SerializedName("lastmodified")
    @Expose
    private Long mLastModified;

    public final static Parcelable.Creator<Driver> CREATOR = new Creator<Driver>() {

        @SuppressWarnings({
            "unchecked"
        })
        public Driver createFromParcel(Parcel in) {
            Driver instance = new Driver();
            instance.mUserName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mPassword = ((String) in.readValue((String.class.getClassLoader())));
            instance.mFirstName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mMidName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLastName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mCycle = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mRuleException = ((String) in.readValue((String.class.getClassLoader())));
            instance.mAddress = ((String) in.readValue((String.class.getClassLoader())));
            instance.mCity = ((String) in.readValue((String.class.getClassLoader())));
            instance.mState = ((String) in.readValue((String.class.getClassLoader())));
            instance.mCountry = ((String) in.readValue((String.class.getClassLoader())));
            instance.mSignature = ((String) in.readValue((String.class.getClassLoader())));
            instance.mTimezone = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.mDst = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mEmail = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLastModified = ((Long) in.readValue((Long.class.getClassLoader())));
            return instance;
        }

        public Driver[] newArray(int size) {
            return (new Driver[size]);
        }

    };

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getMidName() {
        return mMidName;
    }

    public void setMidName(String midName) {
        this.mMidName = midName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public Integer getCycle() {
        return mCycle;
    }

    public void setCycle(Integer cycle) {
        this.mCycle = cycle;
    }

    public String getRuleException() {
        return mRuleException;
    }

    public void setRuleException(String ruleException) {
        this.mRuleException = ruleException;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        this.mState = state;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        this.mSignature = signature;
    }

    public Double getTimezone() {
        return mTimezone;
    }

    public void setTimezone(Double timezone) {
        this.mTimezone = timezone;
    }

    public Boolean getDst() {
        return mDst;
    }

    public void setDst(Boolean dst) {
        this.mDst = dst;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        this.mLicense = license;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public Long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(Long lastModified) {
        this.mLastModified = lastModified;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Driver{");
        sb.append("mUserName='").append(mUserName).append('\'');
        sb.append(", mPassword='").append(mPassword).append('\'');
        sb.append(", mFirstName='").append(mFirstName).append('\'');
        sb.append(", mMidName='").append(mMidName).append('\'');
        sb.append(", mLastName='").append(mLastName).append('\'');
        sb.append(", mCycle=").append(mCycle);
        sb.append(", mRuleException='").append(mRuleException).append('\'');
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append(", mCity='").append(mCity).append('\'');
        sb.append(", mState='").append(mState).append('\'');
        sb.append(", mCountry='").append(mCountry).append('\'');
        sb.append(", mSignature='").append(mSignature).append('\'');
        sb.append(", mTimezone=").append(mTimezone);
        sb.append(", mDst=").append(mDst);
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mEmail='").append(mEmail).append('\'');
        sb.append(", mLastModified=").append(mLastModified);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mUserName).append(mPassword).append(mFirstName).append(mMidName).append(mLastName).append(mCycle).append(mRuleException).append(mAddress).append(mCity).append(mState).append(mCountry).append(mSignature).append(mTimezone).append(mDst).append(mLicense).append(mEmail).append(mLastModified).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Driver)) {
            return false;
        }
        Driver rhs = ((Driver) other);
        return new EqualsBuilder().append(mUserName, rhs.mUserName).append(mPassword, rhs.mPassword).append(mFirstName, rhs.mFirstName).append(mMidName, rhs.mMidName).append(mLastName, rhs.mLastName).append(mCycle, rhs.mCycle).append(mRuleException, rhs.mRuleException).append(mAddress, rhs.mAddress).append(mCity, rhs.mCity).append(mState, rhs.mState).append(mCountry, rhs.mCountry).append(mSignature, rhs.mSignature).append(mTimezone, rhs.mTimezone).append(mDst, rhs.mDst).append(mLicense, rhs.mLicense).append(mEmail, rhs.mEmail).append(mLastModified, rhs.mLastModified).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mUserName);
        dest.writeValue(mPassword);
        dest.writeValue(mFirstName);
        dest.writeValue(mMidName);
        dest.writeValue(mLastName);
        dest.writeValue(mCycle);
        dest.writeValue(mRuleException);
        dest.writeValue(mAddress);
        dest.writeValue(mCity);
        dest.writeValue(mState);
        dest.writeValue(mCountry);
        dest.writeValue(mSignature);
        dest.writeValue(mTimezone);
        dest.writeValue(mDst);
        dest.writeValue(mLicense);
        dest.writeValue(mEmail);
        dest.writeValue(mLastModified);
    }

    public int describeContents() {
        return  0;
    }
}
