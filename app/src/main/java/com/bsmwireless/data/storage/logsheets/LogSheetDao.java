package com.bsmwireless.data.storage.logsheets;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface LogSheetDao {
    @Query("DELETE FROM log_sheet_header WHERE log_day = :logDay")
    int delete(long logDay);

    @Insert(onConflict = REPLACE)
    void insert(List<LogSheetEntity> logSheetHeaders);

    @Insert(onConflict = REPLACE)
    long insert(LogSheetEntity logSheetHeader);

    @Query("SELECT * FROM log_sheet_header WHERE log_day = :logDay AND driver_id = :driverId LIMIT 1")
    LogSheetEntity getByLogDaySync(Long logDay, Integer driverId);

    @Query("SELECT * FROM log_sheet_header WHERE sync = 1 AND driver_id = :driverId")
    List<LogSheetEntity> getUnSync(Integer driverId);

    @Query("SELECT * FROM log_sheet_header WHERE log_day < :logDay AND driver_id = :driverId ORDER BY log_day DESC LIMIT 1")
    LogSheetEntity getLatestLogSheet(Long logDay, Integer driverId);

    @Query("SELECT * FROM log_sheet_header WHERE log_day > :startDay AND log_day < :endDay AND driver_id = :driverId")
    Flowable<List<LogSheetEntity>> getLogSheets(Long startDay, Long endDay, Integer driverId);

    @Query("SELECT * FROM log_sheet_header WHERE log_day > :startDay AND log_day < :endDay AND driver_id = :driverId")
    List<LogSheetEntity> getLogSheetsSync(Long startDay, Long endDay, Integer driverId);
}