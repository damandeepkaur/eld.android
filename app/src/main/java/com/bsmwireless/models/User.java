package com.bsmwireless.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    @SerializedName("id")
    private int mId;

    @SerializedName("firstname")
    private String mFirstName;

    @SerializedName("midname")
    private String mMidName;

    @SerializedName("lastname")
    private String mLastName;

    @SerializedName("uscycle")
    private int mUsCycle;

    @SerializedName("cacycle")
    private int mCaCycle;

    @SerializedName("cyclecountry")
    private int mCycleCountry;

    @SerializedName("ruleexception")
    private String mRuleException;

    @SerializedName("address")
    private String mAddress;

    @SerializedName("city")
    private String mCity;

    @SerializedName("state")
    private String mState;

    @SerializedName("country")
    private String mCountry;

    @SerializedName("isSupervisor")
    private boolean mIsSupervisor;

    @SerializedName("organization")
    private String mOrganization;

    @SerializedName("organizationAddress")
    private String mOrganizationAddress;

    @SerializedName("signature")
    private String mSignature;

    @SerializedName("timezone")
    private double mTimezone;

    @SerializedName("applydst")
    private int mApplyDst;

    @SerializedName("license")
    private String mLicense;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("lastmodified")
    private String mLastModified;

    @SerializedName("updated")
    private boolean mUpdated;

    @SerializedName("configuration")
    private List<Setting> mConfiguration;

    @SerializedName("token")
    private String mToken;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mFirstName);
        dest.writeString(this.mMidName);
        dest.writeString(this.mLastName);
        dest.writeInt(this.mUsCycle);
        dest.writeInt(this.mCaCycle);
        dest.writeInt(this.mCycleCountry);
        dest.writeString(this.mRuleException);
        dest.writeString(this.mAddress);
        dest.writeString(this.mCity);
        dest.writeString(this.mState);
        dest.writeString(this.mCountry);
        dest.writeByte(this.mIsSupervisor ? (byte) 1 : (byte) 0);
        dest.writeString(this.mOrganization);
        dest.writeString(this.mOrganizationAddress);
        dest.writeString(this.mSignature);
        dest.writeDouble(this.mTimezone);
        dest.writeInt(this.mApplyDst);
        dest.writeString(this.mLicense);
        dest.writeString(this.mEmail);
        dest.writeString(this.mLastModified);
        dest.writeByte(this.mUpdated ? (byte) 1 : (byte) 0);
        dest.writeList(this.mConfiguration);
        dest.writeString(this.mToken);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.mId = in.readInt();
        this.mFirstName = in.readString();
        this.mMidName = in.readString();
        this.mLastName = in.readString();
        this.mUsCycle = in.readInt();
        this.mCaCycle = in.readInt();
        this.mCycleCountry = in.readInt();
        this.mRuleException = in.readString();
        this.mAddress = in.readString();
        this.mCity = in.readString();
        this.mState = in.readString();
        this.mCountry = in.readString();
        this.mIsSupervisor = in.readByte() != 0;
        this.mOrganization = in.readString();
        this.mOrganizationAddress = in.readString();
        this.mSignature = in.readString();
        this.mTimezone = in.readDouble();
        this.mApplyDst = in.readInt();
        this.mLicense = in.readString();
        this.mEmail = in.readString();
        this.mLastModified = in.readString();
        this.mUpdated = in.readByte() != 0;
        this.mConfiguration = new ArrayList<>();
        in.readList(this.mConfiguration, Setting.class.getClassLoader());
        this.mToken = in.readString();
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
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

    public int getUsCycle() {
        return mUsCycle;
    }

    public void setUsCycle(int usCycle) {
        mUsCycle = usCycle;
    }

    public int getCaCycle() {
        return mCaCycle;
    }

    public void setCaCycle(int caCycle) {
        mCaCycle = caCycle;
    }

    public int getCycleCountry() {
        return mCycleCountry;
    }

    public void setCycleCountry(int cycleCountry) {
        mCycleCountry = cycleCountry;
    }

    public String getRuleException() {
        return mRuleException;
    }

    public void setRuleException(String ruleException) {
        mRuleException = ruleException;
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

    public boolean isSupervisor() {
        return mIsSupervisor;
    }

    public void setSupervisor(boolean supervisor) {
        mIsSupervisor = supervisor;
    }

    public String getOrganization() {
        return mOrganization;
    }

    public void setOrganization(String organization) {
        mOrganization = organization;
    }

    public String getOrganizationAddress() {
        return mOrganizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress) {
        mOrganizationAddress = organizationAddress;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    public double getTimezone() {
        return mTimezone;
    }

    public void setTimezone(double timezone) {
        mTimezone = timezone;
    }

    public int getApplyDst() {
        return mApplyDst;
    }

    public void setApplyDst(int applyDst) {
        mApplyDst = applyDst;
    }

    public String getLicense() {
        return mLicense;
    }

    public void setLicense(String license) {
        mLicense = license;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getLastModified() {
        return mLastModified;
    }

    public void setLastModified(String lastModified) {
        mLastModified = lastModified;
    }

    public boolean isUpdated() {
        return mUpdated;
    }

    public void setUpdated(boolean updated) {
        mUpdated = updated;
    }

    public List<Setting> getConfiguration() {
        return mConfiguration;
    }

    public void setConfiguration(List<Setting> configuration) {
        mConfiguration = configuration;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (mId != user.mId) return false;
        if (mUsCycle != user.mUsCycle) return false;
        if (mCaCycle != user.mCaCycle) return false;
        if (mCycleCountry != user.mCycleCountry) return false;
        if (mIsSupervisor != user.mIsSupervisor) return false;
        if (Double.compare(user.mTimezone, mTimezone) != 0) return false;
        if (mApplyDst != user.mApplyDst) return false;
        if (mUpdated != user.mUpdated) return false;
        if (mFirstName != null ? !mFirstName.equals(user.mFirstName) : user.mFirstName != null)
            return false;
        if (mMidName != null ? !mMidName.equals(user.mMidName) : user.mMidName != null)
            return false;
        if (mLastName != null ? !mLastName.equals(user.mLastName) : user.mLastName != null)
            return false;
        if (mRuleException != null ? !mRuleException.equals(user.mRuleException) : user.mRuleException != null)
            return false;
        if (mAddress != null ? !mAddress.equals(user.mAddress) : user.mAddress != null)
            return false;
        if (mCity != null ? !mCity.equals(user.mCity) : user.mCity != null) return false;
        if (mState != null ? !mState.equals(user.mState) : user.mState != null) return false;
        if (mCountry != null ? !mCountry.equals(user.mCountry) : user.mCountry != null)
            return false;
        if (mOrganization != null ? !mOrganization.equals(user.mOrganization) : user.mOrganization != null)
            return false;
        if (mOrganizationAddress != null ? !mOrganizationAddress.equals(user.mOrganizationAddress) : user.mOrganizationAddress != null)
            return false;
        if (mSignature != null ? !mSignature.equals(user.mSignature) : user.mSignature != null)
            return false;
        if (mLicense != null ? !mLicense.equals(user.mLicense) : user.mLicense != null)
            return false;
        if (mEmail != null ? !mEmail.equals(user.mEmail) : user.mEmail != null) return false;
        if (mLastModified != null ? !mLastModified.equals(user.mLastModified) : user.mLastModified != null)
            return false;
        if (mConfiguration != null ? !mConfiguration.equals(user.mConfiguration) : user.mConfiguration != null)
            return false;
        return mToken != null ? mToken.equals(user.mToken) : user.mToken == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mId;
        result = 31 * result + (mFirstName != null ? mFirstName.hashCode() : 0);
        result = 31 * result + (mMidName != null ? mMidName.hashCode() : 0);
        result = 31 * result + (mLastName != null ? mLastName.hashCode() : 0);
        result = 31 * result + mUsCycle;
        result = 31 * result + mCaCycle;
        result = 31 * result + mCycleCountry;
        result = 31 * result + (mRuleException != null ? mRuleException.hashCode() : 0);
        result = 31 * result + (mAddress != null ? mAddress.hashCode() : 0);
        result = 31 * result + (mCity != null ? mCity.hashCode() : 0);
        result = 31 * result + (mState != null ? mState.hashCode() : 0);
        result = 31 * result + (mCountry != null ? mCountry.hashCode() : 0);
        result = 31 * result + (mIsSupervisor ? 1 : 0);
        result = 31 * result + (mOrganization != null ? mOrganization.hashCode() : 0);
        result = 31 * result + (mOrganizationAddress != null ? mOrganizationAddress.hashCode() : 0);
        result = 31 * result + (mSignature != null ? mSignature.hashCode() : 0);
        temp = Double.doubleToLongBits(mTimezone);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + mApplyDst;
        result = 31 * result + (mLicense != null ? mLicense.hashCode() : 0);
        result = 31 * result + (mEmail != null ? mEmail.hashCode() : 0);
        result = 31 * result + (mLastModified != null ? mLastModified.hashCode() : 0);
        result = 31 * result + (mUpdated ? 1 : 0);
        result = 31 * result + (mConfiguration != null ? mConfiguration.hashCode() : 0);
        result = 31 * result + (mToken != null ? mToken.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("mId=").append(mId);
        sb.append(", mFirstName='").append(mFirstName).append('\'');
        sb.append(", mMidName='").append(mMidName).append('\'');
        sb.append(", mLastName='").append(mLastName).append('\'');
        sb.append(", mUsCycle=").append(mUsCycle);
        sb.append(", mCaCycle=").append(mCaCycle);
        sb.append(", mCycleCountry=").append(mCycleCountry);
        sb.append(", mRuleException='").append(mRuleException).append('\'');
        sb.append(", mAddress='").append(mAddress).append('\'');
        sb.append(", mCity='").append(mCity).append('\'');
        sb.append(", mState='").append(mState).append('\'');
        sb.append(", mCountry='").append(mCountry).append('\'');
        sb.append(", mIsSupervisor=").append(mIsSupervisor);
        sb.append(", mOrganization='").append(mOrganization).append('\'');
        sb.append(", mOrganizationAddress='").append(mOrganizationAddress).append('\'');
        sb.append(", mSignature='").append(mSignature).append('\'');
        sb.append(", mTimezone=").append(mTimezone);
        sb.append(", mApplyDst=").append(mApplyDst);
        sb.append(", mLicense='").append(mLicense).append('\'');
        sb.append(", mEmail='").append(mEmail).append('\'');
        sb.append(", mLastModified='").append(mLastModified).append('\'');
        sb.append(", mUpdated=").append(mUpdated);
        sb.append(", mConfiguration=").append(mConfiguration);
        sb.append(", mToken='").append(mToken).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
