package com.bsmwireless.common.dagger;

import android.arch.persistence.room.Room;
import android.content.Context;


import com.bsmwireless.data.storage.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    private final static String DATABASE_NAME = "bsm_db";

    @Singleton
    @Provides
    AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
    }
}
