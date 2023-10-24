package com.example.raindriveiter1_10.utility;

public class RiskConverter {


    /**
     * This method is used to calculate if the current rainfall
     * and driver experience is suitable for driving practice
     */
    public static boolean riskLevel(double rainfall, int drivingHour){
        boolean result = false;
        if(drivingHour > 0 && drivingHour <= 30){
            if(rainfall > 0 && rainfall < 1){
                result = true;
            }
        }
        else if(drivingHour > 30 && drivingHour <= 60){
            if(rainfall < 10) result = true;
        }
        else if(drivingHour > 60 && drivingHour <= 90){
            if(rainfall < 20) result = true;
        }
        else if(drivingHour > 90){
            result = true;
        }
        return result;
    }
}
