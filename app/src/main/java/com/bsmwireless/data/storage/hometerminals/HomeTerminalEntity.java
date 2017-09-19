package com.bsmwireless.data.storage.hometerminals;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "home_terminals")
public final class HomeTerminalEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer mId;
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "timezone")
    private String mTimezone;
    @ColumnInfo(name = "address")
    private String mAddress;
    @ColumnInfo(name = "user_id")
    private Integer mUserId;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public Integer getUserId() {
        return mUserId;
    }

    public void setUserId(Integer userId) {
        mUserId = userId;
    }
}
