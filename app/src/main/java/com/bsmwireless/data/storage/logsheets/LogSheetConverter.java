package com.bsmwireless.data.storage.logsheets;

import com.bsmwireless.data.storage.hometerminals.HomeTerminalConverter;
import com.bsmwireless.models.LogSheetHeader;

import java.util.ArrayList;
import java.util.List;

public class LogSheetConverter {
    public static LogSheetHeader toModel(LogSheetEntity entity) {
        LogSheetHeader model = null;

        if (entity != null) {
            model = new LogSheetHeader();
            model.setLogDay(entity.getLogDay());
            model.setVehicleId(entity.getVehicleId());
            model.setBoxId(entity.getBoxId());
            model.setStartOfDay(entity.getStartOfDay());
            model.setShippingId(entity.getShippingId());
            model.setTrailerIds(entity.getTrailerIds());
            model.setCoDriverIds(entity.getCoDriverIds());
            model.setComment(entity.getComment());
            model.setDutyCycle(entity.getDutyCycle());
            model.setHomeTerminal(HomeTerminalConverter.toHomeTerminal(entity.getHomeTerminal()));
            model.setAdditions(entity.getAdditions());
            model.setSigned(entity.getSigned());
        }

        return model;
    }

    public static LogSheetEntity toEntity(LogSheetHeader model) {
        LogSheetEntity entity = null;

        if (model != null) {
            entity = new LogSheetEntity();
            entity.setLogDay(model.getLogDay());
            entity.setVehicleId(model.getVehicleId());
            entity.setBoxId(model.getBoxId());
            entity.setStartOfDay(model.getStartOfDay());
            entity.setShippingId(model.getShippingId());
            entity.setTrailerIds(model.getTrailerIds());
            entity.setCoDriverIds(model.getCoDriverIds());
            entity.setComment(model.getComment());
            entity.setDutyCycle(model.getDutyCycle());
            entity.setHomeTerminal(HomeTerminalConverter.toEntity(model.getHomeTerminal(), null));
            entity.setAdditions(model.getAdditions());
            entity.setSigned(model.getSigned());
        }

        return entity;
    }

    public static List<LogSheetHeader> toModelList(List<LogSheetEntity> entities) {
        List<LogSheetHeader> logSheetHeaders = null;
        if (entities != null) {
            logSheetHeaders = new ArrayList<>(entities.size());
            for (LogSheetEntity entity : entities) {
                logSheetHeaders.add(toModel(entity));
            }
        }
        return logSheetHeaders;
    }

    public static List<LogSheetEntity> toEntityList(List<LogSheetHeader> models) {
        List<LogSheetEntity> entities = null;
        if (models != null) {
            entities = new ArrayList<>(models.size());
            for (LogSheetHeader model : models) {
                entities.add(toEntity(model));
            }
        }
        return entities;
    }
}
