package com.bsmwireless.data.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.data.storage.vehicle.VehicleDao;
import com.bsmwireless.data.storage.vehicle.VehicleEntity;

@Database(entities = {UserEntity.class, VehicleEntity.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userModel();
    public abstract VehicleDao vehicleModel();
}
