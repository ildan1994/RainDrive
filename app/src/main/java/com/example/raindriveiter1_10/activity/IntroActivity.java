package com.example.raindriveiter1_10.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.raindriveiter1_10.R;
import com.example.raindriveiter1_10.model.IntroScreenItems;
import com.example.raindriveiter1_10.utility.IntroViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private IntroViewPagerAdapter introViewPagerAdapter ;
    private TabLayout tabIndicator;
    private Button btnNext;
    private int position = 0 ;
    private Button btnGetStarted;
    private Animation btnAnim ;
    static public final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);


        if (restorePrefData()) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                continueToMainActivity();
            } else {
                requestPermissions(permissions,4);
            }
        }




        setContentView(R.layout.layout_intro_main);
        getSupportActionBar().hide();




        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        final List<IntroScreenItems> mList = new ArrayList<>();
        mList.add(new IntroScreenItems("Welcome to RainDrive","We are dedicated to help you train the learners driver better during rainfall condition.",R.drawable.intro_slide_1));
        mList.add(new IntroScreenItems("Get Notified","Get notification about the right time to train your child during rainfall.",R.drawable.intro_slide_2));
        mList.add(new IntroScreenItems("Know Your Risk","Know the risk involved before you plan to take your child for a drive in a rainy day.",R.drawable.intro_slide_3));
        mList.add(new IntroScreenItems("Digital Logbook","Record your driving history in a single click.",R.drawable.intro_slide_4));


        screenPager =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size()-1) { // when we rech to the last screen
                    // TODO : show the GETSTARTED Button and hide the indicator and the next button
                    loaddLastScreen();
                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDisclaimerDialog();
            }
        });



    }

    private void openDisclaimerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_disclaimer_dialog);
        Button btnAccept = dialog.findViewById(R.id.btn_accept);
        Button btnDecline = dialog.findViewById(R.id.btn_decline);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    continueToMainActivity();
                } else {
                    requestPermissions(permissions,3);
                }
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IntroActivity.this, "You need the accept to continue.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        dialog.show();
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout((int)(displayRectangle.width()),(int)(displayRectangle.height()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 3) {
            if (!contains(grantResults,PackageManager.PERMISSION_DENIED)) {
                continueToMainActivity();
            } else {
                Toast.makeText(this, "This app need require location permission to work properly, you can turn on the permission for this app in the setting and restart the app", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 4) {
            if (!contains(grantResults,PackageManager.PERMISSION_DENIED)) {
                continueToMainActivity();
            } else {
                Toast.makeText(this, "This app need require location permission to work properly, you can turn on the permission for this app in the setting and restart the app", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();
    }

    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);
    }

    private void continueToMainActivity() {
        Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(mainActivity);
        savePrefsData();
        finish();
    }

    public static boolean contains(final int[] array, final int v) {
        boolean result = false;
        for(int i : array){
            if(i == v){
                result = true;
                break;
            }
        }
        return result;
    }
}