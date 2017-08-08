package com.bsmwireless.data.storage.carriers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.bsmwireless.data.storage.hometerminals.HomeTerminalEntity;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.users.UserEntity;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CarrierDao {

    @Query("DELETE FROM carriers WHERE id = :id")
    int deleteCarrier(int id);

    @Insert(onConflict = REPLACE)
    void insertCarriers(List<CarrierEntity> carrier);
}
