package com.bsmwireless.widgets.alerts;

import app.bsmuniversal.com.R;

public enum OccupancyType {
    SINGLE(0, R.string.occupancy_0),
    CO_DRIVER_1(1, R.string.occupancy_1),
    CO_DRIVER_2(2, R.string.occupancy_2),
    CO_DRIVER_3(3, R.string.occupancy_3);

    private int mId;
    private int mName;

    OccupancyType(int type, int name) {
        mId = type;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public int getName() {
        return mName;
    }

    public static OccupancyType getTypeById(int id) {
        for (OccupancyType t: OccupancyType.values()) {
            if (t.mId == id) {
                return t;
            }
        }
        return OccupancyType.SINGLE;
    }
}
