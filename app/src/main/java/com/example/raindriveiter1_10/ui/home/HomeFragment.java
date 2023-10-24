package com.example.raindriveiter1_10.ui.home;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.model.DrivingRecord;
import com.example.raindriveiter1_10.ui.SuitabilityIndicator.SuitabilityIndicator;
import com.example.raindriveiter1_10.utility.JsonConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayCircle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import nl.joery.animatedbottombar.AnimatedBottomBar;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    private AnimatedBottomBar animatedBottomBar;
    private TextView tvLocation;
    private ImageView weatherIcon;
    private TextView tvTemp;
    private TextView tvWeatherDesc;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Button btnGo;
    private TextView tvDayHrs;
    private ProgressBar pbDayHrs;
    private TextView tvNightHrs;

    private TextView tvLevel;
    private ProgressBar pbLevel;
    private ImageSlider imageSlider;
    private ConstraintLayout clDayProgress;
    private ConstraintLayout clNightProgress;
    private ConstraintLayout clLevelProgress;


    public HomeFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainActivity.toolbar.setTitle("Home");
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return view;
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    MainActivity.latitude = String.valueOf(location.getLatitude());
                    MainActivity.longitude = String.valueOf(location.getLongitude());
                    new GetWeatherAndLocationInfoTask().execute(MainActivity.latitude, MainActivity.longitude);

                    tvLocation = view.findViewById(R.id.tv_header_location);
                    tvTemp = view.findViewById(R.id.tv_header_temp);
                    tvWeatherDesc = view.findViewById(R.id.tv_header_weather_des);
                    weatherIcon = view.findViewById(R.id.iv_weather_icon);


                }
            }
        });
        btnGo = view.findViewById(R.id.btn_go);
        btnGo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.custom_button));
        tvDayHrs = view.findViewById(R.id.tv_day_hour_drived);
        tvNightHrs = view.findViewById(R.id.tv_speed);
        imageSlider = view.findViewById(R.id.is_hint);
        clDayProgress = view.findViewById(R.id.cl_day_progress);
        clNightProgress = view.findViewById(R.id.cl_night_progress);
        clLevelProgress = view.findViewById(R.id.cl_level_progress);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.quicktup_1));
        slideModels.add(new SlideModel(R.drawable.quicktup_2));
        slideModels.add(new SlideModel(R.drawable.quicktup_3));
        slideModels.add(new SlideModel(R.drawable.quicktup_4));
        imageSlider.setImageList(slideModels, true);
        tvLevel = view.findViewById(R.id.tv_risk);
        pbDayHrs = view.findViewById(R.id.progress_circular_day_drived);
        final ProgressBar pbNightHrs = view.findViewById(R.id.progress_circular_speed);
        pbLevel = view.findViewById(R.id.progress_circular_risk);
        animatedBottomBar = getActivity().findViewById(R.id.animated_bottom_bar);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatedBottomBar.selectTabById(R.id.check_suitability, true);
            }
        });
        //pbDayHrs.setProgress(50);

        MainActivity.drivingRecordViewModel.getAllDrivingRecords().observe(getViewLifecycleOwner(), new Observer<List<DrivingRecord>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(List<DrivingRecord> drivingRecords) {
//                if (drivingRecords.isEmpty()) {
//                    initTestData();
//                }
                Float dayHrs = 0.0f;
                Float nightHrs = 0.0f;
                //Float dist = 0.0f;
                for (DrivingRecord drivingRecord : drivingRecords) {
                    if (drivingRecord.isAtNight()) {
                        nightHrs += drivingRecord.getRecordMins() / 60.0f;
                    } else {
                        dayHrs += drivingRecord.getRecordMins() / 60.0f;
                    }
                    //dist += drivingRecord.getDrivingDistance();
                }
                int progressDay = (int) (dayHrs * 100 / 100);
                if (progressDay == 0 && dayHrs != 0.0f)
                    progressDay = 1;
                else if (dayHrs == 0.0f)
                    progressDay = 0;
                pbDayHrs.setProgress(progressDay);
                //int progressNight = (int) ((nightHrs / 120.0) * 100);
                int progressNight = (int) (nightHrs * 100 / 20);
                if (progressNight == 0 && nightHrs != 0.0f)
                    progressNight = 1;
                else if (nightHrs == 0.0f)
                    progressNight = 0;
                pbNightHrs.setProgress(progressNight);
                int level = SuitabilityIndicator.calculateLevel(dayHrs + nightHrs);
                int levelPercent = level * 100 / 5;
                //int progressLevel = (int) (level/5);
//                if (progressLevel == 0 && dist != 0.0f)
//                    progressLevel = 1;
                pbLevel.setProgress(levelPercent);
                tvDayHrs.setText(String.format("%.1f", dayHrs));
                tvNightHrs.setText(String.format("%.1f", nightHrs));
                tvLevel.setText(String.valueOf(level));
            }
        });
        if (!restorePrefDataHomeBalloon())
            setUpBalloons();
        return view;

    }


    private class GetWeatherAndLocationInfoTask extends AsyncTask<String, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            String weather = MainActivity.weatherAPI.getCurrentWeatherByCityName("Melbourne,AU-VIC,AUS");
            HashMap<String, String> resultMap = JsonConverter.currentWeather(weather);
            String location = MainActivity.googleAPI.getLocalNameByLatLng(params[0], params[1]);
            resultMap.put("location", location);
            return resultMap;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(HashMap<String, String> resultMap) {
            HashMap<String, String> locationMap = JsonConverter.locationDetail(resultMap.get("location"));
            if (locationMap.get("locality") != null && resultMap.get("temp") != null) {
                tvLocation.setText(locationMap.get("locality"));
                tvTemp.setText(String.format("%.1f", Double.parseDouble(Objects.requireNonNull(resultMap.get("temp"))) - 273.15) + "°C");
                String icon = resultMap.get("icon");
                MainActivity.weatherIconStr = icon;
                String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";
                tvWeatherDesc.setText(resultMap.get("main"));
                Picasso.get().load(iconUrl).into(weatherIcon);
            }
        }
    }

    private void setUpBalloons() {
        btnGo.requestFocus();

        final Balloon balloonWelcome = new Balloon.Builder(getContext())
                .setArrowVisible(false)
                .setWidthRatio(1f)
                .setMargin(5)
                .setPadding(10)
                .setTextSize(20f)
                .setCornerRadius(16f)
                .setShowTime(1)
                .setAlpha(0.9f)
                .setText("Welcome to RainDrive, let's take a closer look at the Home page.")
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

        final Balloon balloonDayProgress = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.7f)
                .setPadding(10)
                .setTextSize(17f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("The number of hours driven at day is shown here. The goal is to complete 100 hours.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
//                .setIsVisibleOverlay(true)
//                .setOverlayColorResource(R.color.overlay)
//                .setOverlayPadding(6f)
//                .setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
//                .setOverlayShape(BalloonOverlayRect.INSTANCE)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        final Balloon balloonNightProgress = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.7f)
                .setPadding(10)
                .setTextSize(17f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("The number of hours driven at night is shown here. The goal is to complete 20 hours.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))

//                .setIsVisibleOverlay(true)
//                .setOverlayColorResource(R.color.overlay)
//                .setOverlayPadding(6f)
//                .setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
//                .setOverlayShape(BalloonOverlayRect.INSTANCE)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        final Balloon balloonLevelProgress = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.7f)
                .setPadding(10)
                .setTextSize(17f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("You can know the level of your learner driver. Levels are divided based on the driving hours.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        final Balloon balloonCheckSuitability = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.7f)
                .setPadding(10)
                .setTextSize(17f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("Click here to check if it is a suitable time to train your learner driver during rainfall")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        balloonWelcome
                .relayShowAlignTop(balloonDayProgress, clDayProgress)
                .relayShowAlignTop(balloonNightProgress, clNightProgress)
                .relayShowAlignTop(balloonLevelProgress, clLevelProgress)
                .relayShowAlignTop(balloonCheckSuitability, btnGo);


        balloonWelcome.show(getActivity().getWindow().getDecorView().getRootView());
        savePrefsDataHomeBalloon();

    }

    private boolean restorePrefDataHomeBalloon() {
        SharedPreferences pref = getContext().getSharedPreferences("homePrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isHomeBalloonOpened", false);
        return isIntroActivityOpnendBefore;
    }


    private void savePrefsDataHomeBalloon() {
        SharedPreferences pref = getContext().getSharedPreferences("homePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isHomeBalloonOpened", true);
        editor.commit();
    }

}


