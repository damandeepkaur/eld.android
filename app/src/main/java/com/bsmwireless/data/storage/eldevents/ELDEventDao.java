package com.bsmwireless.data.storage.eldevents;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ELDEventDao {
    @Query("SELECT * FROM events")
    List<ELDEventEntity> getAll();

    @Query("SELECT * FROM events WHERE sync = 2 AND mobile_time NOT IN (SELECT mobile_time FROM events WHERE sync = 1) ORDER BY id")
    List<ELDEventEntity> getUpdateUnsyncEvents();

    @Query("SELECT * FROM events WHERE sync = 1 AND driver_id=:userId ORDER BY id")
    List<ELDEventEntity> getNewUnsyncEvents(int userId);

    @Query("SELECT * FROM events WHERE event_time >= :startTime AND event_time < :endTime and driver_id = :driverId ORDER BY event_time")
    Single<List<ELDEventEntity>> getEventsFromStartToEndTimeOnce(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time >= :startTime AND event_time < :endTime " +
            "AND driver_id = :driverId AND (event_type = 1 or event_type = 3) ORDER BY event_time, mobile_time, status DESC, sync")
    Flowable<List<ELDEventEntity>> getDutyEventsFromStartToEndTime(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time >= :startTime AND event_time < :endTime " +
            "AND driver_id = :driverId AND (event_type = 1 or event_type = 3) ORDER BY event_time, mobile_time, status DESC, sync")
    Single<List<ELDEventEntity>> getDutyEventsFromStartToEndTimeSync(long startTime, long endTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time < :latestTime AND driver_id = :driverId " +
            "AND (event_type = 1 or event_type = 3) AND status = 1 ORDER BY event_time, id DESC")
    List<ELDEventEntity> getLatestActiveDutyEventSync(long latestTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time < :latestTime AND driver_id = :driverId " +
            "AND (event_type = 1 or event_type = 3) AND status = 1 ORDER BY event_time, id DESC")
    Single<List<ELDEventEntity>> getLatestActiveDutyEventOnce(long latestTime, int driverId);

    @Query("SELECT * FROM events WHERE event_time >= :startTime AND event_time < :endTime " +
            "AND driver_id = :driverId AND (event_type = 1 or event_type = 3) AND status = 1 ORDER BY event_time, mobile_time, status DESC")
    List<ELDEventEntity> getActiveEventsFromStartToEndTimeSync(long startTime, long endTime, int driverId);

    @Query("SELECT count(id) FROM events WHERE event_time >= :startTime AND event_time < :endTime AND driver_id = :driverId AND event_type = 7 AND (event_code = 1 OR event_code = 2)")
    Integer getMalfunctionEventCountSync(int driverId, long startTime, long endTime);

    @Query("SELECT count(id) FROM events WHERE event_time >= :startTime AND event_time < :endTime AND driver_id = :driverId AND event_type = 7 AND (event_code = 3 OR event_code = 4)")
    Integer getDiagnosticEventCountSync(int driverId, long startTime, long endTime);

    @Query("SELECT count(id) FROM events WHERE driver_id = :driverId AND event_type = :type and event_code = :code and mal_code IN (:malCodes)")
    Flowable<Integer> getMalfunctionEventCount(int driverId, int type, int code, String[] malCodes);

    /**
     * Returns the latest event from a database
     *
     * @param type     event type
     * @param malCode  malfunction code. For non-malfunction event should be empty
     * @param driverId driver id
     * @param status   status code
     * @return latest ELD event
     */
    @Query("SELECT * FROM events WHERE driver_id = :driverId AND event_type = :type AND mal_code = :malCode AND status = :status ORDER BY event_time DESC LIMIT 1")
    Flowable<ELDEventEntity> getLatestEvent(int driverId, int type, String malCode, int status);

    /**
     * Returns the latest event from a database
     *
     * @param type     event type
     * @param malCode  malfunction code. For non-malfunction event should be empty
     * @param driverId driver id
     * @param status   status code
     * @return latest ELD event
     */
    @Query("SELECT * FROM events WHERE driver_id = :driverId AND event_type = :type AND mal_code = :malCode AND status = :status ORDER BY event_time DESC LIMIT 1")
    ELDEventEntity getLatestEventSync(int driverId, int type, String malCode, int status);

    /**
     * Loads a count for events with specified lat lng code and status
     *
     * @param driverId   driver id for which will be load a count
     * @param latLngCode range of lat lng codes
     * @param status event status
     * @return count of events
     */
    @Query("SELECT count(id) FROM events WHERE driver_id = :driverId AND latlng_code IN (:latLngCode) AND status = :status")
    Single<Integer> getChangingLocationEventCount(int driverId, String[] latLngCode, int status);

    /**
     * Loads all malfunctions by parameters
     *
     * @param driverId driver id
     * @param type     event type
     * @param malCodes malcode ranges
     * @return
     */
    @Query("SELECT * FROM events WHERE driver_id = :driverId AND event_type = :type AND status = :status AND mal_code IN (:malCodes) ORDER BY event_time")
    Single<List<ELDEventEntity>> loadMalfunctions(int driverId, int type, String[] malCodes, int status);

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

    @Query("DELETE FROM events WHERE id IN (SELECT id FROM events WHERE driver_id = :driverId AND sync = 0 AND event_time >= :eventTimeStart AND event_time < :eventTimeEnd AND mobile_time = :mobileTime AND event_code = :eventCode AND event_type = :eventType ORDER BY status DESC LIMIT 1)")
    int delete(long driverId, long eventTimeStart, long eventTimeEnd, long mobileTime, int eventCode, int eventType);
}
