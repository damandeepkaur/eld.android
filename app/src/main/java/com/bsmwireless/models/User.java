package com.bsmwireless.models;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    @SerializedName("timezone")
    @Expose
    private String mTimezone;
    @SerializedName("email")
    @Expose
    private String mEmail;
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
    @SerializedName("dot")
    @Expose
    private String mDot;
    @SerializedName("syncTime")
    @Expose
    private Long mSyncTime;
    @SerializedName("configuration")
    @Expose
    private List<SyncConfiguration> mConfigurations = null;
    @SerializedName("home")
    @Expose
    private List<HomeTerminal> mHomeTerminals = null;
    @SerializedName("carrier")
    @Expose
    private List<Carrier> mCarriers = null;
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
    @SerializedName("dutyCycle")
    @Expose
    private String mDutyCycle;
    @SerializedName("ruleException")
    @Expose
    private String mRuleException;
    @SerializedName("homeTermId")
    @Expose
    private Integer mHomeTermId;
    @SerializedName("uom")
    @Expose
    private Integer mUom;
    public final static Parcelable.Creator<User> CREATOR = new Creator<User>() {

        @SuppressWarnings({
            "unchecked"
        })
        public User createFromParcel(Parcel in) {
            User instance = new User();
            instance.mId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mTimezone = ((String) in.readValue((String.class.getClassLoader())));
            instance.mEmail = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLicense = ((String) in.readValue((String.class.getClassLoader())));
            instance.mSignature = ((String) in.readValue((String.class.getClassLoader())));
            instance.mExempt = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mUpdated = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.mDot = ((String) in.readValue((String.class.getClassLoader())));
            instance.mSyncTime = ((Long) in.readValue((Long.class.getClassLoader())));
            in.readList(instance.mConfigurations, (SyncConfiguration.class.getClassLoader()));
            in.readList(instance.mHomeTerminals, (HomeTerminal.class.getClassLoader()));
            in.readList(instance.mCarriers, (com.bsmwireless.models.Carrier.class.getClassLoader()));
            instance.mAuth = ((Auth) in.readValue((Auth.class.getClassLoader())));
            instance.mFirstName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mMidName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mLastName = ((String) in.readValue((String.class.getClassLoader())));
            instance.mDutyCycle = ((String) in.readValue((String.class.getClassLoader())));
            instance.mRuleException = ((String) in.readValue((String.class.getClassLoader())));
            instance.mHomeTermId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.mUom = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public User[] newArray(int size) {
            return (new User[size]);
        }

    };

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        this.mTimezone = timezone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        this.mLicense = license;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        this.mSignature = signature;
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
        this.mUpdated = updated;
    }

    public String getDot() {
        return mDot;
    }

    public void setDot(String dot) {
        this.mDot = dot;
    }

    public Long getSyncTime() {
        return mSyncTime;
    }

    public void setSyncTime(Long syncTime) {
        this.mSyncTime = syncTime;
    }

    public List<SyncConfiguration> getConfigurations() {
        return mConfigurations;
    }

    public void setConfigurations(List<SyncConfiguration> configurations) {
        this.mConfigurations = configurations;
    }

    public List<HomeTerminal> getHomeTerminals() {
        return mHomeTerminals;
    }

    public void setHomeTerminals(List<HomeTerminal> homeTerminals) {
        this.mHomeTerminals = homeTerminals;
    }

    public List<Carrier> getCarriers() {
        return mCarriers;
    }

    public void setCarriers(List<Carrier> carriers) {
        this.mCarriers = carriers;
    }

    public Auth getAuth() {
        return mAuth;
    }

    public void setAuth(Auth auth) {
        this.mAuth = auth;
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

    public String getDutyCycle() {
        return mDutyCycle;
    }

    public void setDutyCycle(String dutyCycle) {
        this.mDutyCycle = dutyCycle;
    }

    public String getRuleException() {
        return mRuleException;
    }

    public void setRuleException(String ruleException) {
        this.mRuleException = ruleException;
    }

    public Integer getHomeTermId() {
        return mHomeTermId;
    }

    public void setHomeTermId(Integer homeTermId) {
        this.mHomeTermId = homeTermId;
    }

    public Integer getUom() {
        return mUom;
    }

    public void setUom(Integer uom) {
        this.mUom = uom;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("mId=").append(mId);
        sb.append(", mTimezone='").append(mTimezone).append('\'');
        sb.append(", mEmail='").append(mEmail).append('\'');
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mSignature='").append(mSignature).append('\'');
        sb.append(", mExempt=").append(mExempt);
        sb.append(", mUpdated=").append(mUpdated);
        sb.append(", mDot='").append(mDot).append('\'');
        sb.append(", mSyncTime=").append(mSyncTime);
        sb.append(", mConfigurations=").append(mConfigurations);
        sb.append(", mHomeTerminals=").append(mHomeTerminals);
        sb.append(", mCarriers=").append(mCarriers);
        sb.append(", mAuth=").append(mAuth);
        sb.append(", mFirstName='").append(mFirstName).append('\'');
        sb.append(", mMidName='").append(mMidName).append('\'');
        sb.append(", mLastName='").append(mLastName).append('\'');
        sb.append(", mDutyCycle='").append(mDutyCycle).append('\'');
        sb.append(", mRuleException='").append(mRuleException).append('\'');
        sb.append(", mHomeTermId=").append(mHomeTermId);
        sb.append(", mUom=").append(mUom);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mId).append(mTimezone).append(mEmail).append(mLicense)
                                    .append(mSignature).append(mExempt).append(mUpdated)
                                    .append(mDot).append(mSyncTime).append(mConfigurations)
                                    .append(mHomeTerminals).append(mCarriers).append(mAuth)
                                    .append(mFirstName).append(mMidName).append(mLastName)
                                    .append(mDutyCycle).append(mRuleException).append(mHomeTermId)
                                    .append(mUom).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof User)) {
            return false;
        }
        User rhs = ((User) other);
        return new EqualsBuilder().append(mId, rhs.mId).append(mTimezone, rhs.mTimezone)
                                  .append(mEmail, rhs.mEmail).append(mLicense, rhs.mLicense)
                                  .append(mSignature, rhs.mSignature).append(mExempt, rhs.mExempt)
                                  .append(mUpdated, rhs.mUpdated).append(mDot, rhs.mDot)
                                  .append(mSyncTime, rhs.mSyncTime)
                                  .append(mConfigurations, rhs.mConfigurations)
                                  .append(mHomeTerminals, rhs.mHomeTerminals).append(mCarriers, rhs.mCarriers)
                                  .append(mAuth, rhs.mAuth).append(mFirstName, rhs.mFirstName)
                                  .append(mMidName, rhs.mMidName).append(mLastName, rhs.mLastName)
                                  .append(mDutyCycle, rhs.mDutyCycle)
                                  .append(mRuleException, rhs.mRuleException)
                                  .append(mHomeTermId, rhs.mHomeTermId).append(mUom, rhs.mUom)
                                  .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mId);
        dest.writeValue(mTimezone);
        dest.writeValue(mEmail);
        dest.writeValue(mLicense);
        dest.writeValue(mSignature);
        dest.writeValue(mExempt);
        dest.writeValue(mUpdated);
        dest.writeValue(mDot);
        dest.writeValue(mSyncTime);
        dest.writeList(mConfigurations);
        dest.writeList(mHomeTerminals);
        dest.writeList(mCarriers);
        dest.writeValue(mAuth);
        dest.writeValue(mFirstName);
        dest.writeValue(mMidName);
        dest.writeValue(mLastName);
        dest.writeValue(mDutyCycle);
        dest.writeValue(mRuleException);
        dest.writeValue(mHomeTermId);
        dest.writeValue(mUom);
    }

    public int describeContents() {
        return  0;
    }

}
