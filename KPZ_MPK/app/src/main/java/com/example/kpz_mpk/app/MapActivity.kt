package com.example.kpz_mpk.app

import android.os.Bundle
import com.example.kpz_mpk.R
import com.example.kpz_mpk.app.core.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class MapActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Move the camera to a default location - Wroclaw's Main Square
        val defaultLocation = LatLng(51.109897, 17.032752)
        val defaultZoom= 14.0f

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))
        mMap.uiSettings.isRotateGesturesEnabled = false;
    }
}