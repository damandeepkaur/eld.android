package com.bsmwireless.common.utils;

import com.bsmwireless.models.ELDEvent;

import java.util.ArrayList;
import java.util.List;

public class DutyUtils {
    public static List<ELDEvent> filterEventsByTime(List<ELDEvent> events, long startTime, long endTime) {
        List<ELDEvent> result = new ArrayList<>();
        for (ELDEvent event : events) {
            long eventTime = event.getEventTime();
            if (eventTime >= startTime && eventTime < endTime) {
                result.add(event);
            }
        }
        return result;
    }

    public static ELDEvent getNearestEvent(List<ELDEvent> events, long startTime) {
        ELDEvent result = null;
        long delta = Long.MAX_VALUE;
        for (ELDEvent event : events) {
            long eventTime = event.getEventTime();
            if (startTime >= eventTime && delta > eventTime - startTime) {
                result = event;
            }
        }
        return result;
    }
}
