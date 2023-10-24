package com.example.raindriveiter1_10.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.ui.widget.NotificationAlertDialog;
import com.example.raindriveiter1_10.utility.NotificationIntentService;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;


public class NotificationActivity extends AppCompatActivity implements NotificationAlertDialog.OnInputSelected {

    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pendingIntent;
    private SwitchMaterial notificationSwitch;
    private TextView notificationText;
    private Toolbar toolbar;

    public static final String SHARED_PREF = "notification_status";
    public static final String TEXT = "notification_time";
    public static final String SWITCH = "notification_on";
    public static final String PREF_DAY = "prefer_day";
    public static final String PREF_TIME = "pref_time";
    private StringBuffer pref_day;
    private StringBuffer pref_time;

    /**
     * This is the call back method that will be invoked
     * every time the Activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        setContentView(R.layout.activity_notification);
        toolbar = findViewById(R.id.toolbar_notification);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        notificationSwitch = findViewById(R.id.sw_notification);
        notificationText = findViewById(R.id.notification_tv);
        pref_time = new StringBuffer();
        pref_day = new StringBuffer();

        loadData();
        if(notificationSwitch.isChecked()){
            if(pref_day.length() > 0){
                String[] week = pref_day.toString().split(",");
                String[] time = pref_time.toString().split(",");
                ArrayList<Integer> weekList = new ArrayList<>();
                ArrayList<Integer> timeList = new ArrayList<>();

                for(String s : week){
                    weekList.add(Integer.parseInt(s));
                }
                for(String s : time){
                    timeList.add(Integer.parseInt(s));
                }
                intent = new Intent(this, NotificationIntentService.class);
                intent.putExtra("week", weekList);
                intent.putExtra("day", timeList);
                //cancelNotificationIfExist(getContext(), 0, intent);
                pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                long currentTime = System.currentTimeMillis();
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime, 1000 * 60 * 5, pendingIntent);
            }
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isPressed()){return;}
                if(isChecked){
                    NotificationAlertDialog notificationAlertDialog = new NotificationAlertDialog();
                    //notificationAlertDialog.setTargetFragment(NotificationFragment.this, 1);
                    notificationAlertDialog.show(getSupportFragmentManager(), "Notification Dialog");
                }else{
                    Intent intent2 = new Intent(NotificationActivity.this, NotificationIntentService.class);
                    cancelNotificationIfExist(getApplicationContext(), 0, intent2);
                    notificationText.setVisibility(View.INVISIBLE);
                    notificationText.setText("");

                    SharedPreferences sharedPreferences = getSharedPreferences("last_time", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("last_mills", 0);
                    editor.apply();
                    saveData();
                }
            }
        });
    }

    @SuppressLint("ShortAlarm")
    @Override
    public void sendInput(ArrayList<Integer> weekList, ArrayList<Integer> timeList) {
        if(weekList.size() == 0 || timeList.size() == 0){
            notificationSwitch.setChecked(false);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("You need to select both prefer day and prefer time")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            dialog.show();
        }
        else{
            notificationText.setVisibility(View.VISIBLE);
            StringBuffer text = new StringBuffer("Your notification is On ");
            if(weekList.contains((Integer) 2)) text.append("Monday, ");
            if(weekList.contains((Integer) 3)) text.append("Tuesday, ");
            if(weekList.contains((Integer) 4)) text.append("Wednesday, ");
            if(weekList.contains((Integer) 5)) text.append("Thursday, ");
            if(weekList.contains((Integer) 6)) text.append("Friday, ");
            if(weekList.contains((Integer) 7)) text.append("Saturday, ");
            if(weekList.contains((Integer) 1)) text.append("Sunday ");

            if(timeList.size() == 2){
                text.append(" in all day time");
            }
            else {
                text.append("in the ");
                if(timeList.contains((Integer) 1))  text.append("day time.");
                else if(timeList.contains((Integer) 2)) text.append("night time.");
            }
            notificationText.setText(text);
            for(Integer num : weekList){
                pref_day.append(num);
                pref_day.append(",");
            }
            pref_day.deleteCharAt(pref_day.length() - 1);
            for(Integer num: timeList){
                pref_time.append(num);
                pref_time.append(",");
            }
            pref_time.deleteCharAt(pref_time.length() - 1);
            saveData();

            intent = new Intent(this, NotificationIntentService.class);
            intent.putExtra("week", weekList);
            intent.putExtra("day", timeList);
            //cancelNotificationIfExist(getContext(), 0, intent);
            pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            long currentTime = System.currentTimeMillis();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime, 1000 * 60 * 5, pendingIntent);
        }
    }

    /**
     * This method encapsulate the shared preference method
     * to save the status of the users setting
     */
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, notificationText.getText().toString());
        editor.putBoolean(SWITCH, notificationSwitch.isChecked());
        editor.putString(PREF_DAY, pref_day.toString());
        editor.putString(PREF_TIME, pref_time.toString());
        editor.apply();
    }

    /**
     * This method is used to load data from shared preference every time the fragment
     * is shown on the screen
     */
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        notificationText.setText(sharedPreferences.getString(TEXT, ""));
        notificationSwitch.setChecked(sharedPreferences.getBoolean(SWITCH, false));
        pref_time = pref_time.append(sharedPreferences.getString(PREF_TIME, ""));
        pref_day = pref_day.append(sharedPreferences.getString(PREF_DAY, ""));
    }

    /**
     * This method is used to cancel the notification
     * if the switch is turned off by user
     */
    private void cancelNotificationIfExist(Context context, int requestCode, Intent intent){
        try {
            PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}