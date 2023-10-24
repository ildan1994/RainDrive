package com.example.raindriveiter1_10.utility;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.raindriveiter1_10.R;

import static com.example.raindriveiter1_10.activity.App.CHANNEL_ID;

public class ReminderBroadcast extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        final PendingResult pendingResult = goAsync();



        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.splash)
                .setContentTitle("Test Notification")
                .setContentText("Test Notification text")
                //.setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, notification);

    }
}
