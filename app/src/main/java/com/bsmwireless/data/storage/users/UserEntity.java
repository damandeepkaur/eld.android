package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer mId;
    @ColumnInfo(name = "timezone")
    private String mTimezone;
    @ColumnInfo(name = "email")
    private String mEmail;
    @ColumnInfo(name = "address")
    private String mAddress;
    @ColumnInfo(name = "city")
    private String mCity;
    @ColumnInfo(name = "state")
    private String mState;
    @ColumnInfo(name = "country")
    private String mCountry;
    @ColumnInfo(name = "license")
    private String mLicense;
    @ColumnInfo(name = "signature")
    private String mSignature;
    @ColumnInfo(name = "exempt")
    private Boolean mExempt;
    @ColumnInfo(name = "updated")
    private Boolean mUpdated;
    @ColumnInfo(name = "organization")
    private String mOrganization;
    @ColumnInfo(name = "sync_time")
    private Long mSyncTime;
    @ColumnInfo(name = "first_name")
    private String mFirstName;
    @ColumnInfo(name = "mid_name")
    private String mMidName;
    @ColumnInfo(name = "last_name")
    private String mLastName;
    @ColumnInfo(name = "rule_exception")
    private String mRuleException;
    @ColumnInfo(name = "apply_dst")
    private Integer mApplyDST;
    @ColumnInfo(name = "us_cycle")
    private Integer mUsCycle;
    @ColumnInfo(name = "ca_cycle")
    private Integer mCaCycle;
    @ColumnInfo(name = "cycle_country")
    private Integer mCycleCountry;
    @ColumnInfo(name = "org_addr")
    private String mOrgAddr;
    @ColumnInfo(name = "lastmodified")
    private Long mLastModified;
    @ColumnInfo(name = "last_vehicle_ids")
    private String mLastVehicleIds;

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

    public Long getSyncTime() {
        return mSyncTime;
    }

    public void setSyncTime(Long syncTime) {
        mSyncTime = syncTime;
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

    public Long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(Long lastModified) {
        mLastModified = lastModified;
    }

    public String getLastVehicleIds() {
        return mLastVehicleIds;
    }

    public void setLastVehicleIds(String lastVehicleIds) {
        mLastVehicleIds = lastVehicleIds;
    }
}
