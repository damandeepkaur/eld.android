package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "users")
public final class UserEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer mId;
    @ColumnInfo(name = "timezone")
    private String mTimezone;
    @ColumnInfo(name = "email")
    private String mEmail;
    @ColumnInfo(name = "license")
    private String mLicense;
    @ColumnInfo(name = "signature")
    private String mSignature;
    @ColumnInfo(name = "exempt")
    private Boolean mExempt;
    @ColumnInfo(name = "updated")
    private Boolean mUpdated;
    @ColumnInfo(name = "dot")
    private String mDot;
    @ColumnInfo(name = "sync_time")
    private Long mSyncTime;
    @ColumnInfo(name = "first_name")
    private String mFirstName;
    @ColumnInfo(name = "mid_name")
    private String mMidName;
    @ColumnInfo(name = "last_name")
    private String mLastName;
    @ColumnInfo(name = "duty_cycle")
    private String mDutyCycle;
    @ColumnInfo(name = "rule_exception")
    private String mRuleException;
    @ColumnInfo(name = "home_terminal_id")
    private Integer mHomeTermId;
    @ColumnInfo(name = "uom")
    private Integer mUom;
    @ColumnInfo(name = "last_vehicle_ids")
    private String mLastVehicleIds;
    @ColumnInfo(name = "co_drivers_ids")
    private String mCoDriversIds;
    @ColumnInfo(name = "account_name")
    private String mAccountName;
    @ColumnInfo(name = "offline_change")
    public Boolean mOfflineChange;

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

    public String getLastVehicleIds() {
        return mLastVehicleIds;
    }

    public void setLastVehicleIds(String lastVehicleIds) {
        mLastVehicleIds = lastVehicleIds;
    }

    public String getDot() {
        return mDot;
    }

    public void setDot(String dot) {
        mDot = dot;
    }

    public String getDutyCycle() {
        return mDutyCycle;
    }

    public void setDutyCycle(String dutyCycle) {
        mDutyCycle = dutyCycle;
    }

    public Integer getHomeTermId() {
        return mHomeTermId;
    }

    public void setHomeTermId(Integer homeTermId) {
        mHomeTermId = homeTermId;
    }

    public Integer getUom() {
        return mUom;
    }

    public void setUom(Integer uom) {
        mUom = uom;
    }

    public String getCoDriversIds() {
        return mCoDriversIds;
    }

    public void setCoDriversIds(String coDriversIds) {
        mCoDriversIds = coDriversIds;
    }

    public String getAccountName() {
        return mAccountName;
    }

    public void setAccountName(String accountName) {
        mAccountName = accountName;
    }

    public Boolean isOfflineChange() {
        if (mOfflineChange == null) {
            mOfflineChange = false;
        }
        return mOfflineChange;
    }

    public UserEntity setOfflineChange(boolean isOfflineChange) {
        mOfflineChange = isOfflineChange;
        return this;
    }
}
