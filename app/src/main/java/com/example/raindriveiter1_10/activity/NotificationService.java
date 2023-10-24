package com.example.raindriveiter1_10.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.raindriveiter1_10.R;

import static com.example.raindriveiter1_10.activity.App.CHANNEL_ID;

/**
 * The service used to create notification
 * but this class is deprecated
 */
public class NotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.splash)
                .setContentTitle("Test Notification")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //return super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
