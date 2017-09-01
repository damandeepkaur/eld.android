package com.bsmwireless.data.storage.configurations;

import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.models.HomeTerminal;
import com.bsmwireless.models.SyncConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationConverter {
    public static SyncConfiguration toModel(ConfigurationEntity entity) {
        SyncConfiguration model = null;

        if (entity != null) {
            model = new SyncConfiguration();
            model.setName(entity.getName());
            model.setValue(entity.getValue());
        }

        return model;
    }

    public static ConfigurationEntity toEntity(SyncConfiguration model, int userId) {
        ConfigurationEntity entity = null;

        if (model != null) {
            entity = new ConfigurationEntity();
            entity.setUserId(userId);
            entity.setName(model.getName());
            entity.setValue(model.getValue());

        }
        return entity;
    }

    public static List<SyncConfiguration> toModelList(List<ConfigurationEntity> entities) {
        List<SyncConfiguration> configurations = null;
        if (entities != null) {
            configurations = new ArrayList<>();
            for (ConfigurationEntity entity : entities) {
                configurations.add(toModel(entity));
            }
        }
        return configurations;
    }

    public static List<ConfigurationEntity> toEntityList(List<SyncConfiguration> configurations, Integer userId) {
        List<ConfigurationEntity> entities = null;
        if(configurations != null) {
            entities = new ArrayList<>(configurations.size());
            for (SyncConfiguration configuration:configurations) {
                entities.add(toEntity(configuration, userId));
            }
        }
        return entities;
    }
}
