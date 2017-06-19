package com.bsmwireless.data.storage.users;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users")
    Flowable<List<UserEntity>> getUsers();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    Flowable<UserEntity> getUserById(int id);

    @Query("DELETE FROM users WHERE id = :id")
    int deleteUserById(int id);

    @Query("SELECT * FROM users WHERE account_name = :name LIMIT 1")
    Flowable<UserEntity> getUserByAccountName(String name);

    @Query("DELETE FROM users WHERE account_name = :name")
    int deleteUserByAccountName(String name);

    @Insert(onConflict = REPLACE)
    long insertUser(UserEntity user);
}
