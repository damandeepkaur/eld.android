package com.bsmwireless.screens.logs.dagger;

import com.bsmwireless.data.storage.DutyTypeManager;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

public final class EventLogModel implements DutyTypeManager.DutyTypeCheckable {

    private ELDEvent mEvent;
    private Long mDuration;
    private String mDriverTimezone;
    private String mVehicleName;
    private DutyType mDutyType;

    public EventLogModel() {
    }

    public EventLogModel(ELDEvent event, String driverTimezone) {
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

    public void setEventTime(long time) {
        mEvent.setEventTime(time);
    }

    public Integer getEventCode() {
        //TODO: remove after server fix
        return mEvent.getEventCode() == null ? 0 : mEvent.getEventCode();
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

    public DutyType getDutyType() {
        return mDutyType;
    }

    public void setDutyType(DutyType dutyType) {
        mDutyType = dutyType;
    }

    @Override
    public Boolean isActive() {
        return mEvent.isActive();
    }

    @Override
    public Boolean isDutyEvent() {
        return mEvent.isDutyEvent();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventLogModel{");
        sb.append("mEvent=").append(mEvent);
        sb.append(", mDuration=").append(mDuration);
        sb.append(", mDriverTimezone='").append(mDriverTimezone).append('\'');
        sb.append(", mVehicleName='").append(mVehicleName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
