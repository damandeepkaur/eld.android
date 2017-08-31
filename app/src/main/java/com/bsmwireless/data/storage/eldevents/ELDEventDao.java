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

    @Query("SELECT * FROM events WHERE sync = 1 ORDER BY event_time")
    List<ELDEventEntity> getUpdateUnsyncEvents();

    @Query("SELECT * FROM events WHERE sync = 2 ORDER BY event_time")
    List<ELDEventEntity> getNewUnsyncEvents();

    @Query("SELECT * FROM events WHERE event_time > :startTime AND event_time < :endTime")
    List<ELDEventEntity> getEventsForInterval(long startTime, long endTime);

    @Query("SELECT * FROM events WHERE id = :id")
    Flowable<ELDEventEntity> getEventById(int id);

    @Query("SELECT * FROM events WHERE event_time > :startTime and event_time < :endTime " +
            "and driver_id = :driverId ORDER BY event_time")
    List<ELDEventEntity> getEventsFromStartToEndTimeSync(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time > :startTime and event_time < :endTime " +
            "and driver_id = :driverId and (event_type = 1 or event_type = 3) ORDER BY event_time")
    Flowable<List<ELDEventEntity>> getDutyEventsFromStartToEndTime(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time = (SELECT event_time FROM events WHERE event_time < :latestTime and driver_id = :driverId " +
            "and (event_type = 1 or event_type = 3) and status = 1 ORDER BY event_time DESC) and driver_id = :driverId")
    List<ELDEventEntity> getLatestActiveDutyEventSync(long latestTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time > :startTime and event_time < :endTime " +
            "and driver_id = :driverId and event_type = 1 and status = 1 ORDER BY event_time")
    Flowable<List<ELDEventEntity>> getActiveDutyEventsAndFromStartToEndTime(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time > :startTime and event_time < :endTime " +
            "and driver_id = :driverId and (event_type = 1 or event_type = 3) and status = 1 ORDER BY event_time")
    List<ELDEventEntity> getActiveEventsFromStartToEndTimeSync(long startTime, long endTime, int driverId);

    @Delete
    void delete(ELDEventEntity event);

    @Delete
    void deleteAll(ELDEventEntity... event);

    @Insert(onConflict = REPLACE)
    long insertEvent(ELDEventEntity event);

    @Insert(onConflict = REPLACE)
    long[] insertAll(ELDEventEntity... events);
}
