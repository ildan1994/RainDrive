package com.example.raindriveiter1_10.ui.SuitabilityIndicator;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.model.DrivingRecord;
import com.example.raindriveiter1_10.networkConnection.BackendAPI;
import com.example.raindriveiter1_10.networkConnection.GoogleAPI;
import com.example.raindriveiter1_10.networkConnection.WeatherAPI;
import com.example.raindriveiter1_10.ui.Map.AccidentMap;
import com.example.raindriveiter1_10.ui.widget.DashBoard;
import com.example.raindriveiter1_10.utility.JsonConverter;
import com.example.raindriveiter1_10.utility.Transformer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayCircle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * The fragment used to show the suitability fragment with the indicator
 */
public class SuitabilityIndicator extends Fragment {

    //TrafficAPI trafficAPI = null;
    private WeatherAPI weatherAPI = null;
    private BackendAPI backendAPI = null;
    private GoogleAPI googleAPI = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private DashBoard d;
    private MaterialCardView materialCardViewCur;
    private MaterialCardView materialCardViewSuggest;
    private TextView tvLocation;
    private TextView tvWeatherDesc;
    private TextView tvPrecipitation;
    private TextView tvRiskLevel;
    private TextView tvDriverLevel;
    private TextView tvAccidentRate;
    private TextView tvSuggestDriverLv;
    private TextView tvGoOutSuggest;
    private TextView accidentRateText;
    private Balloon balloonCurSituation;
    private ImageView ivFaceSentiment;
    private Button btnGoToMap;


    private int[] goalTime;
    static public String riskLevel;
    private int goalDistance;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Dialog explanationDialog;

    private FloatingActionButton hintfloatingActionButton;
    private Location mCurrentLocation = null;
    private double totalHours = 0;
    private int totalMins;
    private int driverLevel = 0;
    private Balloon balloonSuggestion;
    private Balloon balloonDashBoard;
    //private static boolean canAlarm = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_suitability_indicator, container, false);
        MainActivity.toolbar.setTitle("Risk Indicator");
//        locationEnable();
        MainActivity.drivingRecordViewModel.getAllDrivingRecords().observe(getViewLifecycleOwner(), new Observer<List<DrivingRecord>>() {
            @Override
            public void onChanged(List<DrivingRecord> drivingRecords) {
                Log.i(TAG, "room database onChanged: ");

                for (DrivingRecord drivingRecord : drivingRecords) {
                    int recordMins = drivingRecord.getRecordMins();
                    totalMins += recordMins;
                }
                totalHours = totalMins / 60;
                driverLevel = calculateLevel(totalHours);
            }
        });
        d = root.findViewById(R.id.dash);


        // To get the last known location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        swipeRefreshLayout = root.findViewById(R.id.srl_suitability);
        tvWeatherDesc = root.findViewById(R.id.tv_weather_desc);
        tvPrecipitation = root.findViewById(R.id.tv_precipitation);
        tvRiskLevel = root.findViewById(R.id.tv_risk_level);
        tvDriverLevel = root.findViewById(R.id.tv_driver_level);
        tvAccidentRate = root.findViewById(R.id.tv_accident_rate);
        tvSuggestDriverLv = root.findViewById(R.id.tv_suggest_driver_lv);
        tvGoOutSuggest = root.findViewById(R.id.tv_suggest_goOut);
        ivFaceSentiment = root.findViewById(R.id.iv_face_sentiment);
        materialCardViewCur = root.findViewById(R.id.cv_cur_situation);
        materialCardViewSuggest = root.findViewById(R.id.cv_suggestion);
        btnGoToMap = root.findViewById(R.id.btn_go_to_map);
        weatherAPI = new WeatherAPI();
        backendAPI = new BackendAPI();
        googleAPI = new GoogleAPI();
        tvLocation = root.findViewById(R.id.tv_location);
        explanationDialog = new Dialog(getContext());
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.i(TAG, "onLocationResult: null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                }
            }
        };


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //If mCurrentLocation is null, there will be a alert dialog
                if (mCurrentLocation != null) {
                    String longitude = String.valueOf(mCurrentLocation.getLongitude());
                    String latitude = String.valueOf(mCurrentLocation.getLatitude());

                    WeatherTestTask weatherTestTask = new WeatherTestTask();
                    weatherTestTask.execute(longitude, latitude);


                } else {
                    getUserPermissions();
                    startLocationUpdates();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2 * 1000);
            }
        });

        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),
                        R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.layout_bottom_sheet,
                                (LinearLayout) root.findViewById(R.id.bottomSheetContainer)
                        );
                Button btnContinueToMap = bottomSheetView.findViewById(R.id.btn_go_to_map_page);
                btnContinueToMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationEnable();
                        Intent intent = new Intent(getActivity(), AccidentMap.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        bottomSheetDialog.dismiss();
                        getActivity().startActivityForResult(intent, 4);
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return root;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = location;
                    Log.i(TAG, "onSuccess: Get last location success");
                    WeatherTestTask weatherTestTask = new WeatherTestTask();
                    String longitude = String.valueOf(mCurrentLocation.getLongitude());
                    String latitude = String.valueOf(mCurrentLocation.getLatitude());
                    weatherTestTask.execute(longitude, latitude);
                }
            }

        });
        if (!restorePrefDataSuitBalloon()) {
            setUpBalloons();
        }
        return root;
    }

    //The async task to call backend api while the view is created and the button is clicked
    private class WeatherTestTask extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            //startLocationUpdates();


            String weather = weatherAPI.getCurrentWeatherByCityName("Melbourne,AU-VIC,AUS");
            //trafficAPI.getTrafficIncidentByLocation(params[0], params[1]);
            //trafficAPI.getTrafficByLocation(params[0], params[1]);
            String location = googleAPI.getLocalNameByLatLng(params[1], params[0]);

            HashMap<String, String> resultMap = JsonConverter.currentWeather(weather);
            String accidentRateResult = "";
            if (resultMap.get("isRain").equals("true")) {
                int rainfallIntensity = Transformer.rainfallTransform(Float.parseFloat(resultMap.get("rainmm")));
                resultMap.put("Rainfall", Integer.toString(rainfallIntensity));
                String accidentRateResult1 = backendAPI.getAccidentRateByRainfallIntensity(rainfallIntensity);
                float[] temp = JsonConverter.accidentRate(accidentRateResult1);
                accidentRateResult = String.valueOf(temp[1]);

            } else {
                accidentRateResult = "5.01707";
                resultMap.put("Rainfall", "0");
            }
            resultMap.put("accident", accidentRateResult);

            HashMap<String, String> locationMap = JsonConverter.locationDetail(location);
            String locality = locationMap.get("locality");

            resultMap.put("locality", locality);


            return resultMap;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {

//            description.setText(result.get("description"));
//            latitudeText.setText(result.get("locality"));
            tvLocation.setText(result.get("locality"));
            tvWeatherDesc.setText(result.get("description"));
            if (result.get("isRain").equals("true")) {
                tvPrecipitation.setText(result.get("rainmm"));
                tvAccidentRate.setText(result.get("accident").substring(0, 4) + "%");
            }

            tvDriverLevel.setText("Level " + driverLevel);

            String riskLevel = getRiskLevel(result.get("Rainfall"));
            tvRiskLevel.setText(riskLevel);
            if (isAdded())
                setUpSuggestion(driverLevel, riskLevel);

        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (!(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);

    }

    private void getUserPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(permissions, 0);
            }
        }
    }


    public static int calculateLevel(double driverHrs) {
        int driverLevel = 0;
        if (driverHrs >= 0 && driverHrs < 30) {
            driverLevel = 1;
        } else if (driverHrs >= 30 && driverHrs < 60) {
            driverLevel = 2;
        } else if (driverHrs >= 60 && driverHrs < 90) {
            driverLevel = 3;
        } else if (driverHrs >= 90 && driverHrs < 120) {
            driverLevel = 4;
        } else if (driverHrs >= 120) {
            driverLevel = 5;
        }
        return driverLevel;
    }

    private String getRiskLevel(String rainfallstr) {
        String riskLevelstr = "";
        int rainfallInt = Integer.parseInt(rainfallstr);
        if (rainfallInt < 1) riskLevelstr = "Low";
        else if (rainfallInt < 10) riskLevelstr = "Medium";
        else if (rainfallInt < 20) riskLevelstr = "High";
        else if (rainfallInt < 30) riskLevelstr = "Very High";
        else riskLevelstr = "Extremely High";
        return riskLevelstr;
    }

    private void setUpSuggestion(int mDriverLevel, String riskStr) {
        String driverS = "You are Level " + mDriverLevel + " driver";
        SpannableString sDriverS = new SpannableString(driverS);
        sDriverS.setSpan(new RelativeSizeSpan(1.3f), 8, 15, 0);
        int suggestLevel = 0;
        List<String> riskLevelsStr = Arrays.asList("Low",
                "Medium",
                "High",
                "Very High",
                "Extremely High");
        tvSuggestDriverLv.setText(sDriverS);
        if (mDriverLevel == 4) {
            suggestLevel = 4;
        } else if (mDriverLevel == 3) {
            if (new HashSet<String>(riskLevelsStr.subList(0, 2)).contains(riskStr))
                suggestLevel = 4;
            else
                suggestLevel = 3;
        } else if (mDriverLevel == 2) {
            if (new HashSet<String>(riskLevelsStr.subList(0, 1)).contains(riskStr))
                suggestLevel = 4;
            else if (riskStr.equals(riskLevelsStr.get(2)))
                suggestLevel = 3;
            else
                suggestLevel = 2;
        } else if (mDriverLevel == 1) {
            if (riskStr.equals(riskLevelsStr.get(0)))
                suggestLevel = 4;
            else if (riskStr.equals(riskLevelsStr.get(1)))
                suggestLevel = 3;
            else if (riskStr.equals(riskLevelsStr.get(2)))
                suggestLevel = 2;
            else if (new HashSet<String>(riskLevelsStr.subList(3, 4)).contains(riskStr))
                suggestLevel = 1;
        }

        switch (suggestLevel) {

            case 4:

                ivFaceSentiment.setImageResource(R.drawable.baseline_sentiment_very_satisfied_black_48);
                ivFaceSentiment.setColorFilter(getResources().getColor(R.color.colorGreen));
                tvGoOutSuggest.setText("The learner driver has already driven " + String.format("%.0f", totalHours)
                                      + " hours and now is a good time to train.");
                btnGoToMap.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                d.cgangePer(0.06f);
                break;
            case 3:
                ivFaceSentiment.setImageResource(R.drawable.baseline_sentiment_satisfied_black_48);
                ivFaceSentiment.setColorFilter(getResources().getColor(R.color.colorYellow));
                tvGoOutSuggest.setText("The learner driver has driven for " + String.format("%.0f", totalHours)
                                 + " hours and now is a good time to train however be cautious.");
                btnGoToMap.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                btnGoToMap.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                d.cgangePer(0.30f);
                break;
            case 2:
                ivFaceSentiment.setImageResource(R.drawable.baseline_sentiment_dissatisfied_black_48);
                ivFaceSentiment.setColorFilter(getResources().getColor(R.color.colorOrange));
                tvGoOutSuggest.setText("The learner driver has only " + String.format("%.0f", totalHours)
                                    + " hours of experience and it is not a suitable time to train");
                btnGoToMap.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                d.cgangePer(0.74f);
                break;
            case 1:
                ivFaceSentiment.setImageResource(R.drawable.baseline_sentiment_very_dissatisfied_black_48);
                ivFaceSentiment.setColorFilter(getResources().getColor(R.color.colorRed));
                tvGoOutSuggest.setText("The learner driver has limited driving experience so we strongly " +
                                       "suggest you not drive out at this time");
                btnGoToMap.setBackgroundColor(getResources().getColor(R.color.colorRed));
                d.cgangePer(0.97f);
                break;
            default:
                break;
        }
    }

    private void setUpBalloons() {
        balloonDashBoard = new Balloon.Builder(getContext())
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
                .setText("This Risk Guage shows the risk at the current moment for your learner driver to drive in rainy conditions.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balloonDashBoard.showAlignTop(d, 0, 55);
            }
        });

        balloonCurSituation = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.8f)
                .setPadding(10)
                .setTextSize(17f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("This part shows information about the current situation.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        balloonSuggestion = new Balloon.Builder(getContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.8f)
                .setPadding(10)
                .setTextSize(17f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("This is the suggestion part which tells you if it is a good time to train your learner driver.")
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        final Balloon balloonWelcome = new Balloon.Builder(getContext())
                .setArrowVisible(false)
                .setWidthRatio(1f)
                .setMargin(5)
                .setPadding(10)
                .setTextSize(20f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("This is the Risk Indicator page where you can see the current situation and suggestions about favorable time for training.")
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

//        balloonDashBoard
        balloonWelcome
                .relayShowAlignTop(balloonDashBoard, d)
                .relayShowAlignTop(balloonCurSituation, materialCardViewCur)
                .relayShowAlignTop(balloonSuggestion, materialCardViewSuggest);


        balloonWelcome.show(getActivity().getWindow().getDecorView().getRootView());
        savePrefsDataSuitBalloon();

    }

    private boolean restorePrefDataSuitBalloon() {
        SharedPreferences pref = getContext().getSharedPreferences("SuitPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isSuitBalloonOpened", false);
        return isIntroActivityOpnendBefore;
    }


    private void savePrefsDataSuitBalloon() {
        SharedPreferences pref = getContext().getSharedPreferences("SuitPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isSuitBalloonOpened", true);
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void locationEnable () {
        LocationManager lm = (LocationManager)
                getActivity().getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }

        if (!gps_enabled ) {
            new AlertDialog.Builder(getContext() )
                    .setMessage( "please turn on your location in the settings" )
                    .setPositiveButton( "OK" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton( "Cancel" , null )
                    .show() ;
        }
    }

}




