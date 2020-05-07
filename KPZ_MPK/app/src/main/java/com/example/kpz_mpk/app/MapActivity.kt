package com.example.kpz_mpk.app

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.kpz_mpk.R
import com.example.kpz_mpk.app.core.BaseActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : BaseActivity(), OnMapReadyCallback,  GoogleMap.OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    // Acceptable distance from city center to still default to user location
    private val TOO_FAR_METRES: Int = 20000

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()

        mMap.isMyLocationEnabled = true

        // Move the camera to a default location - Wroclaw's Main Square
        lateinit var lastLocation: Location
        var defaultLocation = LatLng(51.109897, 17.032752)
        var defaultZoom= 14.0f

        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Wroclaw"))

        mMap.uiSettings.isRotateGesturesEnabled = false;
        mMap.uiSettings.isZoomControlsEnabled = false;

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null ) {
                lastLocation = location
                val currentLocation = LatLng(location.latitude, location.longitude)
                if (!(tooFar(defaultLocation, currentLocation)))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, defaultZoom))
                else
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))
            }
        }

        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }

    private fun tooFar(latLngFrom: LatLng, latLngTo: LatLng): Boolean{
        val results = FloatArray(1)

        Location.distanceBetween(latLngFrom.latitude, latLngFrom.longitude,
            latLngTo.latitude, latLngTo.longitude,
            results);

        return results[0] > TOO_FAR_METRES
    }

}