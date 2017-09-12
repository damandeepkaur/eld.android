package com.bsmwireless.screens.logs;

import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.List;

import app.bsmuniversal.com.R;

public interface LogsView {

    void setEventLogs(List<EventLogModel> logs);

    void setHOSTimes(HOSTimesModel hosTimes);

    void setLogHeader(LogHeaderModel logHeader);

    void setLogSheetHeaders(List<LogSheetHeader> logs);

    void goToAddEventScreen(CalendarItem day);

    void goToEditEventScreen(EventLogModel event);

    void goToEditLogHeaderScreen(LogHeaderModel logHeaderModel);

    void eventAdded();

    void eventUpdated();

    void dutyUpdated();

    void showError(RetrofitException exception);

    void showError(Error error);

    enum Error {
        ERROR_ADD_EVENT(R.string.add_event_error),
        ERROR_UPDATE_EVENT(R.string.update_event_error);

        private int mStringId;

        Error(int stringId) {
            mStringId = stringId;
        }

        public int getStringId() {
            return mStringId;
        }
    }

    void showTitleButton(LogsTitleView.Type expandedType);

    void hideTitleButton(LogsTitleView.Type expandedType);
}
