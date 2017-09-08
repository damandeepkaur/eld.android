package com.bsmwireless.models;

import app.bsmuniversal.com.R;

public enum Malfunction {
    UNKNOWN("", false, R.string.diagnostic_other),
    POWER_DATA_DIAGNOSTIC("1", false, R.string.diagnostic_power_data),
    ENGINE_SYNCHRONIZATION("2", false, R.string.diagnostic_engine_synchronization),
    MISSING_REQUIRED_DATA_ELEMENTS("3", false, R.string.diagnostic_missing_req_data),
    DATA_TRANSFER("4", false, R.string.diagnostic_data_transfer),
    UNIDENTIFIED_DRIVING("5", false, R.string.diagnostic_unidentified_driving),
    OTHER_DIAGNOSTIC("6", false, R.string.diagnostic_other),

    POWER_COMPLIANCE("P", true, R.string.malfunction_power),
    ENGINE_SYNCHRONIZATION_COMPLIANCE("E", true, R.string.malfunction_engine_synchronization),
    TIMING_COMPLIANCE("T", true, R.string.malfunction_timing),
    POSITIONING_COMPLIANCE("L", true, R.string.malfunction_positioning),
    DATA_RECORDING_COMPLIANCE("R", true, R.string.malfunction_data_recording),
    DATA_TRANSFER_COMPLIANCE("S", true, R.string.malfunction_data_transfer),
    OTHER_COMPLIANCE("0", true, R.string.malfunction_other);

    private final String mCode;
    private final boolean mMalfunction;
    private final int mStringRes;

    Malfunction(String code, boolean malfunction, int stringRes) {
        this.mCode = code;
        this.mMalfunction = malfunction;
        this.mStringRes = stringRes;
    }

    public String getCode() {
        return mCode;
    }

    public boolean isMalfunction() {
        return mMalfunction;
    }

    public int getStringRes() {
        return mStringRes;
    }

    public static Malfunction createByCode(String code) {
        for (Malfunction malfunction : values()) {
            if (malfunction.mCode.equals(code)) return malfunction;
        }
        return UNKNOWN;
    }
}
