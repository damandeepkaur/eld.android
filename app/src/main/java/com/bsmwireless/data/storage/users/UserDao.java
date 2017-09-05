package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    Flowable<UserEntity> getUser(int id);

    @Query("SELECT * FROM users WHERE users.id IN (:ids)")
    Flowable<List<UserEntity>> getDrivers(List<Integer> ids);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getUserSync(int id);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    Flowable<FullUserEntity> getFullUser(int id);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    FullUserEntity getFullUserSync(int id);

    @Query("SELECT last_vehicle_ids FROM users WHERE id = :id LIMIT 1")
    Flowable<String[]> getUserLastVehicles(int id);

    @Query("SELECT last_vehicle_ids FROM users WHERE id = :id LIMIT 1")
    String getUserLastVehiclesSync(int id);

    @Query("SELECT timezone FROM users WHERE id = :id LIMIT 1")
    String getUserTimezoneSync(int id);

    @Query("SELECT timezone FROM users WHERE id = :id LIMIT 1")
    Flowable<String> getUserTimezone(int id);

    @Query("DELETE FROM users WHERE id = :id")
    int deleteUser(int id);

    @Insert(onConflict = REPLACE)
    long insertUser(UserEntity user);

    @Query("UPDATE users SET last_vehicle_ids = :vehicles WHERE id = :id")
    void setUserLastVehicles(int id, String vehicles);

    @Query("UPDATE users SET co_drivers_ids = :coDrivers WHERE id = :id")
    void setUserCoDrivers(int id, String coDrivers);

    @Query("SELECT co_drivers_ids FROM users WHERE id = :id LIMIT 1")
    Flowable<String> getUserCoDrivers(int id);

    @Query("SELECT co_drivers_ids FROM users WHERE id = :id LIMIT 1")
    String getUserCoDriversSync(int id);
}
