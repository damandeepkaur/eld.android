package com.bsmwireless.data.storage.vehicles;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface VehicleDao {
    @Query("SELECT * FROM vehicles")
    Flowable<List<VehicleEntity>> getVehicles();

    @Query("SELECT * FROM vehicles WHERE vehicles.id IN (:ids)")
    Flowable<List<VehicleEntity>> getVehicles(List<Integer> ids);

    @Query("SELECT * FROM vehicles WHERE vehicles.id IN (:ids)")
    List<VehicleEntity> getVehiclesSync(List<Integer> ids);

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    VehicleEntity getVehicleSync(int id);

    @Query("DELETE FROM vehicles WHERE id = :id")
    int deleteVehicle(int id);

    @Insert(onConflict = REPLACE)
    long insertVehicle(VehicleEntity vehicle);
}
