package com.example.raindriveiter1_10.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.raindriveiter1_10.model.DrivingRecord;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface DrivingRecordDAO {

    @Query("SELECT * FROM driving_record_table")
    LiveData<List<DrivingRecord>> getAll();

    @Query("SELECT * FROM driving_record_table")
    List<DrivingRecord> getAllRecords();

    @Query("SELECT * FROM driving_record_table WHERE rid = :drivingRecordId LIMIT 1")
    DrivingRecord findByID(int drivingRecordId);

    @Insert
    void insertAll(DrivingRecord... drivingRecords);

    @Insert
    long insert(DrivingRecord drivingRecord);

    @Delete
    void delete(DrivingRecord drivingRecord);

    @Update(onConflict = REPLACE)
    void updateDrivingRecords(DrivingRecord... drivingRecords);

    @Query("DELETE FROM driving_record_table")
    void deleteAll();

    @Query("UPDATE driving_record_table SET driving_date=:drivingDate, driving_start_time=:drivingStartTime, driving_end_time =:drivingEndTime, driving_distance = :drivingDistance, driving_risk = :drivingRisk ,start_location = :startLocation, end_location = :endLocation, icon_str = :iconStr  WHERE rid = :id")
    void updatebyID(int id, String drivingDate, String drivingStartTime, String drivingEndTime, float drivingDistance, String drivingRisk, String startLocation, String endLocation, String iconStr );
}
