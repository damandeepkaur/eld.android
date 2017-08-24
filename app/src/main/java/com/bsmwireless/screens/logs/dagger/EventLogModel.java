package com.bsmwireless.screens.logs.dagger;

import com.bsmwireless.data.storage.DutyManager;
import com.bsmwireless.models.ELDEvent;

public class EventLogModel implements DutyManager.DutyCheckable {

    private ELDEvent mEvent;
    private Long mDuration;
    private String mDriverTimezone;
    private String mVehicleName;
    //only for indication off events (type 3 and code 0)
    private int mOnIndicationCode;

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

    public int getOnIndicationCode() {
        return mOnIndicationCode;
    }

    public void setOnIndicationCode(int onIndicationCode) {
        mOnIndicationCode = onIndicationCode;
    }

    public boolean isActive() {
        return mEvent.getStatus().equals(ELDEvent.StatusCode.ACTIVE.getValue());
    }

    public boolean isDutyEvent() {
        return mEvent.getEventType().equals(ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()) ||
                mEvent.getEventType().equals(ELDEvent.EventType.CHANGE_IN_DRIVER_INDICATION.getValue());
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
