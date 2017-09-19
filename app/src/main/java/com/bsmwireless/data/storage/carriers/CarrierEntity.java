package com.bsmwireless.data.storage.carriers;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "carriers")
public final class CarrierEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer mId;
    @ColumnInfo(name = "dot")
    private String mDot;
    @ColumnInfo(name = "org_id")
    private Integer mOrgId;
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "address")
    private String mAddress;
    @ColumnInfo(name = "last_modified")
    private Long mLastModified;
    @ColumnInfo(name = "user_id")
    private Integer mUserId;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getDot() {
        return mDot;
    }

    public void setDot(String dot) {
        mDot = dot;
    }

    public Integer getOrgId() {
        return mOrgId;
    }

    public void setOrgId(Integer orgId) {
        mOrgId = orgId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public Long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(Long lastModified) {
        mLastModified = lastModified;
    }

    public Integer getUserId() {
        return mUserId;
    }

    public void setUserId(Integer userId) {
        mUserId = userId;
    }
}
