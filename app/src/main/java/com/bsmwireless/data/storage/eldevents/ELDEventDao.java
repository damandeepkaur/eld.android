package com.bsmwireless.data.storage.eldevents;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ELDEventDao {
    @Query("SELECT * FROM events")
    List<ELDEventEntity> getAll();

    @Query("SELECT * FROM events WHERE sync = 1 and driver_id=:userId ORDER BY event_time")
    List<ELDEventEntity> getUpdateUnsyncEvents(int userId);

    @Query("SELECT * FROM events WHERE sync = 2 and driver_id=:userId ORDER BY event_time")
    List<ELDEventEntity> getNewUnsyncEvents(int userId);

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
    Flowable<List<ELDEventEntity>> getLatestActiveDutyEvent(long latestTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time = (SELECT event_time FROM events WHERE event_time < :latestTime and driver_id = :driverId " +
            "and (event_type = 1 or event_type = 3) and status = 1 ORDER BY event_time DESC) and driver_id = :driverId")
    List<ELDEventEntity> getLatestActiveDutyEventSync(long latestTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time > :startTime and event_time < :endTime " +
            "and driver_id = :driverId and event_type = 1 and status = 1 ORDER BY event_time")
    Flowable<List<ELDEventEntity>> getActiveDutyEventsAndFromStartToEndTime(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time > :startTime and event_time < :endTime " +
            "and driver_id = :driverId and (event_type = 1 or event_type = 3) and status = 1 ORDER BY event_time")
    List<ELDEventEntity> getActiveEventsFromStartToEndTimeSync(long startTime, long endTime, int driverId);

    @Query("SELECT count(id) FROM events WHERE event_type = :type and event_code = :code and mal_code IN (:malCodes)")
    Flowable<Integer> getMalfunctionEventCount(int type, int code, String[] malCodes);

    /**
     * Returns the latest event from a database
     * @param type event type
     * @param malCode malfunction code. For non-malfunction event should be empty
     * @return latest ELD event
     */
    @Query("SELECT * FROM events WHERE event_type = :type and mal_code = :malCode ORDER BY event_time LIMIT 1")
    Maybe<ELDEventEntity> getLatestEvent(int type, String malCode);

    /**
     * Returns the latest event from a database
     * @param type event type
     * @param malCode malfunction code. For non-malfunction event should be empty
     * @return latest ELD event
     */
    @Query("SELECT * FROM events WHERE event_type = :type and mal_code = :malCode ORDER BY event_time LIMIT 1")
    ELDEventEntity getLatestEventSync(int type, String malCode);

    @Delete
    void delete(ELDEventEntity event);

    @Delete
    void deleteAll(ELDEventEntity... event);

    @Insert(onConflict = REPLACE)
    long insertEvent(ELDEventEntity event);

    @Insert(onConflict = REPLACE)
    long[] insertAll(ELDEventEntity... events);

}
