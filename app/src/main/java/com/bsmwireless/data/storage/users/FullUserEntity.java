package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import android.support.annotation.Nullable;

import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.configurations.ConfigurationEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.hometerminals.userhometerminal.UserHomeTerminalEntity;
import com.bsmwireless.models.SyncConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FullUserEntity {

    @Embedded
    private UserEntity mUserEntity;

    @Relation(parentColumn = "id", entityColumn = "user_id", entity = CarrierEntity.class)
    private List<CarrierEntity> mCarriers;

    @Relation(parentColumn = "id",
              entityColumn = "user_id",
              entity = UserHomeTerminalEntity.class)
    private List<UserHomeTerminalEntity> mHomeTerminalIds;

    @Relation(parentColumn = "id", entityColumn = "user_id", entity = ConfigurationEntity.class)
    private List<ConfigurationEntity> mConfigurationEntities;

    @Ignore
    @Nullable
    private List<HomeTerminalEntity> mHomeTerminalEntities;

    public FullUserEntity() {
        mUserEntity = new UserEntity();
    }

    public UserEntity getUserEntity() {
        return mUserEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        mUserEntity = userEntity;
    }

    public List<CarrierEntity> getCarriers() {
        return mCarriers;
    }

    public void setCarriers(List<CarrierEntity> carriers) {
        mCarriers = carriers;
    }

    public void setHomeTerminalIds(List<UserHomeTerminalEntity> homeTerminalIds) {
        mHomeTerminalIds = homeTerminalIds;
    }

    public List<Integer> getHomeTerminalIds() {
        List<Integer> ids = new ArrayList<>(mHomeTerminalIds.size());
        for (UserHomeTerminalEntity entity: mHomeTerminalIds) {
            ids.add(entity.getHomeTerminalId());
        }
        return ids;
    }

    @Nullable
    public List<HomeTerminalEntity> getHomeTerminalEntities() {
        return mHomeTerminalEntities;
    }

    public void setHomeTerminalEntities(@Nullable List<HomeTerminalEntity> homeTerminalEntities) {
        mHomeTerminalEntities = homeTerminalEntities;
    }

    public List<ConfigurationEntity> getConfigurationEntities() {
        return mConfigurationEntities;
    }

    public void setConfigurationEntities(List<ConfigurationEntity> configurationEntities) {
        mConfigurationEntities = configurationEntities;
    }

    public List<String> getCyclesList() {
        for (ConfigurationEntity configuration: mConfigurationEntities) {
            if (SyncConfiguration.Type.CYCLE.getName().equals(configuration.getName())) {
                return ListConverter.toStringList(configuration.getValue());
            }
        }
        return Collections.emptyList();
    }
}
