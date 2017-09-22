package com.bsmwireless.data.storage.hometerminals;

import com.bsmwireless.data.storage.hometerminals.userhometerminal.UserHomeTerminalEntity;
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

    public static HomeTerminalEntity toEntity(HomeTerminal homeTerminal) {
        HomeTerminalEntity entity = null;

        if (homeTerminal != null) {
            entity = new HomeTerminalEntity();
            entity.setId(homeTerminal.getId());
            entity.setAddress(homeTerminal.getAddress());
            entity.setName(homeTerminal.getName());
            entity.setTimezone(homeTerminal.getTimezone());
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

    public static List<HomeTerminalEntity> toEntityList(List<HomeTerminal> homeTerminals) {
        List<HomeTerminalEntity> entities = null;
        if (homeTerminals != null) {
            entities = new ArrayList<>();
            for (HomeTerminal homeTerminal : homeTerminals) {
                entities.add(toEntity(homeTerminal));
            }
        }
        return entities;
    }

    public static List<UserHomeTerminalEntity> toUserRelation(List<HomeTerminal> homeTerminals, int userId) {
        List<UserHomeTerminalEntity> entities = null;
        if (homeTerminals != null) {
            entities = new ArrayList<>(homeTerminals.size());
            for (HomeTerminal homeTerminal : homeTerminals) {
                entities.add(new UserHomeTerminalEntity(homeTerminal.getId(), userId));
            }
        }
        return entities;
    }
}
