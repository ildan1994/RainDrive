package com.example.raindriveiter1_10.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.raindriveiter1_10.dao.DrivingRecordDAO;
import com.example.raindriveiter1_10.database.DrivingRecordDatabase;
import com.example.raindriveiter1_10.model.DrivingRecord;

import java.util.List;

public class DrivingRecordRepository {

    private DrivingRecordDAO dao;
    private LiveData<List<DrivingRecord>> allDrivingRecords;
    private DrivingRecord drivingRecord;

    public DrivingRecordRepository(Application application) {
        DrivingRecordDatabase db = DrivingRecordDatabase.getInstance(application);
        dao = db.drivingRecordDAO();
    }

    public LiveData<List<DrivingRecord>> getAllDrivingRecords() {
        allDrivingRecords = dao.getAll();
        return allDrivingRecords;
    }

    public List<DrivingRecord> getAllDrivingRecordsCommon() {
        List<DrivingRecord> drivingRecords = dao.getAllRecords();
        return drivingRecords;
    }

    public void insert(final DrivingRecord drivingRecord) {
        DrivingRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insert(drivingRecord);
            }
        });
    }

    public void deleteAll() {
        DrivingRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAll();
            }
        });
    }

    public void delete(final DrivingRecord drivingRecord) {
        DrivingRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(drivingRecord);
            }
        });
    }

    public void insertAll(final DrivingRecord... drivingRecords) {
        DrivingRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertAll(drivingRecords);
            }
        });
    }

    public void updateDrivingRecords(final DrivingRecord... drivingRecords) {
        DrivingRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.updateDrivingRecords(drivingRecords);
            }
        });
    }

    public void updateDrivingRecordByID(final int drivingRecordId, final String drivingDate, final String drivingStartTime, final String drivingEndTime, final float drivingDistance, final String drivingRisk, final String startLocation, final String endLocation, final String iconStr) {
        DrivingRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.updatebyID(drivingRecordId, drivingDate, drivingStartTime, drivingEndTime, drivingDistance, drivingRisk, startLocation,endLocation, iconStr);
            }
        });
    }

    public DrivingRecord findByID(final int drivingRecordId) {
        DrivingRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DrivingRecord drivingRecord = dao.findByID(drivingRecordId);
                setDrivingRecord(drivingRecord);

            }
        });
        return drivingRecord;
    }

    public void setDrivingRecord(DrivingRecord drivingRecord) {
        this.drivingRecord = drivingRecord;
    }
}
