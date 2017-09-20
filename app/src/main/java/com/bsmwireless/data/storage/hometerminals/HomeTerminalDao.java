package com.bsmwireless.data.storage.hometerminals;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface HomeTerminalDao {

    @Query("DELETE FROM home_terminals WHERE id = :id")
    int deleteHomeTerminal(int id);

    @Insert(onConflict = REPLACE)
    void insertHomeTerminals(List<HomeTerminalEntity> homeTerminals);

    @Query("SELECT * FROM home_terminals WHERE id = :id LIMIT 1")
    HomeTerminalEntity getHomeTerminalSync(int id);

    @Query("SELECT * FROM home_terminals WHERE home_terminals.id IN (:ids)")
    List<HomeTerminalEntity> getHomeTerminalsSync(List<Integer> ids);

    @Query("SELECT * FROM home_terminals WHERE home_terminals.id IN (:ids)")
    Flowable<List<HomeTerminalEntity>> getHomeTerminals(List<Integer> ids);
}
