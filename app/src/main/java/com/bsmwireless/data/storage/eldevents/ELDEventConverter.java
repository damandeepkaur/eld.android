package com.bsmwireless.data.storage.eldevents;

import com.bsmwireless.models.ELDEvent;

import java.util.ArrayList;
import java.util.List;

public class ELDEventConverter {
    public static ELDEvent toModel(ELDEventEntity entity) {
        ELDEvent event = null;

        if (entity != null) {
            event = new ELDEvent();
            event.setId(entity.getId());
            event.setEventType(entity.getEventType());
            event.setEventCode(entity.getEventCode());
            event.setStatus(entity.getStatus());
            event.setOrigin(entity.getOrigin());
            event.setEventTime(entity.getEventTime());
            event.setOdometer(entity.getOdometer());
            event.setEngineHours(entity.getEngineHours());
            event.setLat(entity.getLat());
            event.setLng(entity.getLng());
            event.setDistance(entity.getDistance());
            event.setComment(entity.getComment());
            event.setLocation(entity.getLocation());
            event.setCheckSum(entity.getCheckSum());
            event.setShippingId(entity.getShippingId());
            event.setCoDriverId(entity.getCoDriverId());
            event.setBoxId(entity.getBoxId());
            event.setVehicleId(entity.getVehicleId());
            event.setTzOffset(entity.getTzOffset());
            event.setTimezone(entity.getTimezone());
            event.setMobileTime(entity.getMobileTime());
            event.setDriverId(entity.getDriverId());
            event.setMalfunction(entity.getMalfunction());
            event.setDiagnostic(entity.getDiagnostic());
        }

        return event;
    }

    public static ELDEventEntity toEntity(ELDEvent event) {
        ELDEventEntity entity = null;

        if (event != null) {
            entity = new ELDEventEntity();
            entity.setId(event.getId());
            entity.setEventType(event.getEventType());
            entity.setEventCode(event.getEventCode());
            entity.setStatus(event.getStatus());
            entity.setOrigin(event.getOrigin());
            entity.setEventTime(event.getEventTime());
            entity.setOdometer(event.getOdometer());
            entity.setEngineHours(event.getEngineHours());
            entity.setLat(event.getLat());
            entity.setLng(event.getLng());
            entity.setDistance(event.getDistance());
            entity.setComment(event.getComment());
            entity.setLocation(event.getLocation());
            entity.setCheckSum(event.getCheckSum());
            entity.setShippingId(event.getShippingId());
            entity.setCoDriverId(event.getCoDriverId());
            entity.setBoxId(event.getBoxId());
            entity.setVehicleId(event.getVehicleId());
            entity.setTzOffset(event.getTzOffset());
            entity.setTimezone(event.getTimezone());
            entity.setMobileTime(event.getMobileTime());
            entity.setDriverId(event.getDriverId());
            entity.setMalfunction(event.getMalfunction());
            entity.setDiagnostic(event.getDiagnostic());
        }
        return entity;
    }

    public static List<ELDEvent> toModelList(List<ELDEventEntity> entities) {
        List<ELDEvent> events = new ArrayList<>();
        if (entities != null && !entities.isEmpty()) {
            for (ELDEventEntity entity : entities) {
                events.add(toModel(entity));
            }
        }
        return events;
    }

    public static List<ELDEventEntity> toEntityList(List<ELDEvent> models) {
        List<ELDEventEntity> entities = new ArrayList<>();
        if (models != null && !models.isEmpty()) {
            for (ELDEvent model : models) {
                entities.add(toEntity(model));
            }
        }
        return entities;
    }
}
