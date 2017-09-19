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

    @Query("SELECT * FROM events WHERE sync = 1 AND driver_id=:userId ORDER BY event_time")
    List<ELDEventEntity> getUpdateUnsyncEvents(int userId);

    @Query("SELECT * FROM events WHERE sync = 2 AND driver_id=:userId ORDER BY event_time")
    List<ELDEventEntity> getNewUnsyncEvents(int userId);

    @Query("SELECT * FROM events WHERE event_time > :startTime AND event_time < :endTime " +
            "AND driver_id = :driverId ORDER BY event_time")
    List<ELDEventEntity> getEventsFromStartToEndTimeSync(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time > :startTime AND event_time < :endTime " +
            "AND driver_id = :driverId AND (event_type = 1 or event_type = 3) ORDER BY event_time")
    Flowable<List<ELDEventEntity>> getDutyEventsFromStartToEndTime(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time = (SELECT event_time FROM events WHERE event_time < :latestTime AND driver_id = :driverId " +
            "AND (event_type = 1 or event_type = 3) AND status = 1 ORDER BY event_time DESC) AND driver_id = :driverId")
    List<ELDEventEntity> getLatestActiveDutyEventSync(long latestTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time > :startTime AND event_time < :endTime " +
            "AND driver_id = :driverId AND (event_type = 1 or event_type = 3) AND status = 1 ORDER BY event_time")
    List<ELDEventEntity> getActiveEventsFromStartToEndTimeSync(long startTime, long endTime, int driverId);

    @Query("SELECT count(id) FROM events WHERE event_type = :type AND event_code = :code AND mal_code IN (:malCodes)")
    Flowable<Integer> getMalfunctionEventCount(int type, int code, String[] malCodes);

    @Query("SELECT * FROM events WHERE log_sheet = :logDay AND event_type = 4 AND driver_id = :driverId ORDER BY event_code DESC")
    List<ELDEventEntity> getCertificationEventsSync(long logDay, int driverId);

    @Delete
    void delete(ELDEventEntity event);

    @Delete
    void deleteAll(ELDEventEntity... event);

    @Insert(onConflict = REPLACE)
    long insertEvent(ELDEventEntity event);

    @Insert(onConflict = REPLACE)
    long[] insertAll(ELDEventEntity... events);

}
