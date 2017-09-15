package com.bsmwireless.data.storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.bsmwireless.data.storage.carriers.CarrierDao;
import com.bsmwireless.data.storage.configurations.ConfigurationDao;
import com.bsmwireless.data.storage.configurations.ConfigurationEntity;
import com.bsmwireless.data.storage.eldevents.ELDEventDao;
import com.bsmwireless.data.storage.eldevents.ELDEventEntity;
import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalDao;
import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.logsheets.LogSheetDao;
import com.bsmwireless.data.storage.logsheets.LogSheetEntity;
import com.bsmwireless.data.storage.users.UserDao;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.data.storage.vehicles.VehicleDao;
import com.bsmwireless.data.storage.vehicles.VehicleEntity;


@Database(entities = {UserEntity.class, VehicleEntity.class, ELDEventEntity.class, CarrierEntity.class,
        HomeTerminalEntity.class, LogSheetEntity.class, ConfigurationEntity.class}, version = 16)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract VehicleDao vehicleDao();
    public abstract ELDEventDao ELDEventDao();
    public abstract CarrierDao carrierDao();
    public abstract HomeTerminalDao homeTerminalDao();
    public abstract ConfigurationDao configurationDao();
    public abstract LogSheetDao logSheetDao();
}
