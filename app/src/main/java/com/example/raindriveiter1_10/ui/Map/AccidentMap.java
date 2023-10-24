package com.example.raindriveiter1_10.ui.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androchef.happytimer.countdowntimer.DynamicCountDownView;
import com.androchef.happytimer.countdowntimer.HappyTimer;
import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.model.Accident;
import com.example.raindriveiter1_10.model.DrivingRecord;
import com.example.raindriveiter1_10.model.Marker;
import com.example.raindriveiter1_10.model.TipInfo;
import com.example.raindriveiter1_10.networkConnection.BackendAPI;
import com.example.raindriveiter1_10.ui.SuitabilityIndicator.SuitabilityIndicator;
import com.example.raindriveiter1_10.utility.TipHelper;
import com.example.raindriveiter1_10.utility.JsonConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayCircle;
import com.skydoves.balloon.overlay.BalloonOverlayRect;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AccidentMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<Marker> mClusterManager;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private BackendAPI backendAPI;
    private Location mLocation;
    private Location originLocation;
    private LinearLayout linearLayout;
    LocationRequest locationRequest;
//    private View rootView;
    private TipInfo curTip;
    private Toast backToast;
    private DynamicCountDownView dynamicCountDownView;
    private LocationCallback locationCallback;
    private Button btnStart;
    private List<LatLng> track;
    private TextView tvTipText;
    private Dialog finishDialog;
    private float TotalDistance;
    private CardView cvTip;
    private int completeSec;
    private Polyline polyline;
    private CardView cardView;
    private long backPressedTime;
    private Boolean started;
    private TextView tvDrivingDistance;
    private LinearLayout linearLayoutMapInfo;
    private ProgressBar pbTipTimer;
    Handler handler = new Handler();
    Runnable runnable = null;
    final int delay = 30000;
    private LocalTime startTime;
    private int topInsect;
    private LocalTime endTime;
    private String[] startEndLoc;
    private static final int COLOR_GREEN = 0x8800FA9A;
    private Balloon balloonTip;

    private Iterator<TipInfo> iterator;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map_container), new OnApplyWindowInsetsListener() {

            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                CardView cardView = findViewById(R.id.cv_tip_map);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
                topInsect = insets.getSystemWindowInsetTop();
                layoutParams.setMargins(0, insets.getSystemWindowInsetTop(), 0, 0);
                cardView.setLayoutParams(layoutParams);

                return insets.consumeSystemWindowInsets();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        track = new ArrayList<LatLng>();
        started = false;
        finishDialog = new Dialog(this);


        btnStart = findViewById(R.id.btn_start_driving);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        cardView = findViewById(R.id.cv_map_control);
        cvTip = findViewById(R.id.cv_tip_map);
        Intent intent = getIntent();
        dynamicCountDownView = findViewById(R.id.circularCountDownView);
        linearLayoutMapInfo = findViewById(R.id.ll_map_info);
        dynamicCountDownView.initTimer(86399, HappyTimer.Type.COUNT_UP);

        tvDrivingDistance = findViewById(R.id.tv_driving_distance);
        tvTipText = findViewById(R.id.tv_tip_text);
        pbTipTimer = findViewById(R.id.pb_tip);
        dynamicCountDownView.setTimerType(HappyTimer.Type.COUNT_UP);

        //dynamicCountDownView.setCustomBackgroundDrawable(null);

        dynamicCountDownView.setOnTickListener(new HappyTimer.OnTickListener() {
            @Override
            public void onTick(int i, int i1) {
                completeSec = i;
            }

            @Override
            public void onTimeUp() {
                Toast.makeText(getApplicationContext(), "duration goal reached", Toast.LENGTH_SHORT).show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (!started) {
                    dynamicCountDownView.startTimer();
                    btnStart.setText("Finish");
                    btnStart.setBackground(getResources().getDrawable(R.drawable.custom_button_secondary));
                    //btnStart.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    startTime = LocalTime.now();
                    started = true;
                } else {
                    started = false;
                    dynamicCountDownView.stopTimer();
                    endTime = LocalTime.now();
                    LocalTime time = LocalTime.ofSecondOfDay(completeSec);
                    openFinishDialog(TotalDistance, time);
                }
            }
        });
        backendAPI = new BackendAPI();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.i("map", "onLocationResult: null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    //mLocation = location;
                    if (started) {
                        track.add(new LatLng(location.getLatitude(), location.getLongitude()));
                        polyline = mMap.addPolyline(new PolylineOptions()
                                .startCap(new RoundCap())
                                .endCap(new RoundCap())
                                .color(COLOR_GREEN)
                                .jointType(1)
                                .width(30)
                                .clickable(false)
                                .addAll(track));
                        TotalDistance = calculateRouteDistance(polyline.getPoints());
                        tvDrivingDistance.setText(TotalDistance + " Km");
                    }
                    if (originLocation != null) {
                        Log.i("Map", "" + originLocation.getLongitude());
                        Log.i("Map", "origin location latitude " + originLocation.getLatitude());
                        Log.i("Map", "mLocation latitude " + mLocation.getLatitude());
                    }
                    if (originLocation != null) {

                        if (Math.abs(originLocation.getLongitude() - location.getLongitude()) > 0.003 || Math.abs(originLocation.getLatitude() - location.getLatitude()) > 0.003) {
                            originLocation = location;
                            Log.i("map", "onLocationResult: location different");
                            //mMap.clear();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            LocationAccidentsTask locationAccidentsTask = new LocationAccidentsTask();
                            locationAccidentsTask.execute(location.getLongitude(), location.getLatitude());
                        }
                    }
                }
            }
        };

        createLocationRequest();
        startLocationUpdates();
        origin location longitude

        final TipHelper tipHelper = new TipHelper();
        tipHelper.initializeTips();
        iterator = tipHelper.getTips().iterator();
        pbTipTimer.setMax(1000);
        handler.postDelayed(runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                iterator = setUpTip(tipHelper, iterator);
                tvTipText.setText(curTip.getTipTextSimple());
                pbTipTimer.setProgress(0);
                ObjectAnimator.ofInt(pbTipTimer, "progress", 1000)
                        .setDuration(30000)
                        .start();
               // setUpProgressTimer();
                setupTipBalloon();

            }
        }, 0);


        cvTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balloonTip.showAlignBottom(cvTip, 0, 20);
            }
        });


        setUpIntroBalloons();


    }

    private void setUpProgressTimer() {



    }

    private void setupTipBalloon() {
        balloonTip = new Balloon.Builder(getApplicationContext())
                .setLayout(R.layout.layout_custom_balloon)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setCornerRadius(16f)
                .setElevation(6)
                .setArrowVisible(true)
                .setAlpha(0.9f)
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlueTransparent))
                .setDismissWhenTouchOutside(true)
                .setBalloonAnimation(BalloonAnimation.CIRCULAR)
                .setLifecycleOwner(this)
                .build();
        TextView textView = balloonTip.getContentView().findViewById(R.id.tv_tip_detail_balloon);
        textView.setText(curTip.getTipTextExplained());
        ImageView imageView = balloonTip.getContentView().findViewById(R.id.iv_tip_balloon);
        imageView.setImageResource(curTip.getTipDrawable());
    }
    private void setUpIntroBalloons() {
        Balloon balloonIntroTip = new Balloon.Builder(getApplicationContext())
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setArrowVisible(true)
                .setWidthRatio(0.8f)
                .setPadding(10)
                .setTextSize(17f)
                .setShowTime(1)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("Driving tips are shown here. Now you can teach your learner driver while driving. Tips changes every 30 seconds. Click them to see more information.")
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setIsVisibleOverlay(true)
                .setOverlayColorResource(R.color.overlay)
                .setOverlayPadding(6f)
                .setOverlayShape(BalloonOverlayRect.INSTANCE)
                .setLifecycleOwner(this)
                .build();

        Balloon balloonIntroDriveControl = new Balloon.Builder(getApplicationContext())
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
                .setText("Start your trip by clicking start button and save the trip to the logbook for future reference.")
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setIsVisibleOverlay(true)
                .setOverlayColorResource(R.color.overlay)
                .setOverlayPadding(6f)
                .setOverlayShape(BalloonOverlayRect.INSTANCE)
                .setLifecycleOwner(this)
                .build();

        final Balloon balloonWelcome = new Balloon.Builder(getApplicationContext())
                .setArrowVisible(false)
                .setWidthRatio(1f)
                .setMargin(5)
                .setPadding(10)
                .setTextSize(20f)
                .setCornerRadius(16f)
                .setAlpha(0.9f)
                .setText("This map page can give you warnings when entering an accident-prone area, tips for driving in rainy conditions, and also can save trip information.")
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite))
                .setTextIsHtml(false)
                .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                .setIsVisibleOverlay(true)
                .setOverlayColorResource(R.color.overlay)
                .setOverlayPadding(6f)
                .setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
                .setOverlayShape(new BalloonOverlayCircle(0))
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setLifecycleOwner(this)
                .build();
        balloonWelcome
                .relayShowAlignBottom(balloonIntroTip,cvTip)
                .relayShowAlignTop(balloonIntroDriveControl, cardView);


            balloonWelcome.show(this.getWindow().getDecorView().getRootView());

            //savePrefsDataMapBalloon();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Iterator<TipInfo> setUpTip(TipHelper tipHelper, Iterator<TipInfo> iterator) {
        if (iterator.hasNext()) {
            TipInfo tipInfo = iterator.next();
            curTip = tipInfo;
//            tipInfo.setUpTip(tvTipText, ivToolTip);
        } else {
            iterator = tipHelper.getTips().iterator();
            setUpTip(tipHelper, iterator);
        }
        return iterator;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        linearLayout = findViewById(R.id.ll_map_control);
        mMap.setPadding(0, topInsect + cvTip.getHeight(), 0, linearLayout.getHeight());
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mClusterManager = new ClusterManager<Marker>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("map", "onSuccess:get current location ");

                mLocation = location;
                originLocation = location;


                LocationAccidentsTask locationAccidentsTask = new LocationAccidentsTask();
                double longitude = mLocation.getLongitude();
                System.out.println("Longitude is " + longitude);
                double latitude = mLocation.getLatitude();
                locationAccidentsTask.execute(longitude, latitude);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("map", "onFailure: did not get current location");
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                alertDialog.setTitle("We do not get current location");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
            }
        });
    }


    protected class LocationAccidentsTask extends AsyncTask<Double, Void, List<Accident>> {

        @Override
        protected List<Accident> doInBackground(Double... params) {

            mClusterManager.clearItems();

            List<Accident> results = new ArrayList<>();
            Log.i("map", "doInBackground: "+ "Async task is executed " + params[0]);
            String accidents = backendAPI.getAccidentByLocation(params[0], params[1]);
            System.out.println(accidents);
            results = JsonConverter.accidentsByLocation(accidents);
            return results;
        }

        @Override
        protected void onPostExecute(List<Accident> accidents) {
            final AlertDialog alertDialog1 = new AlertDialog
                    .Builder(AccidentMap.this)
                    .create();

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.alertdialog_layout, null);
            TextView textView = layout.findViewById(R.id.alertTitle);
            if (accidents.size() == 0) {
                textView.setText("Currently there is no accidents around you");
            } else {
                textView.setText("You are currently in an accident prone area, Please slow down");
            }

            alertDialog1.setView(layout);

            if(started) alertDialog1.show();
            //alertDialog1.show();
            //Window window = alertDialog1.getWindow();
            alertDialog1.getWindow().setGravity(Gravity.CENTER);
            //
            Runnable dismissRun = new Runnable() {
                @Override
                public void run() {
                    if (alertDialog1 != null)
                        alertDialog1.dismiss();
                }
            };
            new Handler().postDelayed(dismissRun, 4000);

            if (accidents.size() < 10 && accidents.size() > 0) {
                layout.setBackgroundColor(Color.rgb(227, 30, 36));
            } else if (accidents.size() == 0) {
                layout.setBackgroundColor(Color.rgb(155, 242, 165));
            } else {
                layout.setBackgroundColor(Color.RED);
            }

            Log.i("map", "onPostExecute: Accidents size " + accidents.size());
            for (Accident accident : accidents) {
                if (accident.getInjuryLevel() == 1) {
                    Marker accidentMarker = new Marker(accident.getLatitude(), accident.getLongitude(), "Fatal Accident", "Injury level is: " + Integer.toString(accident.getInjuryLevel()));
                    mClusterManager.addItem(accidentMarker);
                } else if (accident.getInjuryLevel() == 2) {
                    Marker accidentMarker = new Marker(accident.getLatitude(), accident.getLongitude(), "Severe Accident", "Injury level is: " + Integer.toString(accident.getInjuryLevel()));
                    mClusterManager.addItem(accidentMarker);
                } else if (accident.getInjuryLevel() == 3) {
                    Marker accidentMarker = new Marker(accident.getLatitude(), accident.getLongitude(), "Minor Accident", "Injury level is: " + Integer.toString(accident.getInjuryLevel()));
                    mClusterManager.addItem(accidentMarker);
                }

            }

            mClusterManager.cluster();
        }


    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
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

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private float calculateRouteDistance(List<LatLng> routes) {
        float totalDistance = 0.0f;
        for (int i = 0; i < routes.size() - 1; i++) {
            totalDistance += distance(
                    routes.get(i).latitude,
                    routes.get(i).longitude,
                    routes.get(i + 1).latitude,
                    routes.get(i + 1).longitude,
                    "K"
            );
        }
        BigDecimal bd = new BigDecimal(totalDistance).setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        PictureInPictureParams pipp = new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(1, 1))
                .build();

        enterPictureInPictureMode(pipp);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {

        if (isInPictureInPictureMode) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            cardView.setVisibility(View.INVISIBLE);
            cvTip.setVisibility(View.INVISIBLE);

        } else {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            cardView.setVisibility(View.VISIBLE);
            cvTip.setVisibility(View.VISIBLE);
        }


        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {

        PictureInPictureParams pipp = new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(1, 1))
                .build();

        enterPictureInPictureMode(pipp);
        super.onUserLeaveHint();
    }

//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
//
////        if (isInPictureInPictureMode)
//
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openFinishDialog(final float drivingDist, LocalTime time) {
        finishDialog.setContentView(R.layout.win_layout_diaglog_finshed);
        finishDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnAddToLog = finishDialog.findViewById(R.id.btn_add_to_log_dia);
        TextView tvDrivingDist = finishDialog.findViewById(R.id.tv_driving_dist_dia);
        TextView tvDrivingTime = finishDialog.findViewById(R.id.tv_driving_time_dia);

        tvDrivingDist.setText(drivingDist + " Km");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        tvDrivingTime.setText(time.format(dateTimeFormatter));
        if (track.size() != 0) {
            LatLng startLatlng = track.get(0);
            LatLng endLatlng = track.get(track.size() - 1);
            new GetLocationByLatLongTask().execute(
                    String.valueOf(startLatlng.latitude),
                    String.valueOf(startLatlng.longitude),
                    String.valueOf(endLatlng.latitude),
                    String.valueOf(endLatlng.longitude)
            );
        }
        btnAddToLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvStartLoc = finishDialog.findViewById(R.id.tv_start_loc);
                TextView tvEndLoc = finishDialog.findViewById(R.id.tv_end_loc);
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
                DrivingRecord drivingRecord = new DrivingRecord(
                        new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                        startTime.format(formatter),
                        endTime.format(formatter),
                        drivingDist,
                        SuitabilityIndicator.riskLevel,
                        tvStartLoc.getText().toString(),
                        tvEndLoc.getText().toString(),
                        MainActivity.weatherIconStr);
                MainActivity.drivingRecordViewModel.insert(drivingRecord);
                Toast.makeText(getApplicationContext(), "driving record added to log book", Toast.LENGTH_SHORT).show();
//                AnimatedBottomBar animatedBottomBar = findViewById(R.id.animated_bottom_bar);
//                //animatedBottomBar.selectTabById(R.id.log_book, true);
                finishDialog.dismiss();
                setResult(RESULT_OK);
                finish();


            }
        });

        Button btnClose = finishDialog.findViewById(R.id.btn_close_dia);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog.dismiss();
                finish();
            }
        });
        finishDialog.show();

        ;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

    private class GetLocationByLatLongTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String startLocJson = MainActivity.googleAPI.getLocalNameByLatLng(params[0], params[1]);
            String startLoc = JsonConverter.locationDetail(startLocJson).get("locality");
            String endLocJson = MainActivity.googleAPI.getLocalNameByLatLng(params[2], params[3]);
            String endLoc = JsonConverter.locationDetail(endLocJson).get("locality");

            return new String[]{startLoc, endLoc};
        }

        @Override
        protected void onPostExecute(String[] params) {
            TextView tvStartLoc = finishDialog.findViewById(R.id.tv_start_loc);
            tvStartLoc.setText(params[0]);
            TextView tvEndLoc = finishDialog.findViewById(R.id.tv_end_loc);
            tvEndLoc.setText(params[1]);
        }

    }

//    private boolean restorePrefDataMapBalloon() {
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MapPrefs", MODE_PRIVATE);
//        Boolean isBalloonsOpnendBefore = pref.getBoolean("isMapBalloonOpened", false);
//        return isBalloonsOpnendBefore;
//    }
//
//
//    private void savePrefsDataMapBalloon() {
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MapPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putBoolean("isMapBalloonOpened", true);
//        editor.commit();
//    }




}