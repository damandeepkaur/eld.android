package com.bsmwireless.data.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.data.storage.vehicles.VehicleDao;
import com.bsmwireless.data.storage.vehicles.VehicleEntity;

@Database(entities = {UserEntity.class, VehicleEntity.class, ELDEventEntity.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract VehicleDao vehicleDao();
    public abstract ELDEventDao ELDEventDao();
}
