package com.example.raindriveiter1_10.ui.SuitabilityIndicator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raindriveiter1_10.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class RiskGaugeRoot extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SuitabilityIndicator suitabilityIndicator;
    private RiskGuageAbout riskGuageAbout;


    public RiskGaugeRoot() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        suitabilityIndicator = new SuitabilityIndicator();
        riskGuageAbout = new RiskGuageAbout();
        final View root = inflater.inflate(R.layout.fragment_risk_gauge_root, container, false);
        viewPager = root.findViewById(R.id.si_viewpager);
        tabLayout = root.findViewById(R.id.si_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(),0);
        myViewPagerAdapter.addFragment(suitabilityIndicator,"Risk Gauge");
        myViewPagerAdapter.addFragment(riskGuageAbout,"About");




        viewPager.setAdapter(myViewPagerAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_check_risk);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_explain);
        return root;
    }

    private class MyViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();
        public MyViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
            //return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}