package com.bsmwireless.common.utils;

import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.NonDutyType;
import com.bsmwireless.widgets.alerts.Type;

public class DutyUtils {
    public  static Type getTypeByCode(int type, int code) {
        Type eventType = NonDutyType.getNonDutyTypeByCode(type, code);
        return eventType.equals(NonDutyType.UNKNOWN) ? DutyType
                .getDutyTypeByCode(type, code) : eventType;
    }
}
