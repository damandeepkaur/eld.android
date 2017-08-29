package com.bsmwireless.data.storage.logsheets;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

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
    LogSheetEntity getByLogDay(long logDay);
}
