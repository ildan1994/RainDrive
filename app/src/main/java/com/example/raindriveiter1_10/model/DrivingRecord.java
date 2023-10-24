package com.example.raindriveiter1_10.model;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Entity(tableName = "driving_record_table")
public class DrivingRecord {
    @PrimaryKey(autoGenerate = true)
    public int rid;

    @ColumnInfo(name = "driving_date")
    public String drivingDate;

    @ColumnInfo(name = "driving_start_time")
    public String drivingStartTime;

    @ColumnInfo(name = "driving_end_time")
    public String drivingEndTime;

    @ColumnInfo(name = "driving_distance")
    public float drivingDistance;

    @ColumnInfo(name = "driving_risk")
    public String drivingRisk;

    @ColumnInfo(name = "start_location")
    public String startLocation;

    @ColumnInfo(name = "end_location")
    public String endLocation;

    @ColumnInfo(name = "icon_str")
    public String iconStr;


    public DrivingRecord(String drivingDate, String drivingStartTime, String drivingEndTime, float drivingDistance, String drivingRisk, String startLocation, String endLocation, String iconStr) {
        this.drivingDate = drivingDate;
        this.drivingStartTime = drivingStartTime;
        this.drivingEndTime = drivingEndTime;
        this.drivingDistance = drivingDistance;
        this.drivingRisk = drivingRisk;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.iconStr = iconStr;
    }

    public String getIconStr() {
        return iconStr;
    }

    public void setIconStr(String iconStr) {
        this.iconStr = iconStr;
    }

    public int getRid() {
        return rid;
    }

    public String getDrivingDate() {
        return drivingDate;
    }

    public String getDrivingStartTime() {
        return drivingStartTime;
    }

    public String getDrivingEndTime() {
        return drivingEndTime;
    }

    public float getDrivingDistance() {
        return drivingDistance;
    }

    public String getDrivingRisk() {
        return drivingRisk;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setDrivingDate(String drivingDate) {
        this.drivingDate = drivingDate;
    }

    public void setDrivingStartTime(String drivingStartTime) {
        this.drivingStartTime = drivingStartTime;
    }

    public void setDrivingEndTime(String drivingEndTime) {
        this.drivingEndTime = drivingEndTime;
    }

    public void setDrivingDistance(float drivingDistance) {
        this.drivingDistance = drivingDistance;
    }

    public void setDrivingRisk(String drivingRisk) {
        this.drivingRisk = drivingRisk;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getRecordMins() {
        int recordMins =  Math.abs(
                (LocalTime.parse(this.drivingEndTime).toSecondOfDay() -
                        LocalTime.parse(this.drivingStartTime).toSecondOfDay())
                        /60);
        return recordMins;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isAtNight() {
        Date startTimeDate = null;
        try {
            startTimeDate = new SimpleDateFormat("HH:mm:ss").parse(this.drivingStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalTime localTime = startTimeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        return localTime.isAfter(LocalTime.of(20, 0, 0)) || localTime.isBefore(LocalTime.of(5, 0, 0)) ;
    }

    public boolean isRainDay() {
        int weatherID = Integer.parseInt(this.iconStr.substring(0,2));
        return weatherID >=9 && weatherID <=11;
    }

}
