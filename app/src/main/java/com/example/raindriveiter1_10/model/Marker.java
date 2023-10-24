package com.example.raindriveiter1_10.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Marker implements ClusterItem {

    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;

//    public Marker(double lat, double lng)
//    {
//        mPosition = new LatLng(lat, lng);
//    }

    public Marker(double lat, double lng, String title, String snippet)
    {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
