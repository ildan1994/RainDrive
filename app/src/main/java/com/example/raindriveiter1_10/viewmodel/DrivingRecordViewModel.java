package com.example.raindriveiter1_10.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.raindriveiter1_10.model.DrivingRecord;
import com.example.raindriveiter1_10.repository.DrivingRecordRepository;

import java.util.List;

public class DrivingRecordViewModel extends ViewModel {
    private DrivingRecordRepository dRepository;
    private MutableLiveData<List<DrivingRecord>> allDrivingRecords;
    public DrivingRecordViewModel() {
        allDrivingRecords = new MutableLiveData<>();
    }
    public void setDrivingRecords(List<DrivingRecord> drivingRecords) {
        allDrivingRecords.setValue(drivingRecords);
    }

    public LiveData<List<DrivingRecord>> getAllDrivingRecords() {
        return dRepository.getAllDrivingRecords();
    }

    public void initializeVars(Application application) {
        dRepository = new DrivingRecordRepository(application);
    }

    public void insert(DrivingRecord drivingRecord) {
        dRepository.insert(drivingRecord);
    }

    public void insertAll(DrivingRecord... drivingRecords) {
        dRepository.insertAll(drivingRecords);
    }
    public void deleteAll() {
        dRepository.deleteAll();
    }
    public void update(DrivingRecord...drivingRecords) {
        dRepository.updateDrivingRecords(drivingRecords);
    }
    public void updateByID(int drivingRecordId, String drivingDate, String drivingStartTime, String drivingEndTime, float drivingDistance,  String drivingRisk, String startLocation, String endLocation, String iconStr ) {
        dRepository.updateDrivingRecordByID(drivingRecordId, drivingDate, drivingStartTime, drivingEndTime,drivingDistance, drivingRisk,startLocation, endLocation,iconStr );
    }
    public DrivingRecord findByID(int drivingRecordId) {
        return dRepository.findByID(drivingRecordId);
    }
}
