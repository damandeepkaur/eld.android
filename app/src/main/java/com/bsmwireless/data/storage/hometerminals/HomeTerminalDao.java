package com.bsmwireless.data.storage.hometerminals;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface HomeTerminalDao {

    @Query("DELETE FROM home_terminals WHERE id = :id")
    int deleteHomeTerminal(int id);

    @Insert(onConflict = REPLACE)
    void insertHomeTerminals(List<HomeTerminalEntity> homeTerminals);

    @Query("SELECT * FROM home_terminals WHERE id = :id LIMIT 1")
    HomeTerminalEntity getHomeTerminalSync(int id);
}
