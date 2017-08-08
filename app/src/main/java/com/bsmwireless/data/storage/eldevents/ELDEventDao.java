package com.bsmwireless.data.storage.eldevents;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ELDEventDao {
    @Query("SELECT * FROM events")
    List<ELDEventEntity> getAll();

    @Query("SELECT * FROM events WHERE is_sync = 0")
    Flowable<List<ELDEventEntity>> getUnsyncEvents();

    @Query("SELECT * FROM events WHERE event_time > :startTime AND event_time < :endTime")
    List<ELDEventEntity> getEventsForInterval(long startTime, long endTime);

    @Query("SELECT * FROM events WHERE id = :id")
    Flowable<ELDEventEntity> getEventById(int id);

    @Query("SELECT * FROM events WHERE event_time > :startTime and event_time < :endTime ORDER BY event_time")
    Flowable<List<ELDEventEntity>> getEventFromStartToEndTime(long startTime, long endTime);

    @Delete
    void delete(ELDEventEntity event);

    @Insert(onConflict = REPLACE)
    long insertEvent(ELDEventEntity event);

    @Insert(onConflict = REPLACE)
    void insertAll(ELDEventEntity... events);
}
