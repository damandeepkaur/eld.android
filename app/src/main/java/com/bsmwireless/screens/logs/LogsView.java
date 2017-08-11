package com.bsmwireless.screens.logs;

import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.List;

public interface  LogsView {

    void setEventLogs(List<EventLogModel> logs);

    void setTripInfo(TripInfoModel tripInfo);

    void setLogSheetHeaders(List<LogSheetHeader> logs);

    void goToAddEventScreen();

    void goToEditEventScreen(ELDEvent event);

    void goToEditTripInfoScreen();

}
