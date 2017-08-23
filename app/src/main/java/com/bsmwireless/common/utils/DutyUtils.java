package com.bsmwireless.common.utils;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.ArrayList;
import java.util.List;

public class DutyUtils {
    public static List<EventLogModel> filterEventModelsByTypeAndStatus(List<EventLogModel> events, ELDEvent.EventType eventType, ELDEvent.StatusCode statusCode) {
        List<EventLogModel> result = new ArrayList<>();
        for (EventLogModel event : events) {
            if (event.getEventType().equals(eventType.getValue()) && (statusCode == null || event.getEvent().getStatus().equals(statusCode.getValue()))) {
                result.add(event);
            }
        }
        return result;
    }

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
}
