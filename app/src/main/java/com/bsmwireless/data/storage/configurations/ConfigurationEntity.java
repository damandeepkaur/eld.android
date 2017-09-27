package com.bsmwireless.data.storage.configurations;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "configurations")
public final class ConfigurationEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer mId;
    @ColumnInfo(name = "user_id")
    private Integer mUserId;
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "value")
    private String mValue;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getUserId() {
        return mUserId;
    }

    public void setUserId(Integer userId) {
        mUserId = userId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}
