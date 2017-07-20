package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.ColumnInfo;

public class UserLastVehiclesEntity {
    @ColumnInfo(name = "last_vehicle_ids")
    private String mIds;

    public String getIds() {
        return mIds;
    }

    public void setIds(String ids) {
        mIds = ids;
    }
}
