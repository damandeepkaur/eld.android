package com.bsmwireless.widgets.alerts;

import com.bsmwireless.models.ELDEvent;

import app.bsmuniversal.com.R;

public enum NonDutyType implements Type {
    LOGIN(ELDEvent.EventType.LOGIN_LOGOUT.getValue(),
            ELDEvent.LoginLogoutCode.LOGIN.getValue(),
            R.string.event_type_login),
    LOGOUT(ELDEvent.EventType.LOGIN_LOGOUT.getValue(),
            ELDEvent.LoginLogoutCode.LOGOUT.getValue(),
            R.string.event_type_logout),

    POWER_UP(ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue(),
            ELDEvent.EnginePowerCode.POWER_UP.getValue(),
            R.string.event_type_power_up),
    POWER_UP_REDUCE_DECISION(ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue(),
            ELDEvent.EnginePowerCode.POWER_UP_REDUCE_DECISION.getValue(),
            R.string.event_type_power_up),
    SHUT_DOWN(ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue(),
            ELDEvent.EnginePowerCode.SHUT_DOWN.getValue(),
            R.string.event_type_shut_down),
    SHUT_DOWN_REDUCE_DECISION(ELDEvent.EventType.ENGINE_POWER_CHANGING.getValue(),
            ELDEvent.EnginePowerCode.SHUT_DOWN_REDUCE_DECISION.getValue(),
            R.string.event_type_shut_down),

    MALFUNCTION_LOGGED(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
            ELDEvent.MalfunctionCode.MALFUNCTION_LOGGED.getCode(),
            R.string.event_type_malfunction_logged),
    MALFUNCTION_CLEARED(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
            ELDEvent.MalfunctionCode.MALFUNCTION_CLEARED.getCode(),
            R.string.event_type_malfunction_cleared),
    DIAGNOSTIC_LOGGED(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
            ELDEvent.MalfunctionCode.DIAGNOSTIC_LOGGED.getCode(),
            R.string.event_type_diagnostic_logged),
    DIAGNOSTIC_CLEARED(ELDEvent.EventType.DATA_DIAGNOSTIC.getValue(),
            ELDEvent.MalfunctionCode.DIAGNOSTIC_CLEARED.getCode(),
            R.string.event_type_diagnostic_cleared),

    UNKNOWN(0, 0, R.string.event_type_unknown_type);

    private int mType;
    private int mCode;
    private int mName;

    NonDutyType(int type, int code, int name) {
        mType = type;
        mCode = code;
        mName = name;
    }

    public static NonDutyType getNonDutyTypeByCode(int type, int code) {
        for (NonDutyType t : NonDutyType.values()) {
            if (t.mCode == code && t.mType == type) {
                return t;
            }
        }
        return UNKNOWN;
    }

    @Override
    public int getCode() {
        return mCode;
    }

    @Override
    public int getName() {
        return mName;
    }

    @Override
    public int getTitle() {
        return mName;
    }

    @Override
    public int getColor() {
        return DutyType.OFF_DUTY.getColor();
    }

    @Override
    public int getIcon() {
        return DutyType.OFF_DUTY.getIcon();
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public int getOriginalCode() {
        return DutyType.OFF_DUTY.getOriginalCode();
    }

    @Override
    public boolean isSame(int type, int code) {
        return mType == type && mCode == code;
    }

}
