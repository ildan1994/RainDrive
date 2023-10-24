package com.example.raindriveiter1_10.ui.Settings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.ui.widget.NotificationAlertDialog;
import com.example.raindriveiter1_10.utility.NotificationIntentService;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;



public class NotificationFragment extends Fragment implements NotificationAlertDialog.OnInputSelected {

    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pendingIntent;
    private SwitchMaterial notificationSwitch;
    private TextView notificationText;

    public static final String SHARED_PREF = "notification_status";
    public static final String TEXT = "notification_time";
    public static final String SWITCH = "notification_on";

    public NotificationFragment() {}

    /**
     * This method is the method inherit from the interface of onSelected.
     * This method is used to transfer data from the customized alert dialog
     * @param weekList the parameter that store the prefer days of a week of a user
     * @param timeList the parameter that store the prefer times of a day of a user
     */
    @Override
    public void sendInput(ArrayList<Integer> weekList, ArrayList<Integer> timeList) {
        if(weekList.size() == 0 || timeList.size() == 0){
            notificationSwitch.setChecked(false);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
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

            saveData();

            intent = new Intent(getActivity(), NotificationIntentService.class);
            intent.putExtra("week", weekList);
            intent.putExtra("day", timeList);
            //cancelNotificationIfExist(getContext(), 0, intent);
            pendingIntent = PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            long currentTime = System.currentTimeMillis();
            long intervals = 10000;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime, 1000 * 60 * 5, pendingIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This is the call back method that will be invoked
     * every time the fragment is created
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_notification, container, false);
        MainActivity.toolbar.setTitle("More");

        notificationSwitch = root.findViewById(R.id.sw_notification);
        notificationText = root.findViewById(R.id.notification_tv);

        loadData();

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isPressed()){return;}
                if(isChecked){
                    NotificationAlertDialog notificationAlertDialog = new NotificationAlertDialog();
                    notificationAlertDialog.setTargetFragment(NotificationFragment.this, 1);
                    notificationAlertDialog.show(getActivity().getSupportFragmentManager(), "Notification Dialog");
                }else{
                    Intent intent2 = new Intent(getActivity(), NotificationIntentService.class);
                    cancelNotificationIfExist(getContext(), 0, intent2);
                    notificationText.setVisibility(View.INVISIBLE);
                    notificationText.setText("");

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("last_time", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("last_mills", 0);
                    editor.apply();
                    saveData();
                }
            }
        });

        return root;
    }

    /**
     * This method encapsulate the shared preference method
     * to save the status of the users setting
     */
    public void saveData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, notificationText.getText().toString());
        editor.putBoolean(SWITCH, notificationSwitch.isChecked());
        editor.apply();
    }

    /**
     * This method is used to load data from shared preference every time the fragment
     * is shown on the screen
     */
    public void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        notificationText.setText(sharedPreferences.getString(TEXT, ""));
        notificationSwitch.setChecked(sharedPreferences.getBoolean(SWITCH, false));
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

    private void updateNotification(){

    }
}