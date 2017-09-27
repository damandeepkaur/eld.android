package com.bsmwireless.data.storage.logsheets;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.bsmwireless.data.storage.eldevents.ELDEventEntity;

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
    void insert(LogSheetEntity logSheetHeader);

    @Query("SELECT * FROM log_sheet_header WHERE log_day = :logDay LIMIT 1")
    LogSheetEntity getByLogDaySync(Long logDay);

    @Query("SELECT * FROM log_sheet_header WHERE log_day > :startDay AND log_day < :endDay AND driver_id = :driverId ORDER BY log_day")
    Flowable<List<LogSheetEntity>> getLogSheetsFromStartToEndDay(long startDay, long endDay, int driverId);
}
