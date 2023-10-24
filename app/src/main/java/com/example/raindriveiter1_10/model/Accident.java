package com.example.raindriveiter1_10.model;

/**
 * The POJO used to store the accident data
 * the object include the location where the accident is happened
 * and the injury level and the rain level and date of the accident
 */
public class Accident {

    private double longitude;
    private double latitude;
    private int injuryLevel;
    private double rainLevel;
    private String date;

    public Accident(){}


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getInjuryLevel() {
        return injuryLevel;
    }

    public void setInjuryLevel(int injuryLevel) {
        this.injuryLevel = injuryLevel;
    }

    public double getRainLevel() {
        return rainLevel;
    }

    public void setRainLevel(double rainLevel) {
        this.rainLevel = rainLevel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
