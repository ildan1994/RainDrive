package com.example.raindriveiter1_10.ui.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.raindriveiter1_10.R;

import java.util.ArrayList;

/**
 * This is the class used to show the customized dialog which
 * allow user to select his or her preferred day or time
 * of the notification
 */
public class NotificationAlertDialog extends AppCompatDialogFragment {

    private static final String TAG = "MyCustomDialog";

    private CheckBox monCheck, tueCheck, wedCheck, thurCheck, friCheck, satCheck, sunCheck, dayCheck, nightCheck;
    ArrayList<Integer> weekList;
    ArrayList<Integer> timeList;

    /**
     * The interface to send data back
     * from the dialog to the more fragment
     */
    public interface OnInputSelected{
        void sendInput(ArrayList<Integer> weekList, ArrayList<Integer> timeList);
    }

    public OnInputSelected onInputSelected;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.notification_alert_layout, null);
        monCheck = view.findViewById(R.id.check_mon);
        tueCheck = view.findViewById(R.id.check_tue);
        wedCheck = view.findViewById(R.id.check_wed);
        thurCheck = view.findViewById(R.id.check_thur);
        friCheck = view.findViewById(R.id.check_fri);
        satCheck = view.findViewById(R.id.check_sat);
        sunCheck = view.findViewById(R.id.check_sun);
        dayCheck = view.findViewById(R.id.check_day);
        nightCheck = view.findViewById(R.id.check_night);


        weekList = new ArrayList<>();
        timeList = new ArrayList<>();
        monCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monCheck.isChecked()){
                    weekList.add(2);
                }else {
                    weekList.remove((Integer)2);
                }
            }
        });

        tueCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tueCheck.isChecked()){
                    weekList.add(3);
                }else {
                    weekList.remove((Integer)3);
                }
            }
        });

        wedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wedCheck.isChecked()){
                    weekList.add(4);
                }else {
                    weekList.remove((Integer)4);
                }
            }
        });

        thurCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thurCheck.isChecked()){
                    weekList.add(5);
                }else {
                    weekList.remove((Integer) 5);
                }
            }
        });

        friCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friCheck.isChecked()){
                    weekList.add(6);
                }else {
                    weekList.remove((Integer) 6);
                }
            }
        });

        satCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(satCheck.isChecked()){
                    weekList.add(7);
                }else {
                    weekList.remove((Integer)7);
                }
            }
        });

        sunCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sunCheck.isChecked()){
                    weekList.add(1);
                }else {
                    weekList.remove((Integer)1);
                }
            }
        });

        dayCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dayCheck.isChecked()){
                    timeList.add(1);
                }else{
                    timeList.remove((Integer) 1);
                }
            }
        });

        nightCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nightCheck.isChecked()){
                    timeList.add(2);
                }else {
                    timeList.remove((Integer) 2);
                }
            }
        });

        builder.setView(view)
                .setTitle("Set Notification")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onInputSelected.sendInput(weekList, timeList);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<Integer> temp = new ArrayList<>();
                        onInputSelected.sendInput(temp, timeList);
                    }
        }).setCancelable(false);

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            onInputSelected = (OnInputSelected) context;
        }catch (ClassCastException e){
            Log.e(TAG, e.getMessage());
        }
    }
}
