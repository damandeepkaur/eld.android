package com.bsmwireless.data.storage;

import com.bsm.sd.hos.code.HOSRule;
import com.bsm.sd.hos.code.RuleException;
import com.bsm.sd.hos.model.LogEvent;
import com.bsm.sd.hos.model.RuleSelectionHst;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CalculatorConverter {
    public static LogEvent eventToCalculatorEvent(ELDEvent event) {
        return event == null ? null : new LogEvent(new Timestamp(event.getEventTime()), event.getTzOffset(), event.getEventType(), event.getEventCode());
    }

    public static List<LogEvent> eventListToCalculatorEventList(List<ELDEvent> events) {
        List<LogEvent> calculatorEvents = new ArrayList<>();
        for (ELDEvent event : events) {
            if (event.isActive() && event.isDutyEvent()) calculatorEvents.add(eventToCalculatorEvent(event));
        }
        return calculatorEvents;
    }

    public static List<RuleSelectionHst> userToCalculatorRule(User user, long start) {
        RuleSelectionHst rule = new RuleSelectionHst();

        rule.setRule(HOSRule.UNKNOWN);

        //set duty cycle
        rule.setRule(getHOSRule(user.getDutyCycle()));

        //set start time
        rule.setSelecttime(new Timestamp(start));

        //set exceptions
        List<RuleException> exceptions = new ArrayList<>();
        for (String name : ListConverter.toStringList(user.getRuleException())) {
            exceptions.add(getRuleException(name));
        }
        rule.setException(exceptions);

        ArrayList<RuleSelectionHst> result = new ArrayList<>();
        result.add(rule);

        return result;
    }

    private static HOSRule getHOSRule(String name) {
        if (name != null) {
            for (HOSRule hos : HOSRule.values()) {
                if (hos.name().equals(name)) {
                    return hos;
                }
            }
        }

        return HOSRule.UNKNOWN;
    }

    private static RuleException getRuleException(String name) {
        if (name != null) {
            for (RuleException exception : RuleException.values()) {
                if (exception.name().equals(name)) {
                    return exception;
                }
            }
        }
        return RuleException.NONE;
    }
}
