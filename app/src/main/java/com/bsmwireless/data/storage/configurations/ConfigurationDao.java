package com.bsmwireless.data.storage.configurations;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ConfigurationDao {
    @Query("SELECT * FROM configurations")
    List<ConfigurationEntity> getAll();

    @Query("SELECT * FROM configurations WHERE user_id = :userId")
    Flowable<ConfigurationEntity> getByUserId(int userId);

    @Query("DELETE FROM configurations WHERE user_id = :userId")
    int deleteByUserId(int userId);

    @Insert(onConflict = REPLACE)
    long insert(ConfigurationEntity configuration);

    @Insert(onConflict = REPLACE)
    void insertAll(ConfigurationEntity... configurations);

    @Insert(onConflict = REPLACE)
    void insertAll(List<ConfigurationEntity> configurations);
}
