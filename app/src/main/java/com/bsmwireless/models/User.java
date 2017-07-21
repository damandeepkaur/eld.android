
package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class User implements Parcelable {
    public enum DriverType {
        DRIVER,
        CO_DRIVER,
        EXEMPT,
        CARRIER
    }

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("username")
    @Expose
    private String mUsername;
    @SerializedName("password")
    @Expose
    private String mPassword;
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("email")
    @Expose
    private String mEmail;
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
    @SerializedName("license")
    @Expose
    private String mLicense;
    @SerializedName("signature")
    @Expose
    private String mSignature;
    @SerializedName("exempt")
    @Expose
    private Boolean mExempt;
    @SerializedName("updated")
    @Expose
    private Boolean mUpdated;
    @SerializedName("organization")
    @Expose
    private String mOrganization;
    @SerializedName("configuration")
    @Expose
    private List<Configuration> mConfigurations;
    @SerializedName("syncTime")
    @Expose
    private Long mSyncTime;
    @SerializedName("auth")
    @Expose
    private Auth mAuth;
    @SerializedName("firstName")
    @Expose
    private String mFirstName;
    @SerializedName("midName")
    @Expose
    private String mMidName;
    @SerializedName("lastName")
    @Expose
    private String mLastName;
    @SerializedName("ruleException")
    @Expose
    private String mRuleException;
    @SerializedName("applyDST")
    @Expose
    private Integer mApplyDST;
    @SerializedName("usCycle")
    @Expose
    private Integer mUsCycle;
    @SerializedName("caCycle")
    @Expose
    private Integer mCaCycle;
    @SerializedName("cycleCountry")
    @Expose
    private Integer mCycleCountry;
    @SerializedName("orgAddr")
    @Expose
    private String mOrgAddr;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        mLicense = license;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    public Boolean getExempt() {
        return mExempt;
    }

    public void setExempt(Boolean exempt) {
        this.mExempt = exempt;
    }

    public Boolean getUpdated() {
        return mUpdated;
    }

    public void setUpdated(Boolean updated) {
        mUpdated = updated;
    }

    public String getOrganization() {
        return mOrganization;
    }

    public void setOrganization(String organization) {
        mOrganization = organization;
    }

    public List<Configuration> getConfigurations() {
        return mConfigurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        mConfigurations = configurations;
    }

    public Long getSyncTime() {
        return mSyncTime;
    }

    public void setSyncTime(Long syncTime) {
        mSyncTime = syncTime;
    }

    public Auth getAuth() {
        return mAuth;
    }

    public void setAuth(Auth auth) {
        mAuth = auth;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getMidName() {
        return mMidName;
    }

    public void setMidName(String midName) {
        mMidName = midName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getRuleException() {
        return mRuleException;
    }

    public void setRuleException(String ruleException) {
        mRuleException = ruleException;
    }

    public Integer getApplyDST() {
        return mApplyDST;
    }

    public void setApplyDST(Integer applyDST) {
        mApplyDST = applyDST;
    }

    public Integer getUsCycle() {
        return mUsCycle;
    }

    public void setUsCycle(Integer usCycle) {
        mUsCycle = usCycle;
    }

    public Integer getCaCycle() {
        return mCaCycle;
    }

    public void setCaCycle(Integer caCycle) {
        mCaCycle = caCycle;
    }

    public Integer getCycleCountry() {
        return mCycleCountry;
    }

    public void setCycleCountry(Integer cycleCountry) {
        mCycleCountry = cycleCountry;
    }

    public String getOrgAddr() {
        return mOrgAddr;
    }

    public void setOrgAddr(String orgAddr) {
        mOrgAddr = orgAddr;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .append(mId, user.mId)
                .append(mTimezone, user.mTimezone)
                .append(mEmail, user.mEmail)
                .append(mAddress, user.mAddress)
                .append(mCity, user.mCity)
                .append(mState, user.mState)
                .append(mCountry, user.mCountry)
                .append(mLicense, user.mLicense)
                .append(mSignature, user.mSignature)
                .append(mExempt, user.mExempt)
                .append(mUpdated, user.mUpdated)
                .append(mOrganization, user.mOrganization)
                .append(mConfigurations, user.mConfigurations)
                .append(mSyncTime, user.mSyncTime)
                .append(mAuth, user.mAuth)
                .append(mFirstName, user.mFirstName)
                .append(mMidName, user.mMidName)
                .append(mLastName, user.mLastName)
                .append(mRuleException, user.mRuleException)
                .append(mApplyDST, user.mApplyDST)
                .append(mUsCycle, user.mUsCycle)
                .append(mCaCycle, user.mCaCycle)
                .append(mCycleCountry, user.mCycleCountry)
                .append(mOrgAddr, user.mOrgAddr)
                .append(mUsername, user.mUsername)
                .append(mPassword, user.mPassword)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mId)
                .append(mTimezone)
                .append(mEmail)
                .append(mAddress)
                .append(mCity)
                .append(mState)
                .append(mCountry)
                .append(mLicense)
                .append(mSignature)
                .append(mExempt)
                .append(mUpdated)
                .append(mOrganization)
                .append(mConfigurations)
                .append(mSyncTime)
                .append(mAuth)
                .append(mFirstName)
                .append(mMidName)
                .append(mLastName)
                .append(mRuleException)
                .append(mApplyDST)
                .append(mUsCycle)
                .append(mCaCycle)
                .append(mCycleCountry)
                .append(mOrgAddr)
                .append(mUsername)
                .append(mPassword)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("mId=").append(mId);
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mEmail='").append(mEmail).append('\'');
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append(", mCity='").append(mCity).append('\'');
        sb.append(", mState='").append(mState).append('\'');
        sb.append(", mCountry='").append(mCountry).append('\'');
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mSignature='").append(mSignature).append('\'');
        sb.append(", mExempt='").append(mExempt).append('\'');
        sb.append(", mUpdated=").append(mUpdated);
        sb.append(", mOrganization='").append(mOrganization).append('\'');
        sb.append(", mConfigurations=").append(mConfigurations);
        sb.append(", mSyncTime=").append(mSyncTime);
        sb.append(", mAuth=").append(mAuth);
        sb.append(", mFirstName='").append(mFirstName).append('\'');
        sb.append(", mMidName='").append(mMidName).append('\'');
        sb.append(", mLastName='").append(mLastName).append('\'');
        sb.append(", mRuleException='").append(mRuleException).append('\'');
        sb.append(", mApplyDST=").append(mApplyDST);
        sb.append(", mUsCycle=").append(mUsCycle);
        sb.append(", mCaCycle=").append(mCaCycle);
        sb.append(", mCycleCountry=").append(mCycleCountry);
        sb.append(", mOrgAddr='").append(mOrgAddr).append('\'');
        sb.append(", mUsername='").append(mUsername).append('\'');
        sb.append(", mPassword='").append(mPassword).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mId);
        dest.writeString(this.mUsername);
        dest.writeString(this.mPassword);
        dest.writeString(this.mTimezone);
        dest.writeString(this.mEmail);
        dest.writeString(this.mAddress);
        dest.writeString(this.mCity);
        dest.writeString(this.mState);
        dest.writeString(this.mCountry);
        dest.writeString(this.mLicense);
        dest.writeString(this.mSignature);
        dest.writeByte((byte) (this.mExempt ? 1 : 0));
        dest.writeValue(this.mUpdated);
        dest.writeString(this.mOrganization);
        dest.writeTypedList(this.mConfigurations);
        dest.writeValue(this.mSyncTime);
        dest.writeParcelable(this.mAuth, flags);
        dest.writeString(this.mFirstName);
        dest.writeString(this.mMidName);
        dest.writeString(this.mLastName);
        dest.writeString(this.mRuleException);
        dest.writeValue(this.mApplyDST);
        dest.writeValue(this.mUsCycle);
        dest.writeValue(this.mCaCycle);
        dest.writeValue(this.mCycleCountry);
        dest.writeString(this.mOrgAddr);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.mId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mUsername = in.readString();
        this.mPassword = in.readString();
        this.mTimezone = in.readString();
        this.mEmail = in.readString();
        this.mAddress = in.readString();
        this.mCity = in.readString();
        this.mState = in.readString();
        this.mCountry = in.readString();
        this.mLicense = in.readString();
        this.mSignature = in.readString();
        this.mExempt = in.readByte() != 0;
        this.mUpdated = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mOrganization = in.readString();
        this.mConfigurations = in.createTypedArrayList(Configuration.CREATOR);
        this.mSyncTime = (Long) in.readValue(Long.class.getClassLoader());
        this.mAuth = in.readParcelable(Auth.class.getClassLoader());
        this.mFirstName = in.readString();
        this.mMidName = in.readString();
        this.mLastName = in.readString();
        this.mRuleException = in.readString();
        this.mApplyDST = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mUsCycle = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mCaCycle = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mCycleCountry = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mOrgAddr = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
