package com.bsmwireless.data.storage.carriers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CarrierDao {

    @Query("DELETE FROM carriers WHERE id = :id")
    int deleteCarrier(int id);

    @Query("DELETE FROM carriers WHERE user_id = :userId")
    int deleteByUserId(int userId);

    @Insert(onConflict = REPLACE)
    void insertCarriers(List<CarrierEntity> carrier);
}
