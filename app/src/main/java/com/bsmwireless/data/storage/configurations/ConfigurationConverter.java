package com.bsmwireless.data.storage.configurations;

import com.bsmwireless.models.SyncConfiguration;

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
}
