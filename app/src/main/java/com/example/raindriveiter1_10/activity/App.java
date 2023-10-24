package com.example.raindriveiter1_10.activity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * This class is used to create the notification channel
 * for the notification
 */
public class App extends Application {

    public static final String CHANNEL_ID = "exampleServiceChannel";

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID
                    , "Example service channel"
                    , NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

    }
}
