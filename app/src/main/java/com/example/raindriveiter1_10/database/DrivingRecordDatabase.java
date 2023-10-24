package com.example.raindriveiter1_10.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.raindriveiter1_10.dao.DrivingRecordDAO;
import com.example.raindriveiter1_10.model.DrivingRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DrivingRecord.class}, version = 9, exportSchema = false)
public abstract class DrivingRecordDatabase extends RoomDatabase {
    public abstract DrivingRecordDAO drivingRecordDAO();
    private static DrivingRecordDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized DrivingRecordDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    DrivingRecordDatabase.class,
                    "DrivingRecordDatabase").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
