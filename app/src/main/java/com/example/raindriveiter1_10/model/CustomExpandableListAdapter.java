package com.example.raindriveiter1_10.model;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.raindriveiter1_10.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<DrivingRecord>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<DrivingRecord>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public DrivingRecord getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final DrivingRecord drivingRecord = getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView tvDateDuration = convertView.findViewById(R.id.tv_date_duration);
        TextView tvTimePeriod = convertView.findViewById(R.id.tv_time_period);
        ImageView ivRecordDayNight = convertView.findViewById(R.id.iv_record_day_night);
        ImageView ivWeather = convertView.findViewById(R.id.iv_weather_icon_item);
        TextView tvDistancePlace = convertView.findViewById(R.id.tv_distance_place);

        if (drivingRecord.isAtNight()) {
            ivRecordDayNight.setImageResource(R.drawable.ic_moon);
        } else {
            ivRecordDayNight.setImageResource(R.drawable.ic_sun);
        }
        int recordMins =  drivingRecord.getRecordMins();
        String date = null;
        try {
            date = new SimpleDateFormat("MMM dd", Locale.ENGLISH).format(new SimpleDateFormat("dd/MM/yyyy").parse(drivingRecord.getDrivingDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvDateDuration.setText(date + ", " + recordMins + " min");

        String startTime = null;
        String endTime = null;

        try {
            startTime = new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm:ss").parse(drivingRecord.getDrivingStartTime()));
            endTime = new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm:ss").parse(drivingRecord.getDrivingEndTime()));

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        tvTimePeriod.setText(startTime + " - "+ endTime);

        String iconUrl = "http://openweathermap.org/img/w/" + drivingRecord.getIconStr() + ".png";
        Picasso.get().load(iconUrl).into(ivWeather);
        tvDistancePlace.setText(String.format("%.1f", drivingRecord.getDrivingDistance()) + " km, "
        + drivingRecord.getStartLocation() + " to " + drivingRecord.getEndLocation());



        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(layoutInflater).inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
