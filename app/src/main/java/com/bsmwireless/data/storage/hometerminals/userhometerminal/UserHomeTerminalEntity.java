package com.bsmwireless.data.storage.hometerminals.userhometerminal;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "user_home_terminal")
public final class UserHomeTerminalEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer mId;

    @ColumnInfo(name = "terminal_id")
    private Integer mHomeTerminalId;

    @ColumnInfo(name = "user_id")
    private Integer mUserId;

    public UserHomeTerminalEntity() {}

    @Ignore
    public UserHomeTerminalEntity(Integer mHomeTerminalId, Integer mUserId) {
        this.mHomeTerminalId = mHomeTerminalId;
        this.mUserId = mUserId;
    }

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

    public Integer getHomeTerminalId() {
        return mHomeTerminalId;
    }

    public void setHomeTerminalId(Integer homeTerminalId) {
        mHomeTerminalId = homeTerminalId;
    }
}
