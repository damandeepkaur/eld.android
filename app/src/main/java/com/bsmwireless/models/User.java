
package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer mId;
    @SerializedName("orgid")
    @Expose
    private Integer mOrgId;
    @SerializedName("employeeid")
    @Expose
    private String mEmployeeId;
    @SerializedName("password")
    @Expose
    private String mPassword;
    @SerializedName("issupervisor")
    @Expose
    private Integer mIsSupervisorInt;
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("signatureid")
    @Expose
    private Integer mSignatureId;
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
    @SerializedName("updated")
    @Expose
    private Boolean mUpdated;
    @SerializedName("organization")
    @Expose
    private String mOrganization;
    @SerializedName("configuration")
    @Expose
    private List<Setting> mSetting;
    @SerializedName("auth")
    @Expose
    private Auth mAuth;
    @SerializedName("isSupervisor")
    @Expose
    private Boolean mIsSupervisor;
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
    @SerializedName("lastModified")
    @Expose
    private Long mLastModified;
    @SerializedName("orgAddr")
    @Expose
    private String mOrgAddr;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public Integer getOrgId() {
        return mOrgId;
    }

    public void setOrgId(Integer orgId) {
        this.mOrgId = orgId;
    }

    public String getEmployeeId() {
        return mEmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.mEmployeeId = employeeId;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public Integer getIsSupervisorInt() {
        return mIsSupervisorInt;
    }

    public void setIsSupervisorInt(Integer isSupervisorInt) {
        this.mIsSupervisorInt = isSupervisorInt;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        this.mTimezone = timezone;
    }

    public Integer getSignatureId() {
        return mSignatureId;
    }

    public void setSignatureId(Integer signatureId) {
        this.mSignatureId = signatureId;
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

    public Boolean getUpdated() {
        return mUpdated;
    }

    public void setUpdated(Boolean updated) {
        this.mUpdated = updated;
    }

    public String getOrganization() {
        return mOrganization;
    }

    public void setOrganization(String organization) {
        this.mOrganization = organization;
    }

    public List<Setting> getSetting() {
        return mSetting;
    }

    public void setSetting(List<Setting> configuration) {
        this.mSetting = configuration;
    }

    public Auth getAuth() {
        return mAuth;
    }

    public void setAuth(Auth auth) {
        this.mAuth = auth;
    }

    public Boolean getIsSupervisor() {
        return mIsSupervisor;
    }

    public void setIsSupervisor(Boolean isSupervisor) {
        this.mIsSupervisor = isSupervisor;
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

    public String getRuleException() {
        return mRuleException;
    }

    public void setRuleException(String ruleException) {
        this.mRuleException = ruleException;
    }

    public Integer getApplyDST() {
        return mApplyDST;
    }

    public void setApplyDST(Integer applyDST) {
        this.mApplyDST = applyDST;
    }

    public Integer getUsCycle() {
        return mUsCycle;
    }

    public void setUsCycle(Integer usCycle) {
        this.mUsCycle = usCycle;
    }

    public Integer getCaCycle() {
        return mCaCycle;
    }

    public void setCaCycle(Integer caCycle) {
        this.mCaCycle = caCycle;
    }

    public Long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(Long lastModified) {
        this.mLastModified = lastModified;
    }

    public String getOrgAddr() {
        return mOrgAddr;
    }

    public void setOrgAddr(String orgAddr) {
        this.mOrgAddr = orgAddr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .append(mId, user.mId)
                .append(mOrgId, user.mOrgId)
                .append(mEmployeeId, user.mEmployeeId)
                .append(mPassword, user.mPassword)
                .append(mIsSupervisorInt, user.mIsSupervisorInt)
                .append(mTimezone, user.mTimezone)
                .append(mSignatureId, user.mSignatureId)
                .append(mAddress, user.mAddress)
                .append(mCity, user.mCity)
                .append(mState, user.mState)
                .append(mCountry, user.mCountry)
                .append(mSignature, user.mSignature)
                .append(mUpdated, user.mUpdated)
                .append(mOrganization, user.mOrganization)
                .append(mSetting, user.mSetting)
                .append(mAuth, user.mAuth)
                .append(mIsSupervisor, user.mIsSupervisor)
                .append(mFirstName, user.mFirstName)
                .append(mMidName, user.mMidName)
                .append(mLastName, user.mLastName)
                .append(mRuleException, user.mRuleException)
                .append(mApplyDST, user.mApplyDST)
                .append(mUsCycle, user.mUsCycle)
                .append(mCaCycle, user.mCaCycle)
                .append(mLastModified, user.mLastModified)
                .append(mOrgAddr, user.mOrgAddr)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mId)
                .append(mOrgId)
                .append(mEmployeeId)
                .append(mPassword)
                .append(mIsSupervisorInt)
                .append(mTimezone)
                .append(mSignatureId)
                .append(mAddress)
                .append(mCity)
                .append(mState)
                .append(mCountry)
                .append(mSignature)
                .append(mUpdated)
                .append(mOrganization)
                .append(mSetting)
                .append(mAuth)
                .append(mIsSupervisor)
                .append(mFirstName)
                .append(mMidName)
                .append(mLastName)
                .append(mRuleException)
                .append(mApplyDST)
                .append(mUsCycle)
                .append(mCaCycle)
                .append(mLastModified)
                .append(mOrgAddr)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("mId=").append(mId);
        sb.append(", mOrgId=").append(mOrgId);
        sb.append(", mEmployeeId='").append(mEmployeeId).append('\'');
        sb.append(", mPassword='").append(mPassword).append('\'');
        sb.append(", mIsSupervisorInt=").append(mIsSupervisorInt);
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mSignatureId=").append(mSignatureId);
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append(", mCity='").append(mCity).append('\'');
        sb.append(", mState='").append(mState).append('\'');
        sb.append(", mCountry='").append(mCountry).append('\'');
        sb.append(", mSignature='").append(mSignature).append('\'');
        sb.append(", mUpdated=").append(mUpdated);
        sb.append(", mOrganization='").append(mOrganization).append('\'');
        sb.append(", mSetting=").append(mSetting);
        sb.append(", mAuth=").append(mAuth);
        sb.append(", mIsSupervisor=").append(mIsSupervisor);
        sb.append(", mFirstName='").append(mFirstName).append('\'');
        sb.append(", mMidName='").append(mMidName).append('\'');
        sb.append(", mLastName='").append(mLastName).append('\'');
        sb.append(", mRuleException='").append(mRuleException).append('\'');
        sb.append(", mApplyDST=").append(mApplyDST);
        sb.append(", mUsCycle=").append(mUsCycle);
        sb.append(", mCaCycle=").append(mCaCycle);
        sb.append(", mLastModified=").append(mLastModified);
        sb.append(", mOrgAddr='").append(mOrgAddr).append('\'');
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
        dest.writeValue(this.mOrgId);
        dest.writeString(this.mEmployeeId);
        dest.writeString(this.mPassword);
        dest.writeValue(this.mIsSupervisorInt);
        dest.writeString(this.mTimezone);
        dest.writeValue(this.mSignatureId);
        dest.writeString(this.mAddress);
        dest.writeString(this.mCity);
        dest.writeString(this.mState);
        dest.writeString(this.mCountry);
        dest.writeString(this.mSignature);
        dest.writeValue(this.mUpdated);
        dest.writeString(this.mOrganization);
        dest.writeList(this.mSetting);
        dest.writeParcelable(this.mAuth, flags);
        dest.writeValue(this.mIsSupervisor);
        dest.writeString(this.mFirstName);
        dest.writeString(this.mMidName);
        dest.writeString(this.mLastName);
        dest.writeString(this.mRuleException);
        dest.writeValue(this.mApplyDST);
        dest.writeValue(this.mUsCycle);
        dest.writeValue(this.mCaCycle);
        dest.writeValue(this.mLastModified);
        dest.writeString(this.mOrgAddr);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.mId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mOrgId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mEmployeeId = in.readString();
        this.mPassword = in.readString();
        this.mIsSupervisorInt = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mTimezone = in.readString();
        this.mSignatureId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mAddress = in.readString();
        this.mCity = in.readString();
        this.mState = in.readString();
        this.mCountry = in.readString();
        this.mSignature = in.readString();
        this.mUpdated = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mOrganization = in.readString();
        this.mSetting = new ArrayList<>();
        in.readList(this.mSetting, Setting.class.getClassLoader());
        this.mAuth = in.readParcelable(Auth.class.getClassLoader());
        this.mIsSupervisor = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mFirstName = in.readString();
        this.mMidName = in.readString();
        this.mLastName = in.readString();
        this.mRuleException = in.readString();
        this.mApplyDST = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mUsCycle = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mCaCycle = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mLastModified = (Long) in.readValue(Integer.class.getClassLoader());
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
