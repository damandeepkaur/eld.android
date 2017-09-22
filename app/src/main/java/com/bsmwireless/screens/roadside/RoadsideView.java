package com.bsmwireless.screens.roadside;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.List;

public interface RoadsideView {
    void showHeaders(List<String> data);
    void showEvents(List<String> data);
    void showGraph(List<EventLogModel> eventLogs, ELDEvent event);

    List<String> getEventsData(List<ELDEvent> events);
    List<String> getHeadersData(LogSheetHeader header, ELDEvent lastEvent, Vehicle vehicle);
}
