package com.bsmwireless.screens.logs;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;

import java.util.Date;
import java.util.List;

public interface  LogsView {

    void setELDEvents(List<ELDEvent> events);

    void setTripInfo(TripInfo tripInfo);

    void setLogSheetHeaders(List<LogSheetHeader> logs);

    void goToAddEventScreen();

    void goToEditEventScreen(ELDEvent event);

    void goToEditTripInfoScreen();

}
