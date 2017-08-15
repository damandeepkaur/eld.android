package com.bsmwireless.screens.logs.dagger;

import com.bsmwireless.models.ELDEvent;

public class EventLogModel {

    private ELDEvent mEvent;
    private Long mDuration;
    private String mDriverTimezone;
    private String mVehicleName;

    public EventLogModel() {
    }

    public EventLogModel(ELDEvent event,  String driverTimezone) {
        mEvent = event;
        mDriverTimezone = driverTimezone;
    }

    public String getDriverTimezone() {
        return mDriverTimezone;
    }

    public void setDriverTimezone(String driverTimezone) {
        mDriverTimezone = driverTimezone;
    }

    public Long getDuration() {
        return mDuration;
    }

    public void setDuration(Long duration) {
        mDuration = duration;
    }

    public Integer getEventType() {
        return mEvent.getEventType();
    }

    public Long getEventTime() {
        return mEvent.getEventTime();
    }

    public Integer getEventCode() {
        return mEvent.getEventCode();
    }

    public ELDEvent getEvent() {
        return mEvent;
    }

    public void setEvent(ELDEvent event) {
        mEvent = event;
    }

    public String getLocation() {
        return mEvent.getLocation();
    }

    public String getVehicleName() {
        return mVehicleName;
    }

    public void setVehicleName(String vehicleName) {
        mVehicleName = vehicleName;
    }
}
