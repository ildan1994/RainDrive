package com.example.raindriveiter1_10.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.networkConnection.BackendAPI;
import com.example.raindriveiter1_10.networkConnection.GoogleAPI;
import com.example.raindriveiter1_10.networkConnection.WeatherAPI;
import com.example.raindriveiter1_10.ui.DrivingLogBook.DrivingRecordFragment;
import com.example.raindriveiter1_10.ui.Settings.SettingsRootFragment;
import com.example.raindriveiter1_10.ui.SuitabilityIndicator.RiskGaugeRoot;
import com.example.raindriveiter1_10.ui.home.HomeFragment;
import com.example.raindriveiter1_10.viewmodel.DrivingRecordViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AnimatedBottomBar animatedBottomBar;
    private FragmentManager fragmentManager;
    private Toast backToast;
    static public final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private long backPressedTime;

    static public boolean hasPlayedHomeBal = false;
    static public boolean hasPlayedSuitBal = false;
    static public boolean hasPlayedMapBal = false;
    static public String latitude;
    static public String longitude;
    static public WeatherAPI weatherAPI;
    static public BackendAPI backendAPI;
    static public GoogleAPI googleAPI;
    static public Toolbar toolbar;
    static public String weatherIconStr;
    public static DrivingRecordViewModel drivingRecordViewModel;
    public FusedLocationProviderClient mFusedLocationProviderClient;

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        setContentView(R.layout.activity_main);
//        locationEnable();
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("Home");
        toolbar.setLogo(R.mipmap.ic_launcher_round);
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayShowTitleEnabled(false);
        drivingRecordViewModel = new ViewModelProvider(this).get(DrivingRecordViewModel.class);
        drivingRecordViewModel.initializeVars(this.getApplication());
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //MainActivity.drivingRecordViewModel.deleteAll();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        weatherAPI = new WeatherAPI();
        backendAPI = new BackendAPI();
        googleAPI = new GoogleAPI();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            getUserPermissions();
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
            }
        });
        animatedBottomBar = findViewById(R.id.animated_bottom_bar);

        if (savedInstanceState == null) {
            locationEnable();
            animatedBottomBar.selectTabById(R.id.home, true);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }


        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab tab, int newIndex, @NotNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment = null;

                switch (newTab.getId()) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.check_suitability:
                        fragment = new RiskGaugeRoot();
                        // fragment = new SuitabilityIndicatorFragment();
                        break;
                    case R.id.log_book:
                        fragment = new DrivingRecordFragment();
                        break;
                    case R.id.more:
                        fragment = new SettingsRootFragment();
                        break;
                }
                if (fragment != null) {
                    locationEnable();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                            .commit();
                } else {
                    Log.e(TAG, "Error in creating Fragment");
                }
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }




    public void getUserPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(permissions, 0);
            }
        }
    }


    @Override
    public void onBackPressed() {
        fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping back stack");
            fragmentManager.popBackStack();
        }else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
                return;
            } else {
                backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();

        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            recreate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {

        if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "back from log book", Toast.LENGTH_SHORT).show();
                animatedBottomBar.selectTabById(R.id.log_book, true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void locationEnable () {
        LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
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
            new AlertDialog.Builder(MainActivity. this )
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