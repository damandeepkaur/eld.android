package com.bsmwireless.widgets.alerts;

import app.bsmuniversal.com.R;

public enum ELDType {
    GREEN(1, R.string.eld_green),
    ORANGE(2, R.string.eld_orange),
    RED(3, R.string.eld_red);

    private int mId;
    private int mName;

    ELDType(int id, int name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public int getName() {
        return mName;
    }

    public static ELDType getTypeById(int id) {
        for (ELDType t: ELDType.values()) {
            if (t.mId == id) {
                return t;
            }
        }
        return ELDType.GREEN;
    }
}
