package com.example.raindriveiter1_10.ui.DrivingLogBook;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.model.CustomExpandableListAdapter;
import com.example.raindriveiter1_10.model.DrivingRecord;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayCircle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class DrivingRecordFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private LinkedHashMap<String, List<DrivingRecord>> expandableListDetail;
    private TextView tvTripNo;
    private TextView tvDayHrs;
    private TextView tvNightHrs;
    private TextView tvRainHrs;
    private TextView tvTotalHrs;


    public DrivingRecordFragment() {

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driving_record, container, false);
        //initTestData();
        MainActivity.toolbar.setTitle("Driving Log Book");
        tvTripNo = view.findViewById(R.id.tv_trip_no);
        tvRainHrs = view.findViewById(R.id.tv_rain_hrs);
        tvDayHrs = view.findViewById(R.id.tv_day_hrs);
        tvNightHrs = view.findViewById(R.id.tv_night_hrs);
        tvTotalHrs = view.findViewById(R.id.tv_total_hrs);
        expandableListView = view.findViewById(R.id.expandableListView);
        MainActivity.drivingRecordViewModel.getAllDrivingRecords().observe(getViewLifecycleOwner(), new Observer<List<DrivingRecord>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<DrivingRecord> drivingRecords) {

//                if (drivingRecords.size() == 0) {
//                    initTestData();
//                }

                organizeData(drivingRecords);
                expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);
                tvTripNo.setText(String.valueOf(drivingRecords.size()));
                int totalRecordsDayMin = 0;
                int totalRecordsNightMin = 0;
                int totalRainMin = 0;
                for (DrivingRecord drivingRecord : drivingRecords) {
                    if (drivingRecord.isAtNight())
                        totalRecordsNightMin += drivingRecord.getRecordMins();
                    else
                        totalRecordsDayMin += drivingRecord.getRecordMins();
                    if (drivingRecord.isRainDay()) {
                        totalRainMin += drivingRecord.getRecordMins();
                    }
                }
                tvRainHrs.setText(convertMinToHourMin(totalRainMin));
                tvDayHrs.setText(convertMinToHourMin(totalRecordsDayMin));
                tvNightHrs.setText(convertMinToHourMin(totalRecordsNightMin));
                tvTotalHrs.setText(convertMinToHourMin(totalRecordsNightMin + totalRecordsDayMin));
                int count = expandableListAdapter.getGroupCount();
                for (int i = 1; i <= count; i++) {
                    expandableListView.expandGroup(i - 1);
                }
            }
        });


        //expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        //expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
        //expandableListView.setAdapter(expandableListAdapter);


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getContext(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getContext(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(childPosition),
//                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        if (!restorePrefDataDrivingRecordBalloon()) {
            setUpBalloons();
        }
        return view;
    }

    static public void initTestData() {
        //MainActivity.drivingRecordViewModel.deleteAll();
        List<DrivingRecord> drivingRecords = new ArrayList<>();
        drivingRecords.add(new DrivingRecord(
                "2/10/2019",
                "21:15:31",
                "22:22:31",
                2.2f,
                "3.0",
                "Clayton",
                "Caufield",
                "10n"));

        drivingRecords.add(new DrivingRecord(
                "4/10/2019",
                "14:19:31",
                "16:22:31",
                2.3f,
                "3.0",
                "Toorak",
                "Malvern",
                "01d"));

        drivingRecords.add(new DrivingRecord(
                "4/11/2019",
                "10:19:31",
                "12:22:31",
                2.4f,
                "3.0",
                "Clayton",
                "Caufield",
                "04d"));

        drivingRecords.add(new DrivingRecord(
                "6/11/2019",
                "16:19:31",
                "18:22:31",
                2.3f,
                "3.0",
                "Glen Iris",
                "Clifton Hill",
                "10d"));

        drivingRecords.add(new DrivingRecord(
                "5/12/2019",
                "16:19:31",
                "18:22:31",
                2.5f,
                "3.0",
                "Kew East",
                "Camberwell",
                "09d"));
        //organizeData(drivingRecords);
        for (DrivingRecord drivingRecord : drivingRecords) {
            MainActivity.drivingRecordViewModel.insert(drivingRecord);
        }


    }

    private void organizeData(List<DrivingRecord> drivingRecords) {
        expandableListDetail = new LinkedHashMap<>();
        for (int i = 0; i < drivingRecords.size(); i++) {
            List<DrivingRecord> listItems = new ArrayList<>();
            listItems.add(drivingRecords.get(i));
            int j = 0;
            for (j = i + 1; j < drivingRecords.size(); j++) {
                if (parseToDateTitle(drivingRecords.get(j).getDrivingDate())
                        .equals(parseToDateTitle(drivingRecords.get(i).getDrivingDate()))) {
                    listItems.add(drivingRecords.get(j));
                } else {
                    i = j - 1;
                    break;
                }
            }
            expandableListDetail.put(parseToDateTitle(drivingRecords.get(i).getDrivingDate()), listItems);
            if (j == drivingRecords.size())
                break;
        }
    }

    private String parseToDateTitle(String date) {
        String dateTitle = null;
        try {
            dateTitle = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
                    .format(new SimpleDateFormat("dd/MM/yyyy").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTitle;
    }

    private String convertMinToHourMin(int totalMin) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");
        Date date;
        String HourMinStr = null;
        try {
            date = simpleDateFormat.parse(String.valueOf(totalMin));
            HourMinStr = new SimpleDateFormat("H:mm").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm:ss").parse(drivingRecord.getDrivingEndTime()))
        return HourMinStr;
    }

    private void setUpBalloons() {

        final Balloon balloonWelcome = new Balloon.Builder(getContext())
                .setArrowVisible(false)
                .setWidthRatio(1f)
                .setMargin(5)
                .setPadding(10)
                .setTextSize(20f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("This is the logbook page which shows your trip history. there are no records yet.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .setIsVisibleOverlay(true)
                .setOverlayColorResource(R.color.overlay)
                .setOverlayPadding(6f)
                .setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
                .setOverlayShape(new BalloonOverlayCircle(0))
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();


        balloonWelcome.show(getActivity().getWindow().getDecorView().getRootView());

//        if (!MainActivity.hasPlayedHomeBal) {
//
//            MainActivity.hasPlayedHomeBal = true;
//        }
        savePrefsDataDrivingRecordBalloon();

    }

    private boolean restorePrefDataDrivingRecordBalloon() {
        SharedPreferences pref = getContext().getSharedPreferences("DrivingRecordPrefs", MODE_PRIVATE);
        Boolean isDrivingRecordOpnendBefore = pref.getBoolean("isDrivingRecordBalloonOpened", false);
        return isDrivingRecordOpnendBefore;
    }


    private void savePrefsDataDrivingRecordBalloon() {
        SharedPreferences pref = getContext().getSharedPreferences("DrivingRecordPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isDrivingRecordBalloonOpened", true);
        editor.commit();
    }
}