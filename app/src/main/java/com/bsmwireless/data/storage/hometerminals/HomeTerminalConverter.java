package com.bsmwireless.data.storage.hometerminals;

import com.bsmwireless.models.HomeTerminal;

import java.util.ArrayList;
import java.util.List;

public class HomeTerminalConverter {
    public static HomeTerminal toHomeTerminal(HomeTerminalEntity entity) {
        HomeTerminal homeTerminal = null;

        if (entity != null) {
            homeTerminal = new HomeTerminal();
            homeTerminal.setId(entity.getId());
            homeTerminal.setAddress(entity.getAddress());
            homeTerminal.setName(entity.getName());
            homeTerminal.setTimezone(entity.getTimezone());
        }

        return homeTerminal;
    }

    public static HomeTerminalEntity toEntity(HomeTerminal homeTerminal, Integer userId) {
        HomeTerminalEntity entity = null;

        if (homeTerminal != null) {
            entity = new HomeTerminalEntity();
            entity.setId(homeTerminal.getId());
            entity.setAddress(homeTerminal.getAddress());
            entity.setName(homeTerminal.getName());
            entity.setTimezone(homeTerminal.getTimezone());
            entity.setUserId(userId);
        }

        return entity;
    }

    public static List<HomeTerminal> toHomeTerminalList(List<HomeTerminalEntity> entities) {
        List<HomeTerminal> homeTerminals = null;
        if (entities != null) {
            homeTerminals = new ArrayList<>();
            for (HomeTerminalEntity entity : entities) {
                homeTerminals.add(toHomeTerminal(entity));
            }
        }
        return homeTerminals;
    }

    public static List<HomeTerminalEntity> toEntityList(List<HomeTerminal> homeTerminals, Integer userId) {
        List<HomeTerminalEntity> entities = null;
        if (homeTerminals != null) {
            entities = new ArrayList<>();
            for (HomeTerminal homeTerminal : homeTerminals) {
                entities.add(toEntity(homeTerminal, userId));
            }
        }
        return entities;
    }
}
