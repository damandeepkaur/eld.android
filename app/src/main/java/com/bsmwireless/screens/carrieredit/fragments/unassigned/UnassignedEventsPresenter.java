package com.bsmwireless.screens.carrieredit.fragments.unassigned;

import com.bsmwireless.screens.logs.dagger.EventLogModel;

public interface UnassignedEventsPresenter {

    void setView(UnassignedEventsView view);

    void fetchEldEvents();

    void acceptEvent(EventLogModel event, int position);

    void rejectEvent(EventLogModel event, int position);

    void dispose();
}
