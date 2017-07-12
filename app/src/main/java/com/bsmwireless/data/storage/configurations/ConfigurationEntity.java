package com.bsmwireless.data.storage.configurations;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

@Entity(tableName = "configurations")
public class ConfigurationEntity {

    @ColumnInfo(name = "user_id")
    private Integer mUserId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "value")
    private String mValue;

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
