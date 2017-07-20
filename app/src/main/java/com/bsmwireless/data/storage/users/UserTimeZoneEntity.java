package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.ColumnInfo;

public class UserTimeZoneEntity {
    @ColumnInfo(name = "timezone")
    private String mTimezone;

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }
}

