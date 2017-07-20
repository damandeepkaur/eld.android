package com.bsmwireless.data.storage.configurations;

import com.bsmwireless.models.Configuration;

public class ConfigurationConverter {
    public static Configuration toModel(ConfigurationEntity entity) {
        Configuration model = null;

        if (entity != null) {
            model = new Configuration();
            model.setName(entity.getName());
            model.setValue(entity.getValue());
        }

        return model;
    }

    public static ConfigurationEntity toEntity(Configuration model, int userId) {
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
