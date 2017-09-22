package com.bsmwireless.data.storage.hometerminals.userhometerminal;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserHomeTerminalDao {

    @Query("DELETE FROM user_home_terminal WHERE user_id = :id")
    int deleteUserHomeTerminal(int id);

    @Insert(onConflict = REPLACE)
    void insertUserHomeTerminal(List<UserHomeTerminalEntity> relations);
}
