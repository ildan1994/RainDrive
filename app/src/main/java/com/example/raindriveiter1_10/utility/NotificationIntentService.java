package com.example.raindriveiter1_10.utility;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.database.DrivingRecordDatabase;
import com.example.raindriveiter1_10.model.DrivingRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import static com.example.raindriveiter1_10.activity.App.CHANNEL_ID;

/**
 * This is the intent service that will be triggered by
 * the alarm manager every 5 min to check if it is a suitable
 * time to show notification in users device
 */
public class NotificationIntentService extends IntentService {

    private static final String TAG = "IntentService";
    private DrivingRecordDatabase db = null;
    private int totalHours = 0;
    private int driverLevel = 0;
    private ArrayList<Integer> suitableWeekDay;
    private Calendar calendar;
    public static final String SHARED_PREF = "last_time";
    public static final String TEXT = "last_mills";
    private long lastMill = 0;

    public NotificationIntentService()
    {
        super("NotificationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(getApplicationContext(), "alarm is working.....", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
    }


    /**
     * This is the callback that will be invoked every 5 min to check if
     * it is a suitable time to show the notification
     * There are several requirements for triggering the notification
     * 1. If it is the prefer day of a week that the user selected
     * 2. If it is the prefer time the user selected
     * 3. If it is raining currently
     * 4. If it is the risk for driver is low
     * 5. If the notification has already been triggered within the last six hours
     */
    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        Log.d(TAG, "intent service work yeah");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //String intentString = intent.getStringExtra("testIntent");
            ArrayList<Integer> weekDay = intent.getIntegerArrayListExtra("week");
            ArrayList<Integer> dayTime = intent.getIntegerArrayListExtra("day");
            db = DrivingRecordDatabase.getInstance(this);
            calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            long currentTime = System.currentTimeMillis();
            boolean isTime = true;
            if(!weekDay.contains((Integer) dayOfWeek)) {
                isTime = false;
            }
            loadData();
            if(currentTime - lastMill < 6 * 60 * 60 * 1000 && lastMill != currentTime && lastMill != 0) isTime = false;

            if(dayTime.size() == 1){
                if(dayTime.get(0) == 1){
                    if(timeOfDay < 7 || timeOfDay > 19) isTime = false;
                }else{
                    if(timeOfDay >= 7 && timeOfDay <= 19) isTime = false;
                }
            }

            if(isTime){
                List<DrivingRecord> records = db.drivingRecordDAO().getAllRecords();
                for(DrivingRecord record : records){
                    totalHours += record.getRecordMins();
                }
                String currentWeather = MainActivity.weatherAPI.getCurrentWeatherByCityName("Melbourne,AU-VIC,AUS");
                HashMap<String, String> weatherMap = JsonConverter.currentWeather(currentWeather);
                if (weatherMap.get("isRain").equals("true")) {
//                if(true){
                    double rainfallIntensity = Float.parseFloat(weatherMap.get("rainmm"));
                    if(RiskConverter.riskLevel(rainfallIntensity, totalHours)){
//                    if(true){
                        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.baseline_sentiment_satisfied_black_18)
                                .setContentTitle("Good Time to Practice!")
                                .setContentText("Current risk is low and suitable for practicing")
                                //.setContentIntent(pendingIntent)
                                .build();

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        notificationManager.notify(200, notification);
                    }
                    saveData();
                }
            }

        }
    }

    /**
     * This is the method use shared preference to record
     * the last time that the notification is shown
     * if the notification is shown, the mill will be recorded
     * and the next time the notification will show is six hours later
     */
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(TEXT, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * This is the method use shared preference to load
     * the last time that the notification is shown
     */
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        lastMill = sharedPreferences.getLong(TEXT, 0);
        //System.out.println("load data is triggered " + lastMill);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }



}
