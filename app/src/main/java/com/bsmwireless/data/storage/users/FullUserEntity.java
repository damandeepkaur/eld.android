package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;

import java.util.List;

public class FullUserEntity {
    @Embedded
    private UserEntity mUserEntity;
    @Relation(parentColumn = "id", entityColumn = "user_id", entity = CarrierEntity.class)
    private List<CarrierEntity> mCarriers;
    @Relation(parentColumn = "id", entityColumn = "user_id", entity = HomeTerminalEntity.class)
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

    public List<HomeTerminalEntity> getHomeTerminalEntities() {
        return mHomeTerminalEntities;
    }

    public void setHomeTerminalEntities(
            List<HomeTerminalEntity> homeTerminalEntities) {
        mHomeTerminalEntities = homeTerminalEntities;
    }
}
