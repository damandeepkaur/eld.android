package com.bsmwireless.data.storage.hometerminals;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.bsmwireless.data.storage.carriers.CarrierEntity;
import com.bsmwireless.data.storage.users.FullUserEntity;
import com.bsmwireless.data.storage.users.UserEntity;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface HomeTerminalDao {

    @Query("DELETE FROM home_terminals WHERE id = :id")
    int deleteHomeTerminal(int id);

    @Insert(onConflict = REPLACE)
    void insertHomeTerminals(List<HomeTerminalEntity> homeTerminals);
}
