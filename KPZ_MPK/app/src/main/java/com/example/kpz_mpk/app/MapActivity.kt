package com.example.kpz_mpk.app

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.kpz_mpk.R
import com.example.kpz_mpk.app.core.BaseActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : BaseActivity(), OnMapReadyCallback,  GoogleMap.OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    // Acceptable distance from city center to still default to user location
    private val TOO_FAR_METRES: Int = 20000

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var centerLatLang: LatLng
    private lateinit var centerText: TextView
    private  lateinit var reportButton: Button
    private  lateinit var confirmButton: Button
    private  lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        centerText = findViewById<TextView>(R.id.centerTextView)
        centerText.visibility = View.INVISIBLE

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        reportButton = findViewById<Button>(R.id.report_button_id)
        confirmButton = findViewById<Button>(R.id.confirm_button_id)
        cancelButton = findViewById<Button>(R.id.cancel_button_id)


        reportButton?.setOnClickListener() {
            centerText.visibility = View.VISIBLE

            reportButton.visibility = View.INVISIBLE

            confirmButton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE

            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f), 1000, null)
            mMap.uiSettings.isZoomGesturesEnabled = false;
        }


        confirmButton?.setOnClickListener() {

            mMap.addMarker(MarkerOptions()
                .position(mMap.cameraPosition.target)
                .title("Accident"))

            centerText.visibility = View.INVISIBLE

            reportButton.visibility = View.VISIBLE

            confirmButton.visibility = View.INVISIBLE
            cancelButton.visibility = View.INVISIBLE
        }


        cancelButton?.setOnClickListener() {

            centerText.visibility = View.INVISIBLE

            reportButton.visibility = View.VISIBLE

            confirmButton.visibility = View.INVISIBLE
            cancelButton.visibility = View.INVISIBLE

            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null)
            mMap.uiSettings.isZoomGesturesEnabled = true;
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
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

        mMap.setOnCameraMoveListener {
            centerLatLang = mMap.cameraPosition.target
            centerText.text = centerLatLang.toString()
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